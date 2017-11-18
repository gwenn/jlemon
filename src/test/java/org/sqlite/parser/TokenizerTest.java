package org.sqlite.parser;

import java.io.StringReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenizerTest {
	@Test
	public void testInsert() {
		String sql = "INSERT INTO t3 VALUES( 'r c', '');";
		Tokenizer lexer = new Tokenizer(new StringReader(sql));
		while (lexer.scan()) {
			lexer.tokenType();
			lexer.text();
		}
	}

	@Test
	public void testEscapedQuote() {
		String sql = "SELECT 'escaped''quote'";
		Tokenizer lexer = new Tokenizer(new StringReader(sql));
		assertTrue(lexer.scan());
		assertEquals(TokenType.TK_SELECT, lexer.tokenType());
		assertEquals("SELECT", lexer.text());
		assertTrue(lexer.scan());
		assertEquals(TokenType.TK_STRING, lexer.tokenType());
		assertEquals("escaped'quote", lexer.text());
	}
}
