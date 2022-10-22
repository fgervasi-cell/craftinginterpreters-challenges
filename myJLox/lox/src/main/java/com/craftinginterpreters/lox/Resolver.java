package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import com.craftinginterpreters.lox.Expr.Assign;
import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Call;
import com.craftinginterpreters.lox.Expr.Comma;
import com.craftinginterpreters.lox.Expr.Grouping;
import com.craftinginterpreters.lox.Expr.Lambda;
import com.craftinginterpreters.lox.Expr.Literal;
import com.craftinginterpreters.lox.Expr.Logical;
import com.craftinginterpreters.lox.Expr.Ternary;
import com.craftinginterpreters.lox.Expr.Unary;
import com.craftinginterpreters.lox.Expr.Variable;
import com.craftinginterpreters.lox.Stmt.Block;
import com.craftinginterpreters.lox.Stmt.Break;
import com.craftinginterpreters.lox.Stmt.Expression;
import com.craftinginterpreters.lox.Stmt.Function;
import com.craftinginterpreters.lox.Stmt.If;
import com.craftinginterpreters.lox.Stmt.Print;
import com.craftinginterpreters.lox.Stmt.Return;
import com.craftinginterpreters.lox.Stmt.Var;
import com.craftinginterpreters.lox.Stmt.While;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void>
{
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private final Map<Token, Boolean> usages = new HashMap<>();
    private FunctionType currentFunction = FunctionType.NONE;
    private boolean insideLoop = false;

    Resolver(Interpreter interpreter)
    {
        this.interpreter = interpreter;
    }

    private enum FunctionType
    {
        NONE,
        FUNCTION;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) 
    {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) 
    {
        declare(stmt.name);
        define(stmt.name);
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) 
    {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) 
    {
        if (currentFunction == FunctionType.NONE)
            Lox.error(stmt.keyword, "Can't return from top-level code.");
        if (stmt.value != null)
            resolve(stmt.value);
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) 
    {
        declare(stmt.name);
        if (stmt.initializer != null)
            resolve(stmt.initializer);
        define(stmt.name);
        usages.put(stmt.name, false);
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) 
    {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) 
    {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null)
            resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) 
    {
        boolean temp = insideLoop;
        insideLoop = true;
        resolve(stmt.condition);
        resolve(stmt.body);
        insideLoop = temp;
        return null;
    }

    @Override
    public Void visitBreakStmt(Break stmt) 
    {
        if (!insideLoop)
            Lox.error(stmt.position, "Cannot break outside of a loop.");
        return null;
    }

    @Override
    public Void visitCommaExpr(Comma expr) 
    {
        for (Expr argument : expr.exprs)
        {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitBinaryExpr(Binary expr) 
    {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitTernaryExpr(Ternary expr) 
    {
        resolve(expr.left);
        resolve(expr.inner);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Call expr) 
    {
        resolve(expr.callee);
        for (Expr argument : expr.arguments)
        {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitGroupingExpr(Grouping expr) 
    {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Literal expr) 
    {
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable expr) 
    {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE)
            Lox.error(expr.name, "Can't read local variable in its own initializer.");

        resolveLocal(expr, expr.name);
        usages.put(expr.name, true);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Unary expr) 
    {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical expr) 
    {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitAssignExpr(Assign expr) 
    {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitLambdaExpr(Lambda expr) 
    {
        resolveFunction(expr.functionStmt, FunctionType.FUNCTION);
        return null;
    }

    void resolve(List<Stmt> statements)
    {
        for (Stmt statement : statements)
        {
            resolve(statement);
        }
        checkUsages();
    }

    private void resolve(Stmt statement)
    {
        statement.accept(this);
    }

    private void resolve(Expr expression)
    {
        expression.accept(this);
    }

    private void beginScope()
    {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope()
    {
        scopes.pop();
    }

    private void declare(Token name)
    {
        if (scopes.isEmpty())
            return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme))
            Lox.error(name, "Already a variable with this name in this scope.");
        scope.put(name.lexeme, false);
    }

    private void define(Token name)
    {
        if (scopes.isEmpty())
            return;

        scopes.peek().put(name.lexeme, true);
    }

    private void resolveLocal(Expr expr, Token name)
    {
        for (int i = scopes.size() - 1; i >= 0; i--)
        {
            if (scopes.get(i).containsKey(name.lexeme))
            {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void resolveFunction(Function function, FunctionType type)
    {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.params)
        {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    private void checkUsages()
    {
        for (Entry<Token, Boolean> entry : usages.entrySet())
        {
            if (!entry.getValue().booleanValue())
                Lox.error(entry.getKey(), "The variable '" + entry.getKey().lexeme + "' is never used.");
        }
    }
}
