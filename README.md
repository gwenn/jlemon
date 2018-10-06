# jlemon

A fork of the
[LEMON parser generator](https://www.sqlite.org/src/doc/trunk/doc/lemon.html)
that generates Java code and the associated [SQL parser](http://www.sqlite.org/src/artifact?ci=trunk&filename=src/parse.y).

Files `lemon.c`, `lempar.c` are extracted from SQLite v3.18.0.

[![Build Status](https://secure.travis-ci.org/gwenn/jlemon.png)](http://www.travis-ci.org/gwenn/jlemon)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.gwenn/sqlite-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.gwenn/sqlite-parser/)

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
Or [javolution Union](http://javolution.org/apidocs/javolution/io/Union.html) ?

## SQL Parser

[SQLite lexer](http://www.sqlite.org/src/artifact?ci=trunk&filename=src/tokenize.c) and [SQLite parser](http://www.sqlite.org/src/artifact?ci=trunk&filename=src/parse.y) have been ported from C to Java.
The parser generates an AST.

The parser is/will be used to fix [DatabaseMetaData](https://github.com/gwenn/sqlite-jna/blob/master/src/main/java/org/sqlite/driver/DbMeta.java) implementation.

 * java.sql.DatabaseMetaData.getColumns
 * java.sql.DatabaseMetaData.getPrimaryKeys
 * java.sql.DatabaseMetaData.getBestRowIdentifier
 * java.sql.DatabaseMetaData.getCrossReference
 * java.sql.DatabaseMetaData.getImportedKeys
 * java.sql.DatabaseMetaData.getExportedKeys
 * java.sql.DatabaseMetaData.getIndexInfo

## Lexer/Parser

  - Keep track of position (line, column).
  - Streamable (stop at the end of statement).
  - Resumable (restart after the end of statement).

## Test

SQL lexer and parser have been tested with the following scripts:

 * https://github.com/bkiers/sqlite-parser/tree/master/src/test/resources
 * https://github.com/codeschool/sqlite-parser/tree/master/test/sql/official-suite
