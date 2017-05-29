%token_type { int }

// The generated parser function takes a 4th argument as follows:
%extra_argument {Result result}

%left PLUS MINUS.
%left DIVIDE TIMES.

%include {
package simple;

import static simple.TokenType.*;
}

%syntax_error {
  throw new IllegalArgumentException(String.format("near token %d: syntax error", TOKEN));
}

program ::= expr(A). { result.expr = A; }

%type expr { Expr }
expr(A) ::= expr(B) MINUS expr(C). { A = new BinExpr(MINUS, B, C); }
expr(A) ::= expr(B) PLUS expr(C). { A = new BinExpr(PLUS, B, C); }
expr(A) ::= expr(B) TIMES expr(C). { A = new BinExpr(TIMES, B, C); }
expr(A) ::= expr(B) DIVIDE expr(C). { A = new BinExpr(DIVIDE, B, C); }

expr(A) ::= INTEGER(B). { A = new NumExpr(B); }
