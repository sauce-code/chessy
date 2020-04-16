package com.saucecode.chessy.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Provides methods for reading data from the console.
 *
 * @author Torben Kr&uuml;ger
 */
public class Terminal {

	/**
	 * The reader for receiving data.
	 */
	private final BufferedReader console;

	/**
	 * Creates a new Terminal, which is ready for use.
	 */
	public Terminal() {
		console = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * Returns the next line from the console as a String.
	 *
	 * @return String read from the console
	 */
	public String readLine() {
		try {
			return console.readLine();
		} catch (final IOException e) {
			return "\n";
		}
	}

	/**
	 * Returns the next line from the console interpreted as an int. Only the first digits including a sign will be
	 * read.
	 *
	 * @return int read from the console
	 */
	public int readInt() {
		return parseInt(readLine());
	}

	/**
	 * Parses a String to an int.
	 *
	 * @param string the String to be parsed
	 * @return
	 *         <ul>
	 *         <li>the parsed int</li>
	 *         <li>{@code -1}, if an error occurred</li>
	 *         </ul>
	 */
	private int parseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (final NumberFormatException e) {
			return -1;
		}
	}

}
