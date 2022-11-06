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

[Solution](https://github.com/munificent/craftinginterpreters/blob/01e6f5b8f3e5dfa65674c2f9cf4700d73ab41cf8/note/answers/chapter01_introduction/1.md)  
*Comment: YAML and gitignore are not listed in the solution. Instead Jinja2 is mentioned. Jinja is a web template engine for Python (similar to Django).*

### Challenge 1.2

See "HelloWorld.java"  

[Solution](https://github.com/munificent/craftinginterpreters/blob/01e6f5b8f3e5dfa65674c2f9cf4700d73ab41cf8/note/answers/chapter01_introduction/2/Hello.java)  
*Comment: And whatever build tool one chooses...*

### Challenge 1.3

See "/c_programming/doubly_linked_list/" for all the files. I did it with integers instead of strings though...

[Solution](https://github.com/munificent/craftinginterpreters/blob/01e6f5b8f3e5dfa65674c2f9cf4700d73ab41cf8/note/answers/chapter01_introduction/3/linked_list.c)  
*Comment: The solution looks pretty similar to mine so I guess I did it correctly. I also tested my solution thoroughly. The difference is that the solution works with strings.*

## Chapter 2: A Map of the Territory

[Solution](https://github.com/munificent/craftinginterpreters/blob/01e6f5b8f3e5dfa65674c2f9cf4700d73ab41cf8/note/answers/chapter02_map.md)

### Challenge 2.1

There are tons of open source compilers (just search "compiler" on GitHub or smth). However I find it very hard to find the different pieces mentioned in this book. A very simple compiler example is this one: [https://github.com/jamiebuilds/the-super-tiny-compiler](https://github.com/jamiebuilds/the-super-tiny-compiler).  

*Comment: Sadly the solution does not give any examples of good open source compilers.*

### Challenge 2.2

Possible reasons not to JIT:

- Hard to implement
- There can be a delay before the actual execution of a program because it takes time to load and compile the bytecode
- Heavy usage of cache memory

*Comment: The solution also mentions:*

- *You are tied to a specific CPU architecture*
- *Some operating systems disallow executing code generated at runtime which makes just in time compilation impossible*

### Challenge 2.3

One reason could be to offer a command line tool that takes code line by line and executes it immediately.

*Comment: The actualy reason is macros. A macro is code that is executed at compile time so it has to be evaluated immediately.*

## Chapter 3: The Lox Language

[Solution](https://github.com/munificent/craftinginterpreters/blob/01e6f5b8f3e5dfa65674c2f9cf4700d73ab41cf8/note/answers/chapter03_lox.md)

### Challenge 3.1

See "./lox_programs/".  

*Comment: Correct.*

### Challenge 3.2

It is kind of hard for me to come up with questions about the language.
See the lox programs and the comments I wrote. There I documented some "special" things I noticed. Also under Challenge 3.3 I listed some thing that came to my mind.  

*Comment: The solution names some interesting edge cases that will be tested in the following. See ./lox_programs for all of the code.*

What happens if you access a global variable in a function before it is defined?

```lox
fun foo()
{
    print global;
}

foo();

var global = "global";

// Produces error at line 3: Undefined variable 'global'.
```

What does it mean to say something is "an error"? Runtime error? Compile time?  

You need to distinguish

- Syntax errors: Detected by the Scanner
- Compile time errors: Detected at compile time by the Parser
- Runtime errors: Detected at runtime by the Interpreter
- Static errors: Detected in the static analysis between parsing and interpretation e.g. by the Resolver

What kind of expressions are allowed when a superclass is specified?  

```lox
class SomeClass < <some_expression>
{
    ...
}
```

I think the only one that works is VariableExpr (referencing another class name) for \<some_expression>. You cannot use a function call (CallExpr), LiteralExpr or BinaryExpr or anything else that does not hold the value of a class.

What happens if you declare two classes or functions with the same name?

```lox
fun foo()
{
    print "foo1";
}

fun foo()
{
    print "foo2";
}

foo();

// Prints "foo2". I.e. the last defined name is used!
```

Can a class inherit from something that isn't a class?  

No it cannot or you will get the error "Superclass must be a class". You can however store the value of a class in a variable or return it in a function and inherit from this variable or function.  

Can you reassign to the name that is bound by a class or function declaration?  

Yes you can.

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

*Comment: Additional things mentioned in the solution are `break`, `continue`, `switch` and a mechanism for handling exceptions.*

## Chapter 4: Scanning

[Solution](https://github.com/munificent/craftinginterpreters/blob/01e6f5b8f3e5dfa65674c2f9cf4700d73ab41cf8/note/answers/chapter04_scanning.md)

### Challenge 4.1

In regular grammars it is allowed only to have one non-terminal on the left side of a production rule.
On the right only one terminal and a maximum of one non-terminal symbol can be used. E.g. A --> aB or A --> a. The grammars of Python and Haskell break with this rule. In fact a lot of programming languages count on context-free (Type 2) grammars where the right side of a production rule can contain an arbitrary sequence of terminals and non-terminals. Such grammar can be described by the Backus-Naur-Form (BNF). Context-free grammars are used because they are more powerful than regular (Type 3) languages/grammars. E.g. with a type 3 grammar you cannot even specify well formed bracket expressions. Type 1 grammars on the other hand are too complex. Instead the context is inferred at a leater state in the parser.  
[https://qr.ae/pvegwT](https://qr.ae/pvegwT)  

*Comment: The solution basically states the same thing as I do in the above answer but explains it more nicely in respect to Python and Haskell. Both languages have significant levels of indentation. Those levels have to be tracked i.e. "stored" in the "memory" of the grammar which is not possible with regular grammars (this is also why well formed bracket expressions are not possible).*

### Challenge 4.2

Spaces in CoffeeScript:

- To format the content of block strings delimited by *"""* or *'''*
- Multi line strings are joined by a single space
- Objects can be defined in YAML syntax which also recognizes spaces
- For distinguishing operators from XML tags in JSX
- Using Markdown syntax in *Literate CoffeeScript*

[https://coffeescript.org/](https://coffeescript.org/)

Spaces in Ruby:

- In line oriented string literals there must be no space between *<<* and the terminator
- In case of self assignment the syntax *expr op= expr* does not allow whitespace between *op* and *=*
- For creating arrays of strings with %w expressions

[https://ruby-doc.org/docs/ruby-doc-bundle/Manual/man-1.4/syntax.html](https://ruby-doc.org/docs/ruby-doc-bundle/Manual/man-1.4/syntax.html)

Spaces in the C preprocessor:  
The preprocessor in some cases inserts whitespace elements in its token stream to eliminate ambiguity.

[https://stackoverflow.com/questions/37796947/spaces-inserted-by-the-c-preprocessor](https://stackoverflow.com/questions/37796947/spaces-inserted-by-the-c-preprocessor)  

*Comment: My solution might also be true but in the book the focus lies on the optional parantheses for method calls in CoffeeScript and Ruby. The C preprocessor uses spaces to distinguish between "normal" text and function macros.*

### Challenge 4.3

- In order to do stuff like in challenge 4.2 (cleaner syntax?)
- If the parser needs to know about whitespaces (e.g. if indentation has a meaning)
- If whitespaces change the semantic of a program

*Comment: Solution is only interested in not discarding comments. This might be useful for generating documentation and help text in an IDE and to preserve the original formatting in the comment for a nice display.*

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

*Comment: The trick for implementing block comment would have been a variable `nesting` which tracks how many levels the comment nests. That means we increment `nesting` each time we meet a '/\*' and decrement if there is a '\*/'. Afterwards we can check if the nesting is zero again. If not something probably went wrong.*

## Chapter 5: Representing Code

### Challenge 5.1

Given grammar:

```ebnf
expr -> expr ( "(" ( expr ( "," expr )* )? ")" | "." IDENTIFIER )+
      | IDENTIFIER
      | NUMBER
```

This grammar produces stuff like *IDENTIFIER.IDENTIFIER*, *NUMBER.IDENTIFIER.IDENTIFIER*, *IDENTIFIER(IDENTIFIER, NUMBER)* or IDENTIFIER(). Without the syntactic sugar I think the grammar should look something like this:

```ebnf
expr -> expr call;
expr -> IDENTIFIER;
expr -> NUMBER;
call -> "." IDENTIFIER;
call -> "." IDENTIFIER call;
call -> "(" ")";
call -> "(" expr ")";
call -> "(" expr argument ")";
argument -> "," expr;
argument -> "," expr argument;
```

The kind of expressions described by that might be class instantiation, function calls or accessing variables of a class instance.  

*Comment: The answer about what this grammar represents is correct. My grammar should also be correct although the solution is slightly different but there are several correct ways to solve this challenge.*

### Challenge 5.2

I do not know any functional language well enough to devise such a pattern I think...  

*Comment: So called "function pointers" can be used to achieve this task.*

### Challenge 5.3

See *com.craftinginterpreters.lox.RpnRpinter* class. Question that came to mind was how to parse unary operators in RPN. The answer I found stated that you just do not: [https://stackoverflow.com/questions/64867998/how-do-unary-operators-get-parsed-using-rpn](https://stackoverflow.com/questions/64867998/how-do-unary-operators-get-parsed-using-rpn).  

*Comment: My solution is correct except that unary expressions can be represented in RPN but one has to introduce a different symbol for unary negation because it cannot be the same as the binary minus or we would not know how many operands to pop from the stack.*

## Chapter 6: Parsing Expressions

[Solution](https://github.com/munificent/craftinginterpreters/blob/01e6f5b8f3e5dfa65674c2f9cf4700d73ab41cf8/note/answers/chapter06_parsing.md)

### Challenge 6.1

Comma operator has lowest precedence. Change grammar accordingly:

```ebnf
expression  -> comma ;
comma       -> ternary ( "," comma )* ;
ternary     -> equality "?" equality ":" ternary ;
equality    -> comparison ( ( "==" | "!=" ) comparison )* ;
comparison  -> term ( ( ">=" | ">" | "<" | "<=" ) term )* ; 
term        -> factor ( ( "+" | "-" ) factor )* ;
factor      -> unary ( ("/" | "*" ) unary )* ;
unary       -> ( "!" | "-" ) unary | primary ;
primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
```

In Java:

```java
private Expr comma()
{
    Expr expr = ternary();
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
```

Comma operator allows things like

```c
int a = 2, b = 3; // Not the comma operator!
int i = (a = a + 2, b + 1); // a will be incremented by 2 and i will have the value b + 1!
```

Infos:  
[https://en.cppreference.com/w/c/language/operator_precedence](https://en.cppreference.com/w/c/language/operator_precedence)  
[https://en.wikipedia.org/wiki/Comma_operator](https://en.wikipedia.org/wiki/Comma_operator)  

*Comment: The grammar for comma expressions is correct except that on the right side in the solution only the production rule for the operator with the next highest presedence appears. In other words the comma rule does not appear as a non terminal on the right side. Also in the solution there is no new syntax tree node. Instead the binary operator is reused. The implementation is slightly different from mine so I do not know if mine is totally correct. Maybe I should add some tests...*

### Challenge 6.2

Wanted: 4 == 5 ? expression : expression;  
The expression between the `?` and `:` is treated like a grouped expression. The whole expression is right-associative.

Add the ternary operator to the grammar:

```ebnf
expression  -> ternary ;
ternary     -> equality "?" equality ":" ternary ;
equality    -> comparison ( ( "==" | "!=" ) comparison )* ;
comparison  -> term ( ( ">=" | ">" | "<" | "<=" ) term )* ; 
term        -> factor ( ( "+" | "-" ) factor )* ;
factor      -> unary ( ("/" | "*" ) unary )* ;
unary       -> ( "!" | "-" ) unary | primary ;
primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
```

Program it into the parser:

```java
private Expr ternary()
{
    Expr expr = equality();
    if (match(QUESTION_MARK))
    {
        Expr inner = equality();
        consume(COLON, "Expected ':' for ternary operator.");
        Expr right = ternary();
        expr = new Expr.Ternary(expr, inner, right);
    }

    return expr;
}
```

*Comment: The implementation should be correct but my grammar is a little bit off. Ternary should also have the possibility to just go to equality so it should probably change to `ternary -> equality ( "?" equality ":" ternary )?`.*

### Challenge 6.3

Add error productions to grammar:

```ebnf
expression  -> comma ;
comma       -> ternary ( "," comma )* ;
ternary     -> equality "?" equality ":" ternary ;
equality    -> comparison? ( ( "==" | "!=" ) comparison )* ;
comparison  -> term? ( ( ">=" | ">" | "<" | "<=" ) term )* ; 
term        -> factor? ( ( "+" | "-" ) factor )* ;
factor      -> unary? ( ( "/" | "*" ) unary )* ;
unary       -> ( "!" | "-" | "+" ) unary | primary ;
primary     -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
```

Error if unary '+' expression:

```java
private Expr unary() 
{
    if (match(PLUS))
    {
        error(peek(), "Unary '+' expressions are not supported.");
    }
```

*Comment: The solution handles error productions totally different. They are all handled in the primary production rule as seperate alternatives. I am not sure if my solution works as well.*

## Chapter 7: Evaluating Expressions

[Solution](https://github.com/munificent/craftinginterpreters/blob/01e6f5b8f3e5dfa65674c2f9cf4700d73ab41cf8/note/answers/chapter07_evaluating.md)

### Challenge 7.1

My first intuition is that this feature should not be implemented. The reason is that many decisions have to be made about what things like `3 < "pumpkin"` will evaluate to. Those decisions are not transparent to the user and might lead to confusion. Java does not overload the comparison operators for strings and numbers (one can make use of the `Comparable` interface or `Comparator` class). JavaScript allows for comparing different types. The rules are actually not too complex.

- JavaScript converts the string of the expression to a number
- If its not a number (cannot be converted) the value is just `NaN`
- If its the empty string the value is 0
- After conversion the comparison can continue normally

I think those rules are not too bad but also not super useful if you can instead just explicitly convert the type...  

*Comment: The solution also states that it does not make sense to compare mixed types with each other. However the solution suggests that comparing strings with each other could be useful.*

### Challenge 7.2

Enable concatenation of string and number:

```java
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
```

*Comment: I could replace the three new cases I added with just one case where I check if either one of the operands is a string and then use the `stringify(Object)` function to convert both operands.*

### Challenge 7.3

Right now division by zero looks like this: `x / 0 = Infinity`. If this happens inside a greater term the term will still evaluate to `Infinity`. JavaScript also does it like this. Java throws an `ArithmeticException`. I think it is best to handle division by zero similar to java and terminate the program because if an expression returns `Infinity` the programmer probably did something wrong and you cannot use the value of the expression afterwards.  
Handle division by zero:

```java
case SLASH:
    checkNumberOperands(expr.operator, left, right);
    if ((double)right == 0)
    {
        throw new RuntimeError(expr.operator, "Division by zero.");
    }
    return (double)left / (double)right;
```

*Comment: An interesting point is made by the solution. It is said there that not throwing an error might be better in Lox case because Lox does not offer any handling of errors/exceptions anyway so the user has no chance to tackle this error in his program.*

## Chapter 8: Statements and State

[Solution](https://github.com/munificent/craftinginterpreters/blob/01e6f5b8f3e5dfa65674c2f9cf4700d73ab41cf8/note/answers/chapter08_statements.md)

### Challenge 8.1

In `Lox.runPrompt(String)` check if the line ends with a smicolon. If not it cannot be a statement so we evaluate it. To be able to parse the entered string we add the semicolon.

```java
if (!line.endsWith(";"))
    // The line is not a statement.
    evaluate(line + ";");
```

Now we can continue as in `Lox.run(String)`. The parsed statement must be a statement expression. From this statement we can get the encapsulated expression and interpret its value to print it on the screen. Finished...

```java
private static void evaluate(String source)
{
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    if (hadError || !(statements.get(0) instanceof Stmt.Expression))
        return;

    System.out.println(((Stmt.Expression)statements.get(0))
                        .expression.accept(new Interpreter()));
}
```

*Comment: The solution is a lot different and more compilcated than mine but that probably means that my solution is not the best and I overlooked something... In the solution the trick is to allow expression statements in the parser without semicolon if it is a REPL input. In `runPrompt(String)` we call a new method of the parser which sets a boolean value to true so the parser knows that an expression statement without semicolon is okay. We also change the interpreter so that it can evaluate a single expression and return its result.*

### Challenge 8.2

Challenge:

```lox
// Challenge 8.2
// No initializers.
var a;
var b;

a = "assigned";
print a; // OK, was assigned first.

print b; // Should result in error instead of "nil".
```

Solution: Add additional condition that `null` is not a valid value in `Environment.get(Object)`:

```java
Object get(Token name)
{
    if (values.containsKey(name.lexeme) && values.get(name.lexeme) != null)
        return values.get(name.lexeme);

    if (enclosing != null)
        return enclosing.get(name);

    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
}
```

*Comment: My solution is probably wrong because what happens if the user sets a variable explicitly to 'nil'? If this value is explicitly set no error should be thrown but I think my solution does it anyway. A better way to implement this is to use a custom value 'uninitialized' and check for that if a variable is accessed.*

### Challenge 8.3

Expectation: First a `Stmt.Var` ist parsed. Afterwards a block will be parsed with another `Stmt.Var` inside and a `print` statement. When interpreted `a` will be assigned the value "1" in the most outer environment. Then another envoironment is opened inside of it because of the upcoming block statement. Inside this block we have another variable declaration. First the initializer is interpreted. In the initializer we refer to the `a` variable of the outer scope so the result of this expression should be "1 + 2 = 3". This value is assigned to the "new" `a` and gets printed. So the expected answer is that "3" is printed but outside of the block `a` has still the value "1". This would not be the case if we did not search for a refered variable in the most inner scope first.  

Java gives an error "Duplicate local variable" in this case.  

*Comment: Correct.*

## Chapter 9: Control Flow

### Challenge 9.1

First class functions: A language has first class functions if functions are treated as any other variable.

```lox
// First class functions example
fun hello(functionArg)
{
    print "Hello";
    print functionArg();
}

fun world()
{
    return "World";
}

hello(world);
```

With first class functions one can execute different code based on which function is passed as an argument. This can also be used for branching inside the code.  

Dynamic dispatch means determining which function to call based on the objects type.

```lox
// Dynamic dispatch example
class Pet
{
    speak()
    {
        return "Undefined";
    }
}

class Dog < Pet
{
    speak()
    {
        return "Woof";
    }
}

class Cat < Pet 
{
    speak()
    {
        return "Cat";
    }
}

fun speak(pet)
{
    print pet.speak();
}

var pet = Pet();
var dog = Dog();
var cat = Cat();

speak(pet);
speak(dog);
speak(cat);
```

Based on the type of each variable another path in the program is chosen. This is how branching can be achieved using dynamic dispatch.

### Challenge 9.2

Instead of looping *recursive* function calls can be used to execute code several times. Functional programming languages like *Haskell* or *Scheme* use recursion instead of loops because they cannot store and modify the state of a variable.

### Challenge 9.3

Parsing the new keyword into an AST node:

```java
private Stmt breakStatement()
{
    Token position = previous();
    consume(SEMICOLON, "Expected ';' after 'break'.");
    return new Stmt.Break(position);
}
```

Throw a custom exception when interpreting a break statement so that the outer loop know to stop execution:

```java
@Override
public Void visitBreakStmt(Break statement)
{
    throw new BreakException();
}
```

Change the loop interpretation accordingly:

```java
@Override
public Void visitWhileStmt(While statement)
{
    while (isTruthy(evaluate(statement.condition)))
    {
        try
        {
            execute(statement.body);
        }
        catch (Interpreter.BreakException breakLoop)
        {
            break;
        }
    }
    return null;
}
```

## Chapter 10: Functions

### Challenge 10.1

Guess: Maybe because Smalltalk is compiled and not interpreted so we do not check for the number of arguments at runtime but at compile time.  

Wikipedia: "Smalltalk programs are usually compiled to bytecode, which is then interpreted by a virtual machine or dynamically translated into machine-native code."

### Challenge 10.2

Change grammar to:

```ebnf
funDecl -> "fun" function ;
function -> IDENTIFIER? "(" parameters? ")" block ;
```

That means a function name is optional. If the name is omitted we have a *lamda* or *anonymous* function at hand. This approach does not seem to work because lambdas are not statements but expressions which immediately return the function.  

Add new expression for this purpose:

```java
static class Lambda extends Expr
{
    Lambda(Stmt.Function functionStmt)
    {
        this.functionStmt = functionStmt;
    }

    @Override
    <R> R accept(Visitor<R> visitor)
    {
        return visitor.visitLambdaExpr(this);
    }

    final Stmt.Function functionStmt;
}
```

Parse a lambda expression as a primary expression if we match *FUN*. Parse code:

```java
private Expr.Lambda lambda()
{
    // For this type we do not consume an *IDENTIFIER* for the name of the function.
    Stmt.Function functionStmt = function("anonymous function");
    return new Expr.Lambda(functionStmt);
}
```

Interpreter code:

```java
@Override
public Object visitLambdaExpr(Lambda expr)
{
    return new LoxFunction(expr.functionStmt, environment);
}
```

### Challenge 10.3

My expectation is that such a program should print "5". The parameters are in another scope compared to the variables declared in the body because while parsing the body we assign a new environment to it.

```lox
fun scope(a)
{
    var a = "local";
    print a;
}

scope(5);
```

The expectation is wrong. The program prints "local". This is because of the reason already explained above but the conclusion I made was wrong. If we assign a new environment to the body the variable "a" is declared in this environment and therefore if we print "a" we search for its declaration in the inner most environment first which is of course the environment of the function body so "local" is printed.  

JavaScript and Python as far as I can tell behave in the same way.

## Chapter 11: Resolving and Binding

### Challenge 11.1

Because the syntax/grammar does not allow declaring a function without immediately giving it a name (except lambda functions but thats a different story).

### Challenge 11.2

Java code like this

```java
String a = "outer";
{
    String a = a;
}
```

produces the compile time error "Duplicate local variable a".  

Python allows code like this and treats it as a self assignment:

```python
a = "outer"

def foo():
    a = a

print(a)
```

In the above example no new varaible a in the inner scope is created. It references the a defined in the outer scope. This is because Python declares variables dynamically.  

JavaScript has very interesting behaviour.

```javascript
var a = "outer";
{
  var a = a;
  console.log(a);
}

console.log(a);
// prints "outer", "outer"
```

If *var* is used to declare the variables the program runs and prints "outer" both times.

```javascript
let a = "outer";
{
  var a = a;
  console.log(a);
}

console.log(a);
// error: The symbol "a" has already been declared
```

If *let* is used to declare the variable in the global scope an error tells the user that "a" has already been declared.

```javascript
var a = "outer";
{
  let a = a;
  console.log(a);
}

console.log(a);
// error: ReferenceError: Cannot access 'a2' before initialization
```

If *let* is used inside the inner scope the an *ReferenceError* is thrown.  

JavaScript has the worst solution to this problem out of all 4 languages (including lox) because the *let* and *var* syntax and semantics are very complicated to understand. Java and lox have the best solution in my opinion because they report it as an error before the user can even run the program. Python declares variables dynamically and they treat this case just as a self assignment and give a warning but not an error so the program is valid.

### Challenge 11.3

This lox code

```lox
var a = "not used"; // report this as an error
var b = "used";

print b;
```

should report an error like "The variable 'a' is never used". Therefore we track all the declared variables when visiting a variable statement in a map which stores the variable name and if it was referenced or not using a boolean. When visiting a variable expression we update the corresponding entry in the map.  

When the resolver has run we check if there are still variables left in the map with a boolean value of 'false' which means they were not referenced any where in the program. We report that as an error.

### Challenge 11.4

// TODO

## Chapter 12: Classes
