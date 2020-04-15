package com.saucecode.chessy.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * The Game Interface. Defines the API used for communication between model and
 * control / view.
 * 
 * @since 1.0.0
 * 
 * @author Torben Kr&uuml;ger
 */
public interface GameI {

	/**
	 * Standard value for {@link #plyProperty()}.
	 * 
	 * @since 1.0.0
	 */
	public static final int PLY_STD = 4;

	/**
	 * Minimum value for {@link #plyProperty()}.
	 * 
	 * @since 1.0.0
	 */
	public static final int PLY_MIN = 1;

	/**
	 * Maximum value for {@link #plyProperty()}.
	 * 
	 * @since 1.0.0
	 */
	public static final int PLY_MAX = 5;

	/**
	 * Default value for {@link #blackAIProperty()}.
	 * 
	 * @since 1.0.0
	 */
	public static final boolean BLACK_AI_STD = true;

	/**
	 * Default value for {@link #multiThreadedProperty()}.
	 * 
	 * @since 1.0.0
	 */
	public static final boolean MULTI_THREADED_STD = true;

	/**
	 * Returns the read-only board value white property.
	 * 
	 * @return read-only board value white property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyIntegerProperty boardValueWhiteProperty();

	/**
	 * Returns the read-only board value black property.
	 * 
	 * @return read-only board value black property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyIntegerProperty boardValueBlackProperty();

	/**
	 * Returns the read-only board value raw white property.
	 * 
	 * @return read-only board value raw white property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyIntegerProperty boardValueRawWhiteProperty();

	/**
	 * Returns the read-only board value raw black property.
	 * 
	 * @return read-only board value raw black property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyIntegerProperty boardValueRawBlackProperty();

	/**
	 * Returns the read-only busy property.
	 * 
	 * @return read-only busy property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyBooleanProperty busyProperty();

	/**
	 * Returns the read-only selection property.
	 * 
	 * @return read-only selection property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyObjectProperty<Position> selectionProperty();

	/**
	 * Returns the in check property.
	 * 
	 * @return in check property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyObjectProperty<Player> inCheckProperty();

	/**
	 * Returns the read-only in stalemate property.
	 * 
	 * @return read-only in stalemate property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyObjectProperty<Player> inStalemateProperty();

	/**
	 * Returns the read-only in checkmate property.
	 * 
	 * @return read-only in checkmate property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyObjectProperty<Player> inCheckmateProperty();

	/**
	 * Returns the read-only in gameover property.
	 * 
	 * @return read-only in gameover property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyBooleanProperty gameOverProperty();

	/**
	 * Returns the read-only current player property.
	 * 
	 * @return read-only current player property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyObjectProperty<Player> currentPlayerProperty();

	/**
	 * Returns the read-only reset enabled property.
	 * 
	 * @return read-only reset enabled property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyBooleanProperty resetEnabledProperty();

	/**
	 * Returns the read-only undo enabled property.
	 * 
	 * @return read-only undo enabled property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyBooleanProperty undoEnabledPropoerty();

	/**
	 * Returns the read-only progress property.
	 * 
	 * @return read-only progress property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyDoubleProperty progressProperty();

	/**
	 * Returns the read-only from property.
	 * 
	 * @return read-only from property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyObjectProperty<Position> fromProperty();

	/**
	 * Returns the read-only to property.
	 * 
	 * @return read-only to property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyObjectProperty<Position> toProperty();

	/**
	 * Returns the read-only calculated moves property.
	 * 
	 * @return read-only calculated moves property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyIntegerProperty calculatedMovesProperty();

	/**
	 * Returns the read-only calculation time property.
	 * 
	 * @return read-only calculation time property
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyLongProperty calculationTimeProperty();

	/**
	 * Returns the ply property.
	 * 
	 * @return ply property
	 * 
	 * @since 1.0.0
	 */
	public IntegerProperty plyProperty();

	/**
	 * Returns the read-only in field property for a given position.
	 * 
	 * @param position position
	 * 
	 * @return the read-only in field property for a given position
	 * 
	 * @throws NullPointerException if {@code position} is {@code null}
	 * 
	 * @since 1.0.0
	 */
	public ReadOnlyObjectProperty<FieldI> fieldProperty(Position position);

	/**
	 * Returns the read-only black a.i. property.
	 * 
	 * @return read-only black a.i. property
	 * 
	 * @since 1.0.0
	 */
	public BooleanProperty blackAIProperty();

	public BooleanProperty multiThreadedProperty();

	/**
	 * Performs an undo.
	 * 
	 * To have any effect, the value of {@link GameI#resetEnabledProperty()} has to
	 * be {@code true}.
	 * 
	 * @since 1.0.0
	 */
	// TODO throw exception if not possible?
	public void undo();

	/**
	 * Performs a reset. Afterwards it will be set to the same state like after
	 * calling the standard constructor.
	 * 
	 * To have any effect, the value of {@link GameI#resetEnabledProperty()} has to
	 * be {@code true}.
	 * 
	 * @since 1.0.0
	 */
	// TODO throw exception if not possible?
	public void reset();

	/**
	 * Selects a field.
	 * 
	 * <ul>
	 * <li>if no field was selected before and the position contains a figure
	 * belonging to the current player, it will be selected</li>
	 * <li>if the selected field is already selected, it will be unselected</li>
	 * <li>if no field was selected before and the position contains no figure
	 * belonging the current player, nothing will happen</li>
	 * <li>if a field was selected before and the position contains no figure
	 * belonging the current player, the previous selected field will be
	 * unselected</li>
	 * <li>if a field has been selected before and the position is a valid target, a
	 * move will be done. If the move was done by {@link Player#WHITE} and
	 * {@link #blackAIProperty()} is set {@code true}, an A.I. move will be
	 * performed for {@link Player#BLACK}.
	 * </ul>
	 * 
	 * @param position target position
	 * 
	 * @throws NullPointerException if {@code position} is {@code null}
	 * 
	 * @since 1.0.0
	 */
	public void select(Position position);
	
	/**
	 * The Players.
	 * 
	 * @since 1.0.0
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

		public char toChar() {
			switch (this) {
			case WHITE:
				return 'W';
			case BLACK:
				return 'B';
			default:
				throw new IllegalArgumentException("no such enum");
			}
		}

	}

}
