package com.craftinginterpreters.lox;

import java.util.List;

import com.craftinginterpreters.lox.Expr.Assign;
import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Comma;
import com.craftinginterpreters.lox.Expr.Grouping;
import com.craftinginterpreters.lox.Expr.Literal;
import com.craftinginterpreters.lox.Expr.Logical;
import com.craftinginterpreters.lox.Expr.Ternary;
import com.craftinginterpreters.lox.Expr.Unary;
import com.craftinginterpreters.lox.Expr.Variable;
import com.craftinginterpreters.lox.Stmt.Block;
import com.craftinginterpreters.lox.Stmt.Expression;
import com.craftinginterpreters.lox.Stmt.If;
import com.craftinginterpreters.lox.Stmt.Print;
import com.craftinginterpreters.lox.Stmt.Var;
import com.craftinginterpreters.lox.Stmt.While;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>
{
    private Environment environment = new Environment();

    void interpret(List<Stmt> statements)
    {
        try 
        {
            for (Stmt statement : statements)
            {
                execute(statement);
            }
        }
        catch (RuntimeError error)
        {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitCommaExpr(Comma expr) 
    {
        for (int i = 0; i < expr.exprs.size() - 1; i++)
        {
            evaluate(expr.exprs.get(i));
        }
        return evaluate(expr.exprs.get(expr.exprs.size() - 1));
    }

    @Override
    public Object visitBinaryExpr(Binary expr) 
    {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type)
        {
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case BANG_EQUAL:
                return !isEqual(left, right);
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;
                if (left instanceof String && right instanceof String)
                    return (String)left + (String)right;
                if (left instanceof String && right instanceof Double)
                    return (String)left + String.valueOf(right);
                if (left instanceof Double && right instanceof String)
                    return String.valueOf(left) + (String)right;
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return (double)left - (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double)right == 0)
                {
                    throw new RuntimeError(expr.operator, "Division by zero.");
                }
                return (double)left / (double)right;
            default:
                return null;
        }
    }

    @Override
    public Object visitTernaryExpr(Ternary expr) 
    {
        Object condition = evaluate(expr.left);

        if (!(condition instanceof Boolean))
            throw new RuntimeError(expr.condition, "The left expression of a ternary operator" + 
                                   " must evaluate to a boolean value.");
        
        if ((boolean)condition)
        {
            return evaluate(expr.inner);
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) 
    {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) 
    {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Logical expr)
    {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR && isTruthy(left)
            || expr.operator.type == TokenType.AND && !isTruthy(left))
        {
            return left;
        }
        
        return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Unary expr) 
    {
        Object right = evaluate(expr.right);

        switch (expr.operator.type)
        {
            case MINUS:
                return -(double)right;
            case BANG:
                return !isTruthy(right);
            default:
                return null;
        }
    }

    @Override
    public Object visitVariableExpr(Variable expr)
    {
        return environment.get(expr.name);
    }
    
    @Override
    public Void visitExpressionStmt(Expression stmt) 
    {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt)
    {
        Object condition = evaluate(stmt.condition);
        if (isTruthy(condition))
        {
            execute(stmt.thenBranch);
        }
        else if (stmt.thenBranch != null)
        {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) 
    {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Var statement)
    {
        Object value = null;
        if (statement.initializer != null)
            value = evaluate(statement.initializer);

        environment.define(statement.name.lexeme, value);

        return null;
    }

    @Override
    public Void visitWhileStmt(While statement)
    {
        while (isTruthy(evaluate(statement.condition)))
        {
            execute(statement.body);
        }
        return null;
    }

    @Override
    public Object visitAssignExpr(Assign expr)
    {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public Void visitBlockStmt(Block stmt)
    {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    private Object evaluate(Expr expr)
    {
        return expr.accept(this);
    }

    private void execute(Stmt stmt)
    {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment env)
    {
        Environment previous = this.environment;
        try 
        {
            this.environment = env;

            for (Stmt statement : statements)
            {
                execute(statement);
            }
        }
        finally 
        {
            this.environment = previous;
        }
    }

    private boolean isTruthy(Object object)
    {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b)
    {
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;
        return a.equals(b);
    }

    private void checkNumberOperand(Token operator, Object operand)
    {
        if (operand instanceof Double)
            return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right)
    {
        if (left instanceof Double && right instanceof Double)
            return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private String stringify(Object value)
    {
        if (value == null)
            return "nil";
        if (value instanceof Double)
        {
            String text = value.toString();
            if (text.endsWith(".0"))
            {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return value.toString();
    }
}
