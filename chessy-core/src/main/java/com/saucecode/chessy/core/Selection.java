package com.saucecode.chessy.core;

public class Selection {
	
	public static final int MIN = 0;
	
	// inclusive
	public static final int MAX = 7;

	private final int x;

	private final int y;

	public Selection(int x, int y) {
		if (x < MIN || y < MIN || x > MAX || y > MAX) {
			throw new IllegalArgumentException("invalid values");
		}
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return "Selection [x=" + x + ", y=" + y + "]";
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Selection other = (Selection) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
