package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

public class Parser 
{
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    List<Stmt> parse()
    {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd())
        {
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration()
    {
        try 
        {
            if (match(VAR))
                return varDeclaration();
            if (match(FUN))
                return function("function");
            return statement();
        }
        catch (ParseError error)
        {
            synchronize();
            return null;
        }
    }

    private Stmt.Var varDeclaration()
    {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL))
        {
            initializer = expression();
        }
        consume(SEMICOLON, "Expected ';' after variable declaration.");

        return new Stmt.Var(name, initializer);
    }

    private Stmt.While whileStatement()
    {
        consume(LEFT_PAREN, "Expected '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected ')' after condition.");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    private Stmt breakStatement()
    {
        Token position = previous();
        consume(SEMICOLON, "Expected ';' after 'break'.");
        return new Stmt.Break(position);
    }

    private Stmt statement()
    {
        if (match(PRINT))
            return printStatement();
        if (match(LEFT_BRACE))
            return new Stmt.Block(block());
        if (match(IF))
            return ifStatement();
        if (match(WHILE))
            return whileStatement();
        if (match(FOR))
            return forStatement();
        if (match(BREAK))
            return breakStatement();
        if (match(RETURN))
            return returnStatement();
        
        return expressionStatement();
    }

    private Stmt forStatement()
    {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        Stmt initializer;
        if (match(SEMICOLON))
            initializer = null;
        else if (match(VAR))
            initializer = varDeclaration();
        else 
            initializer = expressionStatement();

        Expr condition = null;
        if (!check(SEMICOLON))
            condition = expression();
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!check(RIGHT_PAREN))
            increment = expression();

        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();

        if (increment != null)
            body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
        if (condition == null)
            condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);
        if (initializer != null)
            body = new Stmt.Block(Arrays.asList(initializer, body));

        return body;
    }

    private Stmt.If ifStatement()
    {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE))
            elseBranch = statement();
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt.Print printStatement()
    {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt.Return returnStatement()
    {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON))
            value = expression();
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt.Expression expressionStatement()
    {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Stmt.Function function(String kind)
    {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expected '(' after " + kind + " name.");
        List<Token> params = new ArrayList<>();
        if (!check(RIGHT_PAREN))
        {
            do 
            {
                if (params.size() >= 255)
                    error(peek(), "Can't have more than 255 parameters.");
                params.add(consume(IDENTIFIER, "Expect parameter name."));
            }
            while(match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, params, body);
    }

    private List<Stmt> block()
    {
        List<Stmt> statements = new ArrayList<>();
        while(!check(RIGHT_BRACE) && !isAtEnd())
        {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr expression()
    {
        return comma();
    }

    private Expr comma()
    {
        Expr expr = assignment();
        if (match(COMMA))
        {
            List<Expr> exprs = new ArrayList<>();
            exprs.add(expr);
            exprs.add(comma());
            while (match(COMMA))
            {
                exprs.add(comma());
            }
            return new Expr.Comma(exprs);
        }

        return expr;
    }

    private Expr assignment()
    {
        Expr expr = ternary();

        if (match(EQUAL))
        {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable)
            {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr ternary()
    {
        Expr expr = or();
        if (match(QUESTION_MARK))
        {
            Token condition = previous();
            Expr inner = equality();
            consume(COLON, "Expected ':' for ternary operator.");
            Expr right = ternary();
            expr = new Expr.Ternary(expr, inner, right, condition);
        }

        return expr;
    }

    private Expr or()
    {
        Expr expr = and();

        while (match(OR))
        {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and()
    {
        Expr expr = equality();

        while (match(AND))
        {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() 
    {
        if (match(BANG_EQUAL, EQUAL_EQUAL))
        {
            String error = "'==' and '!=' are binary operators and cannot appear" +
                            " at the beginnig of an expression.";
            error(peek(), error);
        }

        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL))
        {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() 
    {
        if (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL))
        {
            String error = "'<', '<=', '>' and '>=' are binary operators and cannot appear" +
                            " at the beginnig of an expression.";
            error(peek(), error);
        }

        Expr expr = term();
        while (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL))
        {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() 
    {
        if (match(PLUS, MINUS))
        {
            String error = "'+' and '-' are binary operators and connot appear"
                            + " at the beginning of an expression.";
            error(peek(), error);
        }

        Expr expr = factor();
        while (match(PLUS, MINUS))
        {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() 
    {
        if (match(SLASH, STAR))
        {
            String error = "'/' and '*' are binary operators and cannot appear at the beginning"
                            + " of an expression";
            error(peek(), error);
        }

        Expr expr = unary();
        while (match(SLASH, STAR))
        {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() 
    {
        if (match(PLUS))
        {
            error(peek(), "Unary '+' expressions are not supported.");
        }

        if (match(BANG, MINUS))
        {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return call();
    }

    private Expr finishCall(Expr expr)
    {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN))
        {
            do
            {
                if (arguments.size() >= 255)
                    error(peek(), "Can't have more than 255 arguments.");
                arguments.add(assignment());
            }
            while (match(COMMA));
        }
        
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expr.Call(expr, paren, arguments);
    }

    private Expr call()
    {
        Expr expression = primary();

        while (true)
        {
            if (match(LEFT_PAREN))
                expression = finishCall(expression);
            else
                break;
        }

        return expression;
    }

    private Expr primary() 
    {
        if (match(TRUE))
            return new Expr.Literal(true);
        if (match(FALSE))
            return new Expr.Literal(false);
        if (match(NIL))
            return new Expr.Literal(null);
        if (match(STRING, NUMBER))
            return new Expr.Literal(previous().literal);
        if (match(IDENTIFIER))
            return new Expr.Variable(previous());

        if (match(LEFT_PAREN))
        {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) 
    {
        for (TokenType type : types)
        {
            if (check(type))
            {
                advance();
                return true;
            }
        }
        
        return false;
    }

    private Token consume(TokenType type, String message)
    {
        if (check(type))
            return advance();

        throw error(peek(), message);
    }

    private Token advance() 
    {
        if (!isAtEnd())
            current++;

        return previous();
    }

    private boolean check(TokenType type) 
    {
        if (isAtEnd())
            return false;

        return peek().type == type;
    }

    private boolean isAtEnd()
    {
        return peek().type == EOF;
    }

    private Token peek()
    {
        return tokens.get(current);
    }

    private Token previous()
    {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message)
    {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize()
    {
        advance();
        while (!isAtEnd())
        {
            if (previous().type == SEMICOLON)
                return;

            switch (peek().type)
            {
                case CLASS:
                case FOR:
                case FUN: 
                case IF: 
                case PRINT:
                case RETURN:
                case VAR:
                case WHILE:
                    return;
                default:
            }

            advance();
        }
    }
}
