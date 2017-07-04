package org.sqlite.parser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) throws IOException {
		Path dir = Paths.get(args[0]);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{sql}")) {
			for (Path path : stream) {
				parse(path);
			}
		}
	}
	public static void main_(String[] args) throws IOException {
		Path dir = Paths.get(args[0]);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{sql}")) {
			for (Path path : stream) {
				tokenize(path);
			}
		}
	}

	public static void _main(String[] args) throws IOException {
		for (String arg : args) {
			Path path = Paths.get(arg);
			tokenize(path);
		}
	}

	private static void parse(Path path) throws IOException {
		System.out.println(path);
		try (Reader reader = Files.newBufferedReader(path)) {
			try {
				Tokenizer lexer = new Tokenizer(reader);
				// TODO Parse next stmt
				while (!lexer.atEndOfFile()) {
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
					/* TODO Upon reaching the end of input, call the parser two more times
					** with tokens TK_SEMI and 0, in that order. */
					if (lexer.atEndOfFile()) {
						if (TokenType.TK_SEMI != lastTokenParsed) {
							parser.sqlite3Parser(TokenType.TK_SEMI, null);
						}
						parser.sqlite3Parser(0, null);
					}
					// TODO display cmd
				}
			} catch (ScanException e) {
				System.out.printf("Error while lexing %s: %s%n", path, e.getMessage());
			} catch (ParseException e) {
				System.out.printf("Error while parsing %s: %s%n", path, e.getMessage());
			}
		}
	}
	private static void tokenize(Path path) throws IOException {
		System.out.println(path);
		try (Reader reader = Files.newBufferedReader(path)) {
			try {
				Tokenizer lexer = new Tokenizer(reader);
				while (lexer.scan()) {
					int tokenType = lexer.tokenType();
					System.out.print(TokenType.toString(tokenType));
					System.out.print(" : ");
					String text = lexer.text();
					System.out.println(text);
					if (tokenType == TokenType.TK_SEMI) {
						System.out.println();
					}
				}
			} catch (ScanException e) {
				System.out.printf("Error while lexing %s: %s%n", path, e.getMessage());
			}
		}
	}
}