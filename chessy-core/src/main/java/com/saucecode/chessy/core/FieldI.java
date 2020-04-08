package com.saucecode.chessy.core;

/**
 * Interface for value-holding fields, which represent one of each field on a
 * chessgame.
 * 
 * @since 1.0.0
 * 
 * @author tk
 */
public interface FieldI {

	/**
	 * Returns the figure, which is standing on this field.
	 * 
	 * @return figure, which is standing on this field
	 * 
	 * @since 1.0.0
	 */
	public FigureType getFigure();

	/**
	 * Returns the modifier for this field.
	 * 
	 * @return modifier for this field
	 * 
	 * @since 1.0.0
	 */
	public Modifier getModifier();

	/**
	 * All possible Modiefiers.
	 * 
	 * @since 1.0.0
	 * 
	 * @author Torben Kr&uuml;ger
	 */
	public enum Modifier {
		SELECTED, FROM, TO, NONE
	}

	/**
	 * All possible Figures.
	 * 
	 * @since 1.0.0
	 * 
	 * @author Torben Kr&uuml;ger
	 */
	public enum FigureType {
		BISHOP_BLACK, BISHOP_WHITE, KING_BLACK, KING_WHITE, KNIGHT_BLACK, KNIGHT_WHITE, PAWN_BLACK, PAWN_WHITE,
		QUEEN_BLACK, QUEEN_WHITE, ROOK_BLACK, ROOK_WHITE, NONE
	}

}
