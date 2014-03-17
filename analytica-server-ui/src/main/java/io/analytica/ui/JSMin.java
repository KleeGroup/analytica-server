/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses>
 */
package io.analytica.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class JSMin {

	private static final int EOF = -1;

	private final PushbackInputStream in;
	private final OutputStream out;

	private int theA;
	private int theB;

	public JSMin(final InputStream in, final OutputStream out) {
		this.in = new PushbackInputStream(in);
		this.out = out;
	}

	/**
	 * isAlphanum -- return true if the character is a letter, digit, underscore, dollar sign, or non-ASCII character.
	 */
	static boolean isAlphanum(final int c) {
		return c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c == '_' || c == '$' || c == '\\' || c > 126;
	}

	/**
	 * get -- return the next character from stdin. Watch out for lookahead. If the character is a control character,
	 * translate it to a space or linefeed.
	 */
	int get() throws IOException {
		final int c = in.read();

		if (c >= ' ' || c == '\n' || c == EOF) {
			return c;
		}

		if (c == '\r') {
			return '\n';
		}

		return ' ';
	}

	/**
	 * Get the next character without getting it.
	 */
	int peek() throws IOException {
		final int lookaheadChar = in.read();
		in.unread(lookaheadChar);
		return lookaheadChar;
	}

	/**
	 * next -- get the next character, excluding comments. peek() is used to see if a '/' is followed by a '/' or '*'.
	 */
	int next() throws IOException, UnterminatedCommentException {
		int c = get();
		if (c == '/') {
			switch (peek()) {
				case '/':
					for (;;) {
						c = get();
						if (c <= '\n') {
							return c;
						}
					}

				case '*':
					get();
					for (;;) {
						switch (get()) {
							case '*':
								if (peek() == '/') {
									get();
									return ' ';
								}
								break;
							case EOF:
								throw new UnterminatedCommentException();
						}
					}

				default:
					return c;
			}

		}
		return c;
	}

	/**
	 * action -- do something! What you do is determined by the argument: 1 Output A. Copy B to A. Get the next B. 2
	 * Copy B to A. Get the next B. (Delete A). 3 Get the next B. (Delete B). action treats a string as a single
	 * character. Wow! action recognizes a regular expression if it is preceded by ( or , or =.
	 */
	void action(final int d) throws IOException, UnterminatedRegExpLiteralException, UnterminatedCommentException, UnterminatedStringLiteralException {
		switch (d) {
			case 1:
				out.write(theA);
			case 2:
				theA = theB;

				if (theA == '\'' || theA == '"') {
					for (;;) {
						out.write(theA);
						theA = get();
						if (theA == theB) {
							break;
						}
						if (theA <= '\n') {
							throw new UnterminatedStringLiteralException();
						}
						if (theA == '\\') {
							out.write(theA);
							theA = get();
						}
					}
				}

			case 3:
				theB = next();
				if (theB == '/' && (theA == '(' || theA == ',' || theA == '=')) {
					out.write(theA);
					out.write(theB);
					for (;;) {
						theA = get();
						if (theA == '/') {
							break;
						} else if (theA == '\\') {
							out.write(theA);
							theA = get();
						} else if (theA <= '\n') {
							throw new UnterminatedRegExpLiteralException();
						}
						out.write(theA);
					}
					theB = next();
				}
		}
	}

	/**
	 * jsmin -- Copy the input to the output, deleting the characters which are insignificant to JavaScript. Comments
	 * will be removed. Tabs will be replaced with spaces. Carriage returns will be replaced with linefeeds. Most spaces
	 * and linefeeds will be removed.
	 */
	public void jsmin() throws IOException, UnterminatedRegExpLiteralException, UnterminatedCommentException, UnterminatedStringLiteralException {
		theA = '\n';
		action(3);
		while (theA != EOF) {
			switch (theA) {
				case ' ':
					if (isAlphanum(theB)) {
						action(1);
					} else {
						action(2);
					}
					break;
				case '\n':
					switch (theB) {
						case '{':
						case '[':
						case '(':
						case '+':
						case '-':
							action(1);
							break;
						case ' ':
							action(3);
							break;
						default:
							if (isAlphanum(theB)) {
								action(1);
							} else {
								action(2);
							}
					}
					break;
				default:
					switch (theB) {
						case ' ':
							if (isAlphanum(theA)) {
								action(1);
								break;
							}
							action(3);
							break;
						case '\n':
							switch (theA) {
								case '}':
								case ']':
								case ')':
								case '+':
								case '-':
								case '"':
								case '\'':
									action(1);
									break;
								default:
									if (isAlphanum(theA)) {
										action(1);
									} else {
										action(3);
									}
							}
							break;
						default:
							action(1);
							break;
					}
			}
		}
		out.flush();
	}

	class UnterminatedCommentException extends Exception {
	}

	class UnterminatedStringLiteralException extends Exception {
	}

	class UnterminatedRegExpLiteralException extends Exception {
	}

	public static void main(final String arg[]) {
		try {
			final JSMin jsmin = new JSMin(new FileInputStream(arg[0]), System.out);
			jsmin.jsmin();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final UnterminatedRegExpLiteralException e) {
			e.printStackTrace();
		} catch (final UnterminatedCommentException e) {
			e.printStackTrace();
		} catch (final UnterminatedStringLiteralException e) {
			e.printStackTrace();
		}
	}

}
