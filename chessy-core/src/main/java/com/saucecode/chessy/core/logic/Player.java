package com.saucecode.chessy.core.logic;

/**
 * The Players.
 * 
 * @author Torben Kr&uuml;ger
 */
public enum Player { // TODO MOVE TO INTERFACE

	/**
	 * White.
	 */
	WHITE,

	/**
	 * Black.
	 */
	BLACK;

	@Override
	public String toString() {
		switch (this) {
		case WHITE:
			return "W";
		case BLACK:
			return "B";
		default:
			throw new IllegalArgumentException();
		}
	}

}
