package com.saucecode.chessy.core;

/**
 * Interface for value-holding fields, which represent one of each field on a
 * chessgame.
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
		NONE

	}

	/**
	 * All possible Figures.
	 * 
	 * @since 1.0.0
	 * 
	 * @author Torben Kr&uuml;ger
	 */
	public enum FigureType {

		/**
		 * Black bishop.
		 * 
		 * @since 1.0.0
		 */
		BISHOP_BLACK,

		/**
		 * White bishop.
		 * 
		 * @since 1.0.0
		 */
		BISHOP_WHITE,

		/**
		 * Black King.
		 * 
		 * @since 1.0.0
		 */
		KING_BLACK,

		/**
		 * White King.
		 * 
		 * @since 1.0.0
		 */
		KING_WHITE,

		/**
		 * Black Knight.
		 * 
		 * @since 1.0.0
		 */
		KNIGHT_BLACK,

		/**
		 * White Knight.
		 * 
		 * @since 1.0.0
		 */
		KNIGHT_WHITE,

		/**
		 * Black Pawn.
		 * 
		 * @since 1.0.0
		 */
		PAWN_BLACK,

		/**
		 * White Pawn.
		 * 
		 * @since 1.0.0
		 */
		PAWN_WHITE,

		/**
		 * Black Queen.
		 * 
		 * @since 1.0.0
		 */
		QUEEN_BLACK,

		/**
		 * White Queen.
		 * 
		 * @since 1.0.0
		 */
		QUEEN_WHITE,

		/**
		 * Black Rook.
		 * 
		 * @since 1.0.0
		 */
		ROOK_BLACK,

		/**
		 * White Rook.
		 * 
		 * @since 1.0.0
		 */
		ROOK_WHITE,

		/**
		 * No figure.
		 * 
		 * @since 1.0.0
		 */
		NONE

	}

}
