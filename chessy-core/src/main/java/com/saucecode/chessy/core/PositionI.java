package com.saucecode.chessy.core;

/**
 * Interface for positions.
 * 
 * @since 1.0.0
 * 
 * @author Torben Kr&uuml;ger
 */
public interface PositionI {

	/**
	 * Minimum value (inclusive).
	 */
	public static final int MIN = 0;

	/**
	 * Maximum value (inclusive).
	 */
	public static final int MAX = 7;

	/**
	 * Returns x.
	 * 
	 * @return x, which is between {@link #MIN} (inclusive) and {@link #MAX}
	 *         (inclusive).
	 * 
	 * @since 1.0.0
	 */
	public int getX();

	/**
	 * Returns y.
	 * 
	 * @return y, which is between {@link #MIN} (inclusive) and {@link #MAX}
	 *         (inclusive).
	 * 
	 * @since 1.0.0
	 */
	public int getY();

}
