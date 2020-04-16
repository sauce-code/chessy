package com.saucecode.chessy.core.logic;

/**
 * All possible states for a chess game.
 *
 * @author Torben Kr&uuml;ger
 */
public enum State {

	/**
	 * Player white is in check.
	 */
	CHECK_WHITE,

	/**
	 * Player black is in check.
	 */
	CHECK_BLACK,

	/**
	 * Player white stalemated. It's a draw.
	 */
	STALEMATE_WHITE,

	/**
	 * Player black stalemated. It's a draw.
	 */
	STALEMATE_BLACK,

	/**
	 * Player white is checkmated. Player black won.
	 */
	CHECKMATE_WHITE,

	/**
	 * Player black is checkmated. Player white won.
	 */
	CHECKMATE_BLACK,

	/**
	 * No special event occured.
	 */
	NONE;

	@Override
	public String toString() {
		switch (this) {
		case CHECK_WHITE:
			return "Player white is in check.";
		case CHECK_BLACK:
			return "Player black is in check.";
		case STALEMATE_WHITE:
			return "Player white is stalemated. It's a draw.";
		case STALEMATE_BLACK:
			return "Player black is stalemated. It's a draw.";
		case CHECKMATE_WHITE:
			return "Player white is checkmated. Player black won.";
		case CHECKMATE_BLACK:
			return "Player black is checkmated. Player white won.";
		case NONE:
			// TODO
			return "no state";
		default:
			throw new IllegalArgumentException();
		}
	}

}
