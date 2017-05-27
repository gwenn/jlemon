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
> cpp -P <filename>.java
```

## Hack

As there is no `union` in Java, `yy%d` fields of `YYMINORTYPE` have been replaced by `yy%d()` getters and `yy%d(%type value)` setters.
But when translating code (see `translate_code`), the logic used to make the difference between a read access to a `yy%d` field and a write access is fallible.