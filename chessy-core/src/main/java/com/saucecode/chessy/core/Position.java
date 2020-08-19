package com.saucecode.chessy.core;

/**
 * An implementation of {@link PositionI}.
 *
 * @see PositionI
 *
 * @author Torben Kr&uuml;ger
 */
public class Position implements PositionI {

	/**
	 * X-position. This value is always is range of {@link PositionI#MIN} (inclusive) and {@link PositionI#MAX}
	 * (inclusive).
	 */
	private final int x;

	/**
	 * Y-position. This value is always is range of {@link PositionI#MIN} (inclusive) and {@link PositionI#MAX}
	 * (inclusive).
	 */
	private final int y;

	/**
	 * Creates a new {@link Position}.
	 *
	 * @param x x-position
	 * @param y y-position
	 * @throws IllegalArgumentException if {@code x} or {@code y} are not in range of {@link PositionI#MIN} (inclusive)
	 *                                  and {@link PositionI#MAX} (inclusive)
	 */
	public Position(int x, int y) {
		if (x < MIN || y < MIN || x > MAX || y > MAX) {
			throw new IllegalArgumentException("invalid values");
		}
		this.x = x;
		this.y = y;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return (char) (x + 97) + Integer.toString(y + 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Position other = (Position) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

}
