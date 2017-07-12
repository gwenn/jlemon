package org.sqlite.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

/**
 * Port of Go Scanner in Java.
 */
abstract class Scanner {
	// The reader provided by the client.
	private /*final*/ Reader r;
	// Maximum size of a token
	private final int maxTokenSize;
	// Last token type returned by split.
	private int tokenType;
	// Buffer used as argument to split.
	private char[] buf;
	// First non-processed byte in buf.
	private int start;
	// End of data in buf.
	private int end;

	private boolean eof;

	Scanner(Reader r) {
		init(r);
		maxTokenSize = 64 * 1024;
		buf = new char[4096]; // Plausible starting size; needn't be large.
	}

	/** Reuse this scanner with a new content. */
	void init(Reader r) {
		this.r = Objects.requireNonNull(r, "null reader");
		tokenType = 0;
		start = 0;
		end = 0;
		eof = false;
	}

	/** Used to tokenize the input.
	 *
	 * If the {@code data} does not yet hold a complete token, {@code split} can return {@code 0} (and not {@link #advance})
	 * to signal the Scanner to read more data into the slice and try again with a longer slice starting at the same point in the input.
	 *
	 * If the {@code data} content must be skipped, {@code split} can {@link #advance} and return {@code 0}.
	 *  
	 * The function is never called with an empty data slice unless {@code atEOF} is {@code true}.
	 * If {@code atEOF} is {@code true}, however, data may be non-empty and, as always, holds unprocessed text.
	 * 
	 * @param data non null
	 * @param start non negative
	 * @param end greater that {@code start} (or maybe equal at EOF)
	 * @param atEOF {@code true} at end of file
	 */
	abstract int split(char[] data, int start, int end, boolean atEOF) throws ScanException;

	/** Advances the Scanner to the next token, which will then be
	 * available through the {@link #text()} method.
	 * @return {@code false} when the scan stops, by reaching the end of the input.
	 */
	boolean scan() throws ScanException {
		// Loop until we have a token.
		while (true) {
			// See if we can get a token with what we already have.
			if (end > start || eof) {
				final int pstart = start;
				tokenType = split(buf, start, end, eof);
				if (tokenType != 0) {
					return true;
				} else if (pstart != start) {
					continue;
				}
			}
			// We cannot generate a token with what we are holding.
			// If we've already hit EOF, we are done.
			if (eof) {
				start = 0;
				end = 0;
				return false;
			}
			read();
		}
	}

	/** @return The token type returned by {@link #split} function */
	public int tokenType() {
		return tokenType;
	}

	private void read() throws ScanException {
		// Must read more data.
		// First, shift data to beginning of buffer if there's lots of empty space
		// or space is needed.
		if (start > 0 && (end == buf.length || start > buf.length / 2)) {
			System.arraycopy(buf, start, buf, 0, end - start);
			end -= start;
			start = 0;
		}
		// Is the buffer full? If so, resize.
		if (end == buf.length) {
			if (buf.length >= maxTokenSize) {
				throw new ScanException(ErrorCode.TokenTooLong);
			}
			int newSize = Math.min(buf.length * 2, maxTokenSize);
			char[] newBuf = new char[newSize];
			System.arraycopy(buf, start, newBuf, 0, end - start);
			buf = newBuf;
			end -= start;
			start = 0;
			return;
		}
		// Finally we can read some input.
		try {
			int n = r.read(buf, end, buf.length - end);
			if (n < 0) {
				eof = true;
			} else {
				end += n;
			}
		} catch (IOException e) {
			throw new ScanException(e.getMessage(), e);
		}
	}

	boolean atEndOfFile() {
		return eof;
	}

	String subSequence(int start, int end) {
		if (start < 0 || end < 0 || start > end || end > this.end) {
			throw new IndexOutOfBoundsException();
		}
		if (start == end) {
			return "";
		}
		return new String(buf, /*this.start + */start, end - start);
	}

	/** Used by {@link #split} function to advance the input.
	 * @param n the number of bytes.
	 */
	void advance(int n) throws ScanException {
		if (n < 0) {
			throw new ScanException("SplitFunc returns negative advance count");
		}
		if (n > end) {
			throw new ScanException("SplitFunc returns advance count beyond input");
		}
		start = n;
	}
}
