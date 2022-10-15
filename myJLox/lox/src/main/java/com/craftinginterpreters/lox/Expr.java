package com.craftinginterpreters.lox;

import java.util.List;

abstract class Expr
{
    interface Visitor<R>
    {
        R visitCommaExpr(Comma expr);
        R visitBinaryExpr(Binary expr);
        R visitTernaryExpr(Ternary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitVariableExpr(Variable expr);
        R visitUnaryExpr(Unary expr);
        R visitLogicalExpr(Logical expr);
        R visitAssignExpr(Assign expr);
    }
    static class Comma extends Expr
    {
        Comma(List<Expr> exprs)
        {
            this.exprs = exprs;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitCommaExpr(this);
        }

        final List<Expr> exprs;
    }
    static class Binary extends Expr
    {
        Binary(Expr left, Token operator, Expr right)
        {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }
    static class Ternary extends Expr
    {
        Ternary(Expr left, Expr inner, Expr right, Token condition)
        {
            this.left = left;
            this.inner = inner;
            this.right = right;
            this.condition = condition;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitTernaryExpr(this);
        }

        final Expr left;
        final Expr inner;
        final Expr right;
        final Token condition;
    }
    static class Grouping extends Expr
    {
        Grouping(Expr expression)
        {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;
    }
    static class Literal extends Expr
    {
        Literal(Object value)
        {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }
    static class Variable extends Expr
    {
        Variable(Token name)
        {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitVariableExpr(this);
        }

        final Token name;
    }
    static class Unary extends Expr
    {
        Unary(Token operator, Expr right)
        {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }
    static class Logical extends Expr
    {
        Logical(Expr left, Token operator, Expr right)
        {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitLogicalExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }
    static class Assign extends Expr
    {
        Assign(Token name, Expr value)
        {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor)
        {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final Expr value;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
