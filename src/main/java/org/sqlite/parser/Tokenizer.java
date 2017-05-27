package org.sqlite.parser;

import java.io.Reader;

import static java.lang.Character.isWhitespace;
import static org.sqlite.parser.TokenType.*;

/**
 * A SQL tokenizer.
 * Adapted from [SQLite tokenizer](http://www.sqlite.org/src/artifact?ci=trunk&filename=src/tokenize.c)
 */
class Tokenizer extends Scanner {
  private int lineno;
  private int column;

  Tokenizer(Reader r) {
    super(r);
  }

  @Override
  void init(Reader r) {
    super.init(r);
    lineno = 1;
    column = 0;
  }

  int split(char[] data, int start, int end, boolean atEOF) throws ScanException {
    if (atEOF && end == start) {
      return 0;
    }
    char c = data[start];
    start += 1;
    if (isWhitespace(c)) {
      // eat as much space as possible
      int i;
      for (i = start; i < end && isWhitespace(data[i]); i++) {}
      advance(i);
      return 0;
    }
    if (c == '-') {
      if (start < end) {
        if (data[start] == '-') {
          // eat comment
          int i;
          for (i = start + 1; i < end && (c=data[i]) != '\n'; i++) {}
          if (c == '\n' || atEOF) {
            advance(i+(atEOF ? 0 : 1));
            return 0;
          } // else ask more data until '\n'
        } else {
          advance(start);
          return TK_MINUS;
        }
      } else if (atEOF) {
        advance(start);
        return TK_MINUS;
      } // else ask more data
    } else if (c == '(')  {
      advance(start);
      return TK_LP;
    } else if (c == ')')  {
      advance(start);
      return TK_RP;
    } else if (c == ';')  {
      advance(start);
      return TK_SEMI;
    } else if (c == '+')  {
      advance(start);
      return TK_PLUS;
    } else if (c == '*')  {
      advance(start);
      return TK_STAR;
    } else if (c == '/')  {
      if (start < end) {
        if (data[start] == '*') {
          // eat comment
          int i;
          for (i = start + 1; i < end && (c != '*' || data[i] != '/');) {
            c = data[i];
            i++;
          }
          if (data[i] == '/') {
            advance(i+1);
            return 0;
          } else if (atEOF) {
            throw new ScanException(ErrorCode.UnterminatedBlockComment);
          } // else ask more data until '*/'
        } else {
          advance(start);
          return TK_SLASH;
        }
      } else if (atEOF) {
        advance(start);
        return TK_SLASH;
      } // else ask more data
    } else if (c == '%') {
      advance(start);
      return TK_REM;
    } else if (c == '=') {
      if (start < end) {
        if (data[start] == '=') {
          advance(start+1);
        } else {
          advance(start);
        }
        return TK_EQ;
      } else if (atEOF) {
        advance(start);
        return TK_EQ;
      } // else ask more data to fuse '==' or not
    } else if (c == '<') {
      if (start < end) {
        if (data[start] == '=') {
          advance(start+1);
          return TK_LE;
        } else if (data[start] == '>') {
          advance(start+1);
          return TK_NE;
        } else if (data[start] == '<') {
          advance(start+1);
          return TK_LSHIFT;
        } else {
          advance(start);
          return TK_LT;
        }
      } else if (atEOF) {
        advance(start);
        return TK_LT;
      } // else ask more data
    } else if (c == '>') {
      if (start < end) {
        if (data[start] == '=') {
          advance(start+1);
          return TK_GE;
        } else if (data[start] == '>') {
          advance(start+1);
          return TK_RSHIFT;
        } else {
          advance(start);
          return TK_GT;
        }
      } else if (atEOF) {
        advance(start);
        return TK_GT;
      } // else ask more data
    } else if (c == '!') {
      if (start < end) {
        if (data[start] == '=') {
          advance(start+1);
          return TK_NE;
        } else {
          throw new ScanException(ErrorCode.ExpectedEqualsSign);
        }
      } else if (atEOF) {
        throw new ScanException(ErrorCode.ExpectedEqualsSign);
      } // else ask more data
    } else if (c == '|') {
      if (start < end) {
        if (data[start] == '|') {
          advance(start+1);
          return TK_CONCAT;
        } else {
          advance(start);
          return TK_BITOR;
        }
      } else if (atEOF) {
        advance(start);
        return TK_BITOR;
      } // else ask more data
    } else if (c == ',') {
      advance(start);
      return TK_COMMA;
    } else if (c == '&') {
      advance(start);
      return TK_BITAND;
    } else if (c == '~') {
      advance(start);
      return TK_BITNOT;
    } else {
      throw new ScanException(ErrorCode.UnrecognizedToken);
    }
    // TODO
    return 0; // ask more data
  }

  /**
   * Returns current line number
   */
  int lineno() {
    return lineno;
  }

  /**
   * Returns current column.
   */
  int column() {
    return column;
  }

  private static boolean is_identifier_start(char c) {
    return (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z') || c > 0x7F;
  }

  private static boolean is_identifier_continue(char c) {
    return c == '$' || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' ||
      (c >= 'a' && c <= 'z') || c > 0x7F;
  }
}