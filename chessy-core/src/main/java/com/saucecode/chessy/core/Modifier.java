package com.saucecode.chessy.core;

/**
 * All possible Modiefiers.
 *
 * @since 1.0.0
 *
 * @author Torben Kr&uuml;ger
 */
public enum Modifier {

	/**
	 * Describes that this field has been selected by a player for a move.
	 *
	 * @since 1.0.0
	 */
	SELECTED,

	/**
	 * Describes that a figure has been moved from this field.
	 *
	 * @since 1.0.0
	 */
	FROM,

	/**
	 * Describes that a figure has been moved to this field.
	 *
	 * @since 1.0.0
	 */
	TO,

	/**
	 * No modifier has been applied to this field.
	 *
	 * @since 1.0.0
	 */
	NONE,

}