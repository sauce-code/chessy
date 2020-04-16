package com.saucecode.chessy.core;

/**
 * Interface for value-holding fields, which represent one of each field on a chessgame.
 *
 * @since 1.0.0
 *
 * @author Torben Kr&uuml;ger
 */
public interface FieldI {

	/**
	 * Returns the figure, which is standing on this field.
	 *
	 * @return figure, which is standing on this field
	 *
	 * @since 1.0.0
	 */
	FigureType getFigure();

	/**
	 * Returns the modifier for this field.
	 *
	 * @return modifier for this field
	 *
	 * @since 1.0.0
	 */
	Modifier getModifier();

}
