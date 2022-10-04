# Challenges

## Chapter 1: Introduction

### Challenge 1.1

The six domain-specific languages are:

- Markdown: A lightweight markup language with plain-text-formatting syntax
- Hypertext Markup Language (HTML): Standard markup langauge for documents designed to be displayed in a web browser
- gitignore: A gitignore file tells Git which files to ignore
- Makefile: Is used by the build management tool *make* to describe the build process
- YAML Ain't Markup Language (YAML): Simplified markup language for data serialization
- Syntactically Awesome Style Sheets (Sass): Preprocessor scripting language that is interpreted or compiled into Cascading Style Sheets (CSS)
- ...

### Challenge 1.2

See "HelloWorld.java"

### Challenge 1.3

See "/c_programming/doubly_linked_list/" for all the files. I did it with integers instead of strings though...

## Chapter 2: A Map of the Territory

### Challenge 2.1

// TODO

### Challenge 2.2

// TODO

### Challenge 2.3

// TODO

## Chapter 3: The Lox Language

### Challenge 3.1

See "./lox_programs/".

### Challenge 3.2

It is kind of hard for me to come up with questions about the language.
See the lox programs and the comments I wrote. There I documented some "special" things I noticed. Also under Challenge 3.3 I listed some thing that came to my mind.

### Challenge 3.3

Missing features for "real" programs:

- Multiple inheritance
- Type casting
- ++ and -- shortcuts for incrementing or decrementing
- No enumeration type
- No overloading
- Inner classes
- No Arrays
- No possibility to format the output of the *print* statement

## Chapter 4: Scanning

### Challenge 4.1

// TODO

### Challenge 4.2

// TODO

### Challenge 4.3

// TODO

### Challenge 4.4

Solution without nesting:

```java
private void blockComment() 
{
    while (!isAtEnd())
    {
        if (peek() == '*' && peekNext() == '/')
        {
            // The block ends and we need to consume "*" and "/".
            advance();
            advance();
            return;
        }
        advance();
    }
    Lox.error(line, "Block comment not terminated.");
}
```

```java
case '/':
    if (match('/'))
    {
        // A comment goes unitl the end of the line.
        while (peek() != '\n' && !isAtEnd())
            advance();
    }
    else if (match('*'))
    {
        blockComment();
    }
    else 
    {
        addToken(SLASH);
    }
    break;
```

Solution with nesting:

```java
private void blockComment() 
{
    while (!isAtEnd())
    {
        // There is a nested block comment.
        if (peek() == '/' && peekNext() == '*')
        {
            advance();
            advance();
            blockComment();
        }
                
        if (peek() == '*' && peekNext() == '/')
        {
            // The block ends and we need to consume "*" and "/".
            advance();
            advance();
            return;
        }

        if (!isAtEnd() && advance() == '\n')
        {
            line++;
        }
    }
    Lox.error(line, "Block comment not terminated.");
}
```

The problem with this solution is that things like

```lox
/* 
    This is a comment
    /*
        This is a nested comment
    */
*/
```

work and

```lox
/* 
    This is a comment
    /*
        This is a nested comment
    
*/
```

is also regognized as an error but in the below example the tokens *STAR* and *SLASH* are scanned :(.

```lox
/* 
    This is a comment
    */
*/
```
