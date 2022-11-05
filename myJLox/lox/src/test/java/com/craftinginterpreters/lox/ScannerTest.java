package com.craftinginterpreters.lox;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ScannerTest 
{
    @Test
    public void testIfStatement()
    {
        String ifStatement = """
                if (a < 10)
                {
                    doSmth();
                }
                """;
        Scanner sc = new Scanner(ifStatement);
        List<Token> actual = sc.scanTokens();
        List<Token> expected = new ArrayList<>()
        {
            {
                add(new Token(TokenType.IF, "if", null, 1));
                add(new Token(TokenType.LEFT_PAREN, "(", null, 1));
                add(new Token(TokenType.IDENTIFIER, "a", null, 1));
                add(new Token(TokenType.LESS, "<", null, 1));
                add(new Token(TokenType.NUMBER, "10", 10.0, 1));
                add(new Token(TokenType.RIGHT_PAREN, ")", null, 1));
                add(new Token(TokenType.LEFT_BRACE, "{", null, 2));
                add(new Token(TokenType.IDENTIFIER, "doSmth", null, 3));
                add(new Token(TokenType.LEFT_PAREN, "(", null, 3));
                add(new Token(TokenType.RIGHT_PAREN, ")", null, 3));
                add(new Token(TokenType.SEMICOLON, ";", null, 3));
                add(new Token(TokenType.RIGHT_BRACE, "}", null, 4));
                add(new Token(TokenType.EOF, "", null, 4));
            }
        };
        assertEquals(expected, actual);
    }    

    @Test
    public void testWhileStatement()
    {
        String whileStatement = """
                while (doSmth() and doSmthElse() == true)
                {
                    var a = "20";
                    break;
                }
                """;
        Scanner sc = new Scanner(whileStatement);
        List<Token> actual = sc.scanTokens();
        List<Token> expected = new ArrayList<>()
        {
            {
                add(new Token(TokenType.WHILE, "while", null, 1));
                add(new Token(TokenType.LEFT_PAREN, "(", null, 1));
                add(new Token(TokenType.IDENTIFIER, "doSmth", null, 1));
                add(new Token(TokenType.LEFT_PAREN, "(", null, 1));
                add(new Token(TokenType.RIGHT_PAREN, ")", null, 1));
                add(new Token(TokenType.AND, "and", null, 1));
                add(new Token(TokenType.IDENTIFIER, "doSmthElse", null, 1));
                add(new Token(TokenType.LEFT_PAREN, "(", null, 1));
                add(new Token(TokenType.RIGHT_PAREN, ")", null, 1));
                add(new Token(TokenType.EQUAL_EQUAL, "==", null, 1));
                add(new Token(TokenType.TRUE, "true", null, 1));
                add(new Token(TokenType.RIGHT_PAREN, ")", null, 1));
                add(new Token(TokenType.LEFT_BRACE, "{", null, 2));
                add(new Token(TokenType.VAR, "var", null, 3));
                add(new Token(TokenType.IDENTIFIER, "a", null, 3));
                add(new Token(TokenType.EQUAL, "=", null, 3));
                add(new Token(TokenType.STRING, "\"20\"", "20", 3));
                add(new Token(TokenType.SEMICOLON, ";", null, 3));
                add(new Token(TokenType.BREAK, "break", null, 4));
                add(new Token(TokenType.SEMICOLON, ";", null, 4));
                add(new Token(TokenType.RIGHT_BRACE, "}", null, 5));
                add(new Token(TokenType.EOF, "", null, 5));
            }
        };
        assertEquals(expected, actual);
    }

    @Test
    public void testUnterminatedString()
    {
        String unterminated = """
                var a = \"unterminated
                """;
        Scanner sc = new Scanner(unterminated);
        List<Token> actual = sc.scanTokens();
    }
}
