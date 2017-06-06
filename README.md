# jlemon

A fork of the
[LEMON parser generator](https://www.sqlite.org/src/doc/trunk/doc/lemon.html)
that generates Java code.

Files `lemon.c`, `lempar.c` are extracted from SQLite v3.18.0.

## Usage

```bash
> # compile
> cc -g -O2 -o jlemon lemon.c
> # run
> jlemon <filename>.y
> # preprocess generated code
> cpp -P <filename>.j > yyParser.java
```

For example:
```bash
cc -g -O2 -o jlemon lemon.c
./jlemon src/test/java/simple/parser.y
cpp -P src/test/java/simple/parser.j > src/test/java/simple/yyParser.java
mvn test
```
Or on windows platform:
```
cl /O2 /Fejlemon.exe lemon.c
jlemon.exe src\test\java\simple\parser.y
cl /EP /C src\test\java\simple\parser.j > src\test\java\simple\yyParser.java
```

## Generated Parser API

```java
public class yyParser {
  // %extra_argument declaration

  public yyParser(
    ParseARG_PDECL               /* Optional %extra_argument parameter */
  ) {
    // constructor
  }
  public void ParseFinalize() {  /* or can be renamed to %nameFinalize */
    // optional (clean the stack)
  }
  public void Parse(             /* or can be renamed to %name */
    int yymajor,                 /* The major token code number */
    ParseTOKENTYPE yyminor       /* The value for the token */
  ){
    // To be called by lexer
  }
}
```

## Hack

As there is no `union` in Java, `yy%d` fields of `YYMINORTYPE` have been replaced by `yy%d()` getters and `yy%d(%type value)` setters.
But when translating code (see `translate_code`), the logic used to make the difference between a read access to a `yy%d` field and a write access is fallible.

Maybe try a code manipulator ([janino](http://janino-compiler.github.io/janino/#janino-as-a-code-manipulator)) ?