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
	 * Player white is stalemated. It's a draw.
	 */
	STALEMATE_WHITE,

	/**
	 * Player black is stalemated. It's a draw.
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
	NONE,

}
