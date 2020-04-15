package com.saucecode.chessy.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.saucecode.chessy.core.FieldI.FigureType;
import com.saucecode.chessy.core.FieldI.Modifier;
import com.saucecode.chessy.core.logic.Board;
import com.saucecode.chessy.core.logic.Player;
import com.saucecode.chessy.core.util.BoundedIntegerProperty;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

/**
 * Represents a chess game.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Chess">https://en.wikipedia.org/
 *      wiki/Chess</a>
 * @author Torben Kr&uuml;ger
 */
public class Game implements GameI {

	final static Logger logger = Logger.getLogger(Game.class);

	/**
	 * Saves all previous boards.
	 */
	private Stack<Board> history;

	private final ReadOnlyIntegerWrapper boardValueWhite = new ReadOnlyIntegerWrapper();

	private final ReadOnlyIntegerWrapper boardValueBlack = new ReadOnlyIntegerWrapper();

	private final ReadOnlyIntegerWrapper boardValueRawWhite = new ReadOnlyIntegerWrapper();

	private final ReadOnlyIntegerWrapper boardValueRawBlack = new ReadOnlyIntegerWrapper();

	private final SimpleObjectProperty<Board> board = new SimpleObjectProperty<>(); // TODO

	private final ReadOnlyBooleanWrapper busy = new ReadOnlyBooleanWrapper();

	private final ReadOnlyObjectWrapper<Position> selection = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyObjectWrapper<Player> inCheck = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyObjectWrapper<Player> inStalemate = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyObjectWrapper<Player> inCheckmate = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper();

	private final ReadOnlyObjectWrapper<Player> currentPlayer = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyBooleanWrapper resetEnabled = new ReadOnlyBooleanWrapper();

	private final ReadOnlyBooleanWrapper undoEnabled = new ReadOnlyBooleanWrapper();

	private final ReadOnlyDoubleWrapper progressProperty = new ReadOnlyDoubleWrapper();

	private final ReadOnlyObjectWrapper<Position> from = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyObjectWrapper<Position> to = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyIntegerWrapper calculatedMoves = new ReadOnlyIntegerWrapper();

	private final ReadOnlyLongWrapper calculationTime = new ReadOnlyLongWrapper();

	private final Map<Position, ReadOnlyObjectWrapper<FieldI>> fieldMap = new HashMap<>();

	private final SimpleBooleanProperty multiThreaded = new SimpleBooleanProperty(MULTI_THREADED_STD);

	private final SimpleBooleanProperty blackAI = new SimpleBooleanProperty(BLACK_AI_STD);

	private final BoundedIntegerProperty ply = new BoundedIntegerProperty(PLY_STD, PLY_MIN, PLY_MAX);

	/**
	 * Creates a new game.
	 */
	public Game() {
		this.history = new Stack<Board>();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Position pos = new Position(x, y);
				Field field = new Field(FigureType.NONE, Modifier.NONE);
				fieldMap.put(pos, new ReadOnlyObjectWrapper<FieldI>(field));
			}
		}

		undoEnabled.bind(resetEnabled);

		board.addListener(new ChangeListener<Board>() {

			@Override
			public void changed(ObservableValue<? extends Board> observable, Board oldValue, Board newValue) {
				Platform.runLater(() -> {
					boardValueWhite.set(board.get().getValue(Player.WHITE));
					boardValueBlack.set(board.get().getValue(Player.BLACK));
					boardValueRawWhite.set(board.get().getValueRaw(Player.WHITE));
					boardValueRawBlack.set(board.get().getValueRaw(Player.BLACK));
					switch (board.get().getCurrentState()) {
					case CHECK_BLACK:
						inCheck.set(Player.BLACK);
						break;
					case CHECK_WHITE:
						inCheck.set(Player.WHITE);
						break;
					case CHECKMATE_BLACK:
						inCheckmate.set(Player.BLACK);
						break;
					case CHECKMATE_WHITE:
						inCheckmate.set(Player.WHITE);
						break;
					case STALEMATE_BLACK:
						inStalemate.set(Player.BLACK);
						break;
					case STALEMATE_WHITE:
						inStalemate.set(Player.WHITE);
						break;
					case NONE:
						inCheck.set(null);
						inStalemate.set(null);
						break;
					default:
						throw new IllegalStateException("no such state");
					}
					currentPlayer.set(board.get().getCurrentPlayer());

					resetEnabled.set(history.size() > 0);

					from.set(board.get().getFrom());
					to.set(board.get().getTo());
					if (board.get().getFrom() != null) {
						int x = board.get().getFrom().getX();
						int y = board.get().getFrom().getY();
						FigureType figureType = (board.get().getBoard()[x][y] == null) ? FigureType.NONE
								: board.get().getBoard()[x][y].getFigureType();
						fieldMap.get(board.get().getFrom()).set(new Field(figureType, Modifier.FROM));
					}
					if (board.get().getTo() != null) {
						int x = board.get().getTo().getX();
						int y = board.get().getTo().getY();
						fieldMap.get(board.get().getTo())
								.set(new Field(board.get().getBoard()[x][y].getFigureType(), Modifier.TO));
					}

					for (int x = 0; x < 8; x++) {
						for (int y = 0; y < 8; y++) {
							Position pos = new Position(x, y);
							ReadOnlyObjectWrapper<FieldI> field = fieldMap.get(pos);
							FigureType figureType = FigureType.NONE;
							Modifier modifier = Modifier.NONE;
							if (board.get().getBoard()[x][y] != null) {
								figureType = board.get().getBoard()[x][y].getFigureType();
							}
							if (selection.get() != null && selection.get().equals(pos)) {
								modifier = Modifier.SELECTED;
							} else if (from.get() != null && from.get().equals(pos)) {
								modifier = Modifier.FROM;
							} else if (to.get() != null && to.get().equals(pos)) {
								modifier = Modifier.TO;
							}
							Field newField = new Field(figureType, modifier);
							if (field.get() == null) {
								logger.debug("changed: " + newField);
								field.set(newField);
							} else if (!field.get().equals(newField)) {
								logger.debug("changed: " + newField);
								field.set(newField);
							}
						}
					}
				});
			}

		});

		blackAI.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (board.get().getCurrentPlayer() == Player.BLACK) {
					move(ply.get());
				}
			}
		});

		busy.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					Platform.runLater(() -> progressProperty.set(0.0)); // TODO beibelassen?
				}
			}

		});

		board.set(new Board());
	}

	/**
	 * Moves a piece from one square to another square.
	 * 
	 * @throws IllegalArgumentException if at least one of the arguments doesn't fit
	 *                                  the dimension of a chess game
	 * @param fromX x origin
	 * @param fromY y origin
	 * @param toX   x destiny
	 * @param toY   y destiny
	 * @return {@code true}, if move was valid
	 */
	private boolean move(int fromX, int fromY, int toX, int toY) {
		Board temp = board.get().move(fromX, fromY, toX, toY);
		if (temp != null) {
			history.push(board.get());
			board.set(temp);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Makes a move on the current board, using an AI.
	 * 
	 * @param ply number of plies the A.I shall look ahead
	 * @return
	 *         <ul>
	 *         <li>{@code true}, if the move was successful</li>
	 *         <li>{@code false}, if the current player is
	 *         <a href="https://en.wikipedia.org/wiki/Checkmate">checkmated</a></li>
	 *         <li>{@code false}, if the current player is
	 *         <a href="https://en.wikipedia.org/wiki/Stalemate">stalemated</a></li>
	 *         </ul>
	 */
	private void move(int ply) {
		final Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				logger.info("started");
				long timeStart = System.currentTimeMillis();
				Platform.runLater(() -> busy.set(true)); // TODO sollte nicht sein
				Board temp = null;
				AtomicInteger count = new AtomicInteger();
				temp = board.get().getMax(ply, progressProperty, multiThreaded.get(), count);
				if (temp != null) {
					for (int i = 0; i < ply - 1; i++) {
						temp = temp.getPrevious();
					}
					history.push(board.get());
					board.set(temp);
				}
				long timeEnd = System.currentTimeMillis();
				long timeDiff = timeEnd - timeStart;
				Platform.runLater(() -> calculatedMoves.set(count.get()));
				Platform.runLater(() -> calculationTime.set(timeDiff));
				Platform.runLater(() -> busy.set(false)); // TODO sollte nicht sein
				logger.info("finished, calculated a total of " + count + " possible moves in " + timeDiff + " ms");
				return null;
			}
		};
		Thread thread = new Thread(task);
		thread.start();
	}

	@Override
	public String toString() {
		return board.get().toString();
	}

	@Override
	public ReadOnlyIntegerProperty boardValueWhiteProperty() {
		return boardValueWhite.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyIntegerProperty boardValueBlackProperty() {
		return boardValueBlack.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyIntegerProperty boardValueRawWhiteProperty() {
		return boardValueRawWhite.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyIntegerProperty boardValueRawBlackProperty() {
		return boardValueRawBlack.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyBooleanProperty busyProperty() {
		return busy.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Position> selectionProperty() {
		return selection.getReadOnlyProperty();
	}

	@Override
	public BooleanProperty blackAIProperty() {
		return blackAI;
	}

	@Override
	public ReadOnlyObjectProperty<Player> inCheckProperty() {
		return inCheck.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Player> inStalemateProperty() {
		return inStalemate.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Player> inCheckmateProperty() {
		return inCheckmate.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyBooleanProperty gameOverProperty() {
		return gameOver.getReadOnlyProperty(); // TODO
	}

	@Override
	public ReadOnlyObjectProperty<Player> currentPlayerProperty() {
		return currentPlayer.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyBooleanProperty resetEnabledProperty() {
		return resetEnabled.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyBooleanProperty undoEnabledPropoerty() {
		return undoEnabled.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyDoubleProperty progressProperty() {
		return progressProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Position> fromProperty() {
		return from.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Position> toProperty() {
		return to.getReadOnlyProperty();
	}

	@Override
	public BooleanProperty multiThreadedProperty() {
		return multiThreaded;
	}

	@Override
	public IntegerProperty plyProperty() {
		return ply;
	}

	@Override
	public ReadOnlyIntegerProperty calculatedMovesProperty() {
		return calculatedMoves.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyLongProperty calculationTimeProperty() {
		return calculationTime.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<FieldI> fieldProperty(Position position) {
		Objects.requireNonNull(position, "position must not be null");
		return fieldMap.get(position).getReadOnlyProperty();
	}

	@Override
	public void select(Position position) {
		Objects.requireNonNull(position, "position must not be null");

		// clicked selected field => unselect
		if (position.equals(selection.get())) {
			unselect();
			
		} else {
			int x = position.getX();
			int y = position.getY();

			// empty space clicked or enemy figure clicked 
			if (board.get().getFigure(x, y) == null
					|| board.get().getFigure(x, y).getOwner() != board.get().getCurrentPlayer()) {

				// means a figure was clicked in previous click
				if (selection.get() != null) {

					if (move(selection.get().getX(), selection.get().getY(), x, y)) {

						if (blackAIProperty().get() && board.get().getCurrentPlayer() == Player.BLACK) {

							move(ply.get());

						}
					}
				}
				unselect();
			} else {
				// own figure clicked
				if (board.get().getFigure(x, y).getOwner() == board.get().getCurrentPlayer()) {
					if (selection.get() != null) {
						fieldMap.get(selection.get()).set(new Field(
								board.get().getBoard()[selection.get().getX()][selection.get().getY()].getFigureType(),
								Modifier.NONE));
					}
					selection.set(new Position(x, y));
					fieldMap.get(position)
							.set(new Field(board.get().getBoard()[position.getX()][position.getY()].getFigureType(),
									Modifier.SELECTED));
				}
			}
		}

	}

	private void unselect() {
		int x = selection.get().getX();
		int y = selection.get().getY();
		FigureType ft = FigureType.NONE;
		if (board.get().getBoard()[x][y] != null) {
			ft = board.get().getBoard()[x][y].getFigureType();
		}
		Field field = new Field(ft, Modifier.NONE);
		fieldMap.get(selection.get()).set(field);
		selection.set(null);
	}

	@Override
	public void reset() {
		// TODO check if busy
		if (resetEnabled.get()) {
			history.clear();
			board.set(new Board());
		}
	}

	@Override
	public void undo() {
		// TODO check if busy
		if (history.size() > 0) {
			board.set(history.pop());
		}
	}

}
