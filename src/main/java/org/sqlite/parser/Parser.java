package org.sqlite.parser;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.sqlite.parser.ast.Cmd;

import static java.util.Objects.requireNonNull;

/**
 * Streamable SQL parser.
 * <pre>{@code
 * final Tokenizer lexer = new Tokenizer(reader);
 * try {
 * 	for (Cmd cmd : new Parser(lexer)) {
 * 		assert cmd != null;
 * 	}
 * } catch (ScanException e) {
 * 	System.out.printf("Error while lexing %s (%d:%d): %s%n", path, lexer.lineno(), lexer.column(), e.getMessage());
 * } catch (ParseException e) {
 * 	System.out.printf("Error while parsing %s (%d:%d): %s%n", path, lexer.lineno(), lexer.column(), e.getMessage());
 * }
 * }</pre>
 */
public class Parser implements Iterable<Cmd> {
	private final Tokenizer lexer;

	public Parser(Tokenizer lexer) {
		this.lexer = requireNonNull(lexer);
	}

	private static final Token NULL = new Token(0, null);
	private static final Token SEMI = new Token(TokenType.TK_SEMI, ";");

	/**
	 * Parse one command/statement at a time.
	 *
	 * @return {@code null} at end of file/stream, otherwise one command/statement.
	 */
	public static Cmd parse(Tokenizer lexer) throws ScanException, ParseException {
		if (lexer.atEndOfFile()) {
			return null;
		}
		Context ctx = new Context();
		yyParser parser = new yyParser(ctx);
		int lastTokenParsed = -1;
		while (lexer.scan()) {
			int tokenType = lexer.tokenType();
			String text = lexer.text();
			Token yyminor = new Token(tokenType, text);
			parser.sqlite3Parser(tokenType, yyminor);
			lastTokenParsed = tokenType;
			if (ctx.done()) {
				break;
			}
		}
		if (lastTokenParsed == -1) {
			return null; // empty end
		}
		/* Upon reaching the end of input, call the parser two more times
		with tokens TK_SEMI and 0, in that order. */
		if (lexer.atEndOfFile()) {
			if (TokenType.TK_SEMI != lastTokenParsed) {
				parser.sqlite3Parser(TokenType.TK_SEMI, SEMI);
			}
			parser.sqlite3Parser(0, NULL);
		}
		parser.sqlite3ParserFinalize();
		assert ctx.stmt != null;
		return new Cmd(ctx.explain, ctx.stmt);
	}

	@Override
	public Iterator<Cmd> iterator() {
		return new Iterator<Cmd>() {
			private State state = State.NOT_READY;
			private Cmd cmd = null;

			@Override
			public boolean hasNext() {
				if (State.FAILED == state) {
					throw new IllegalStateException();
				}
				if (State.DONE == state) {
					return false;
				} else if (State.READY == state) {
					return true;
				}

				state = State.FAILED;
				cmd = parse(lexer);
				if (cmd != null) {
					state = State.READY;
					return true;
				} else {
					state = State.DONE;
					return false;
				}
			}

			@Override
			public Cmd next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				state = lexer.atEndOfFile() ? State.DONE : State.NOT_READY;
				return cmd;
			}
		};
	}
	private enum State {
		READY, NOT_READY, DONE, FAILED,
	}
}
