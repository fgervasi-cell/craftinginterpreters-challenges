package com.craftinginterpreters.lox;

import java.util.Objects;

class Token
{
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line)
    {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString()
    {
        return type + " " + lexeme + " " + literal;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (obj instanceof Token)
        {
            Token token = (Token)obj;
            return token.lexeme.equals(lexeme) && token.type == type;
        }
        return false;
    }

    @Override
    public int hashCode() 
    {
        return Objects.hash(lexeme, type);
    }
}
