package org.sqlite.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.sqlite.parser.ast.Cmd;

class Main {
	public static void main__(String[] args) {
		final StringReader stringReader = new StringReader("PRAGMA parser_trace=1;");
		parse(new Tokenizer(stringReader), true);
	}

	public static void main(String[] args) throws IOException {
		Path dir = Paths.get(args[0]);
		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				parse(file);
				return super.visitFile(file, attrs);
			}
		});
		/*try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{sql}")) {
			for (Path path : stream) {
				parse(path);
			}
		}*/
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
		//System.out.println(path);
		try (Reader reader = Files.newBufferedReader(path)) {
			final Tokenizer lexer = new Tokenizer(reader);
			try {
				parse(lexer, true);
			} catch (ScanException e) {
				System.out.printf("Error while lexing %s (%d:%d): %s%n", path, lexer.lineno(), lexer.column(), e.getMessage());
			} catch (ParseException e) {
				System.out.printf("Error while parsing %s (%d:%d): %s%n", path, lexer.lineno(), lexer.column(), e.getMessage());
			} catch (Throwable t) {
				System.out.printf("Error while parsing %s (%d:%d): %s%n", path, lexer.lineno(), lexer.column(), t.getMessage());
				t.printStackTrace(System.out);
			}
		}
	}
	private static void parse_(Path path) throws IOException {
		try (Reader reader = Files.newBufferedReader(path)) {
			final Tokenizer lexer = new Tokenizer(reader);
			try {
				for (Cmd cmd : new Parser(lexer)) {
					assert cmd != null;
				}
			} catch (ScanException e) {
				System.out.printf("Error while lexing %s (%d:%d): %s%n", path, lexer.lineno(), lexer.column(), e.getMessage());
			} catch (ParseException e) {
				System.out.printf("Error while parsing %s (%d:%d): %s%n", path, lexer.lineno(), lexer.column(), e.getMessage());
			}
		}
	}

	private static void parse(Tokenizer lexer, boolean check) {
		while (!lexer.atEndOfFile()) {
			Cmd cmd = Parser.parse(lexer);
			if (cmd == null) {
				continue; // empty end
			}
			if (check) {
				String sql = cmd.toSql();
				StringReader reader = new StringReader(sql);
				Tokenizer tokenizer = new Tokenizer(reader);
				try {
					parse(tokenizer, false);
				} catch (Exception e) {
					System.out.printf("Error while parsing %s (%d:%d): %s%n", sql, tokenizer.lineno(), tokenizer.column(), e.getMessage());
					throw e;
				}
			}
		}
	}
	private static void tokenize(Path path) throws IOException {
		System.out.println(path);
		try (Reader reader = Files.newBufferedReader(path)) {
			Tokenizer lexer = new Tokenizer(reader);
			try {
				while (lexer.scan()) {
					short tokenType = lexer.tokenType();
					System.out.print(TokenType.toString(tokenType));
					System.out.print(" : ");
					String text = lexer.text();
					System.out.println(text);
					if (tokenType == TokenType.TK_SEMI) {
						System.out.println();
					}
				}
			} catch (ScanException e) {
				System.out.printf("Error while lexing %s (%d:%d): %s%n", path, lexer.lineno(), lexer.column(), e.getMessage());
			}
		}
	}
}
