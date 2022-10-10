package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Comma;
import com.craftinginterpreters.lox.Expr.Grouping;
import com.craftinginterpreters.lox.Expr.Literal;
import com.craftinginterpreters.lox.Expr.Ternary;
import com.craftinginterpreters.lox.Expr.Unary;

public class RpnPrinter implements Expr.Visitor<String> 
{
    public String print(Expr expr)
    {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Binary expr) 
    {
        String leftOperand = expr.left.accept(this);
        String rightOperand = expr.right.accept(this);
        String operator = expr.operator.lexeme;
        return String.format("%s %s %s", leftOperand, rightOperand, operator);
    }

    @Override
    public String visitGroupingExpr(Grouping expr) 
    {
        return expr.expression.accept(this);
    }

    @Override
    public String visitLiteralExpr(Literal expr) 
    {
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) 
    {
        return "";
    }
    
    @Override
    public String visitTernaryExpr(Ternary expr) 
    {
        return "";
    }
    
    @Override
    public String visitCommaExpr(Comma expr) 
    {
        return "";
    }

    public static void main(String[] args)
    {
        Expr expr = new Binary(
            new Grouping(
            new Binary(
            new Literal(1), 
            new Token(TokenType.PLUS, "+", null, 0), 
            new Literal(2))), 
            new Token(TokenType.STAR, "*", null, 0), 
            new Grouping(
            new Binary(
            new Literal(4), 
            new Token(TokenType.MINUS, "-", null, 0), 
            new Literal(3))));

        System.out.println(new RpnPrinter().print(expr));
    }
}
