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

		/**
		 * Describes that this field has been selected by a player for a move.
		 */
		SELECTED,

		/**
		 * Describes that a figure has been moved from this field.
		 */
		FROM,

		/**
		 * Describes that a figure has been moved to this field.
		 */
		TO,

		/**
		 * No modifier has been applied to this field.
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
		 */
		BISHOP_BLACK, 
		
		/**
		 * White bishop.
		 */
		BISHOP_WHITE, 
		
		/**
		 * Black King.
		 */
		KING_BLACK, 
		
		/**
		 * White King.
		 */
		KING_WHITE, 
		
		/**
		 * Black Knight.
		 */
		KNIGHT_BLACK,
		
		/**
		 * White Knight.
		 */
		KNIGHT_WHITE, 
		
		/**
		 * Black Pawn.
		 */
		PAWN_BLACK, 
		
		/**
		 * White Pawn.
		 */
		PAWN_WHITE,
		
		/**
		 * Black Queen.
		 */
		QUEEN_BLACK,
		
		/**
		 * White Queen.
		 */
		QUEEN_WHITE,
		
		/**
		 * Black Rook.
		 */
		ROOK_BLACK, 
		
		/**
		 * White Rook.
		 */
		ROOK_WHITE, 
		
		/**
		 * No figure.
		 */
		NONE
		
	}

}
