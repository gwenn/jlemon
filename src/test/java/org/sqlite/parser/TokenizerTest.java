package org.sqlite.parser;

import java.io.StringReader;
import org.junit.Test;

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
}
