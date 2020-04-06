package com.saucecode.chessy.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;

/**
 * The Game Interface. Defines the API used for communication between model and
 * control / view.
 * 
 * @since 1.0.0
 * 
 * @author Torben Kr&uuml;ger
 *
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

	public static final int DIM = 8;

	public static final boolean BUSY_STD = false;

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

	public IntegerProperty boardValueWhiteProperty();

	public IntegerProperty boardValueBlackProperty();

	public IntegerProperty boardValueRawWhiteProperty();

	public IntegerProperty boardValueRawBlackProperty();

	public ObjectProperty<Board> boardProperty();

	public BooleanProperty busyProperty();

	public ObjectProperty<Position> selectionProperty();

	public IntegerProperty plyProperty();

	public BooleanProperty blackAIProperty();

	public ObjectProperty<Player> inCheckProperty();

	public ObjectProperty<Player> inStalemateProperty();

	public ObjectProperty<Player> inCheckmateProperty();

	public BooleanProperty gameOverProperty();

	public ObjectProperty<Player> currentPlayerProperty();

	public BooleanProperty resetEnabledProperty();

	public BooleanProperty undoEnabledPropoerty();

	public DoubleProperty progressProperty();

	public ObjectProperty<Position> fromProperty();

	public ObjectProperty<Position> toProperty();

	public BooleanProperty multiThreadedProperty();

	public IntegerProperty calculatedMovesProperty();

	public LongProperty calculationTimeProperty();

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

}
