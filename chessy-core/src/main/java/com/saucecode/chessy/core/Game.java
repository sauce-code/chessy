package com.saucecode.chessy.core;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.saucecode.chessy.core.logic.Board;
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
import javafx.concurrent.Task;

/**
 * Represents a chess game.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Chess">https://en.wikipedia.org/ wiki/Chess</a>
 *
 * @author Torben Kr&uuml;ger
 */
public class Game implements GameI {

	/**
	 * Static {@link Logger} instance for the class {@link Game}.
	 */
	final static Logger logger = Logger.getLogger(Game.class);

	/**
	 * Saves all previous boards.
	 */
	private final Stack<Board> history;

	private final ReadOnlyIntegerWrapper scoreWhite = new ReadOnlyIntegerWrapper();

	private final ReadOnlyIntegerWrapper scoreBlack = new ReadOnlyIntegerWrapper();

	private final ReadOnlyIntegerWrapper scoreWhiteTotal = new ReadOnlyIntegerWrapper();

	private final ReadOnlyIntegerWrapper scoreBlackTotal = new ReadOnlyIntegerWrapper();

	private final SimpleObjectProperty<Board> board = new SimpleObjectProperty<>(); // TODO

	private final ReadOnlyBooleanWrapper busy = new ReadOnlyBooleanWrapper(true);

	private final ReadOnlyObjectWrapper<Position> selection = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyObjectWrapper<Player> inCheck = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyObjectWrapper<Player> inStalemate = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyObjectWrapper<Player> inCheckmate = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper();

	private final ReadOnlyObjectWrapper<Player> currentPlayer = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyBooleanWrapper resetEnabled = new ReadOnlyBooleanWrapper();

	private final ReadOnlyBooleanWrapper undoEnabled = new ReadOnlyBooleanWrapper();

	private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();

	private final ReadOnlyObjectWrapper<Position> from = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyObjectWrapper<Position> to = new ReadOnlyObjectWrapper<>();

	private final ReadOnlyIntegerWrapper calculatedMoves = new ReadOnlyIntegerWrapper();

	private final ReadOnlyLongWrapper calculationTime = new ReadOnlyLongWrapper();

	private final HashMap<Position, ReadOnlyObjectWrapper<FieldI>> fieldMap = new HashMap<>();

	private final SimpleBooleanProperty multiThreaded = new SimpleBooleanProperty(MULTI_THREADED_STD);

	private final SimpleBooleanProperty aiBlackActive = new SimpleBooleanProperty(BLACK_AI_STD);

	private final BoundedIntegerProperty aiBlackPly = new BoundedIntegerProperty(PLY_STD, PLY_MIN, PLY_MAX);

	private final AtomicBoolean locked = new AtomicBoolean(true);
	
//	private final ReadOnlyBooleanWrapper locked = new ReadOnlyBooleanWrapper(true);
	
	private final AtomicBoolean aiMove = new AtomicBoolean();

	/**
	 * Creates a new game.
	 */
	public Game() {
		logger.debug("initializing new Game instance");
		history = new Stack<>();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				final Position pos = new Position(x, y);
				final Field field = new Field(FigureType.NONE, Modifier.NONE);
				fieldMap.put(pos, new ReadOnlyObjectWrapper<FieldI>(field));
			}
		}

		undoEnabled.bind(resetEnabled);

		board.addListener((ChangeListener<Board>) (observable, oldValue, newValue) -> Platform.runLater(() -> {
//			new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					
//				}
//			}.run();
			scoreWhite.set(board.get().getScore(Player.WHITE));
			scoreBlack.set(board.get().getScore(Player.BLACK));
			scoreWhiteTotal.set(board.get().getScoreTotal(Player.WHITE));
			scoreBlackTotal.set(board.get().getScoreTotal(Player.BLACK));
			switch (board.get().getCurrentState()) {
			case CHECK_BLACK:
				inCheck.set(Player.BLACK);
				inStalemate.set(null);
				inCheckmate.set(null);
				break;
			case CHECK_WHITE:
				inCheck.set(Player.WHITE);
				inStalemate.set(null);
				inCheckmate.set(null);
				break;
			case CHECKMATE_BLACK:
				inCheck.set(null);
				inStalemate.set(null);
				inCheckmate.set(Player.BLACK);
				break;
			case CHECKMATE_WHITE:
				inCheck.set(null);
				inStalemate.set(null);
				inCheckmate.set(Player.WHITE);
				break;
			case STALEMATE_BLACK:
				inCheck.set(null);
				inStalemate.set(Player.BLACK);
				inCheckmate.set(null);
				break;
			case STALEMATE_WHITE:
				inCheck.set(null);
				inStalemate.set(Player.WHITE);
				inCheckmate.set(null);
				break;
			case NONE:
				inCheck.set(null);
				inStalemate.set(null);
				inCheckmate.set(null);
				break;
			default:
				throw new IllegalStateException("no such state");
			}
			currentPlayer.set(newValue.getCurrentPlayer());

			resetEnabled.set(history.size() > 0);

			from.set(board.get().getFrom());
			to.set(board.get().getTo());
			if (board.get().getFrom() != null) {
				final int x1 = board.get().getFrom().getX();
				final int y1 = board.get().getFrom().getY();
				final FigureType figureType1 = (board.get().getBoard()[x1][y1] == null) ? FigureType.NONE
						: board.get().getBoard()[x1][y1].getFigureType();
				fieldMap.get(board.get().getFrom()).set(new Field(figureType1, Modifier.FROM));
			}
			if (board.get().getTo() != null) {
				final int x2 = board.get().getTo().getX();
				final int y2 = board.get().getTo().getY();
				fieldMap.get(board.get().getTo())
						.set(new Field(board.get().getBoard()[x2][y2].getFigureType(), Modifier.TO));
			}

			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					final Position pos = new Position(x, y);
					final ReadOnlyObjectWrapper<FieldI> field = fieldMap.get(pos);
					FigureType figureType2 = FigureType.NONE;
					Modifier modifier = Modifier.NONE;
					if (board.get().getBoard()[x][y] != null) {
						figureType2 = board.get().getBoard()[x][y].getFigureType();
					}
					if (selection.get() != null && selection.get().equals(pos)) {
						modifier = Modifier.SELECTED;
					} else if (from.get() != null && from.get().equals(pos)) {
						modifier = Modifier.FROM;
					} else if (to.get() != null && to.get().equals(pos)) {
						modifier = Modifier.TO;
					}
					final Field newField = new Field(figureType2, modifier);
					if (field.get() == null) {
						logger.debug("changed: " + newField);
						field.set(newField);
					} else if (!field.get().equals(newField)) {
						logger.debug("changed: " + newField);
						field.set(newField);
					}
				}
			}
			busy.set(false);
			if (!aiMove.get()) {
				unlock();
			}
		}));

		aiBlackActive.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (board.get().getCurrentPlayer() == Player.BLACK) {
				move(aiBlackPly.get());
			}
		});

		busy.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (!newValue) {
				Platform.runLater(() -> progress.set(0.0)); // TODO beibelassen?
			}
		});

		board.set(new Board());
	}

	/**
	 * Moves a piece from one square to another square.
	 *
	 * @throws IllegalArgumentException if at least one of the arguments doesn't fit the dimension of a chess game
	 * @param fromX x origin
	 * @param fromY y origin
	 * @param toX   x destiny
	 * @param toY   y destiny
	 * @return {@code true}, if move was valid
	 */
	private boolean move(int fromX, int fromY, int toX, int toY) {
		final Board temp = board.get().move(fromX, fromY, toX, toY);
		if (temp != null) {
			if (aiBlackActiveProperty().get() && board.get().getCurrentPlayer() == Player.WHITE) {
				aiMove.set(true);
			}
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
	 */
	private void move(int ply) {
		final Task<Void> task = new Task<>() {
			@Override
			protected Void call() throws Exception {
				logger.debug("started");
				final long timeStart = System.currentTimeMillis();
				Platform.runLater(() -> busy.set(true)); // TODO sollte nicht sein
				Board temp = null;
				final AtomicInteger count = new AtomicInteger();
				temp = board.get().getMax(ply, progress, multiThreaded.get(), count);
				if (temp != null) {
					for (int i = 0; i < ply - 1; i++) {
						temp = temp.getPrevious();
					}
					aiMove.set(false);
					history.push(board.get());
					board.set(temp);
				}
				final long timeEnd = System.currentTimeMillis();
				final long timeDiff = timeEnd - timeStart;
				Platform.runLater(() -> calculatedMoves.set(count.get()));
				Platform.runLater(() -> calculationTime.set(timeDiff));
				Platform.runLater(() -> busy.set(false)); // TODO sollte nicht sein
				String countString = new DecimalFormat().format(count).toString();
				logger.debug(
						"finished, calculated a total of " + countString + " possible moves in " + timeDiff + " ms");
//				unlock();
				return null;
			}
		};
		final Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public String toString() {
		return board.get().toString();
	}

	@Override
	public ReadOnlyIntegerProperty scoreWhiteProperty() {
		return scoreWhite.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyIntegerProperty scoreBlackProperty() {
		return scoreBlack.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyIntegerProperty scoreWhiteTotalProperty() {
		return scoreWhiteTotal.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyIntegerProperty scoreBlackTotalProperty() {
		return scoreBlackTotal.getReadOnlyProperty();
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
	public BooleanProperty aiBlackActiveProperty() {
		return aiBlackActive;
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
	
	public Player getCurrentPlayer() {
		return currentPlayer.get();
	}

	@Override
	public ReadOnlyBooleanProperty resetEnabledProperty() {
		return resetEnabled.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyBooleanProperty undoEnabledProperty() {
		return undoEnabled.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyDoubleProperty progressProperty() {
		return progress.getReadOnlyProperty();
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
	public IntegerProperty aiBlackPlyProperty() {
		return aiBlackPly;
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
	public boolean isLocked() {
		return locked.get();
	}

	private void lock() {
		boolean wasLocked = locked.getAndSet(true);
		if (wasLocked) {
			throw new IllegalStateException();
		}
		logger.debug("locked");
	}

	private void unlock() {
		locked.set(false);
		logger.debug("unlocked");
	}

	@Override
	public void undo() {
		lock();
		if (history.size() > 0) {
			unselect();
			board.set(history.pop());
		}
		unlock();
	}

	@Override
	public void reset() {
		lock();
		if (resetEnabled.get()) {
			history.clear();
			board.set(new Board());
		}
		unlock();
	}

	@Override
	public void select(Position position) {
		lock();
		// TODO if game over, do nothing?
		Objects.requireNonNull(position, "position must not be null");

		// clicked selected field => unselect
		if (position.equals(selection.get())) {
			unselect();
			unlock();

		} else {
			final int x = position.getX();
			final int y = position.getY();

			// empty space clicked or enemy figure clicked
			if (board.get().getFigure(x, y) == null
					|| board.get().getFigure(x, y).getOwner() != board.get().getCurrentPlayer()) {
				

				// means a figure was clicked in previous click
				if (selection.get() != null) {

					if (move(selection.get().getX(), selection.get().getY(), x, y)) {

						if (aiBlackActiveProperty().get() && board.get().getCurrentPlayer() == Player.BLACK) {

							move(aiBlackPly.get());

						} 
					}
				}
				unselect();
				if (!aiMove.get()) {
					unlock();
				}
			}

			// own figure clicked
			else {
//				if (board.get().getFigure(x, y).getOwner() == board.get().getCurrentPlayer()) {
					Platform.runLater(() -> {
						if (selection.get() != null) {
							fieldMap.get(selection.get()).set(new Field(
									board.get().getBoard()[selection.get().getX()][selection.get().getY()].getFigureType(),
									Modifier.NONE));
						}
						selection.set(new Position(x, y));
						fieldMap.get(position)
								.set(new Field(board.get().getBoard()[position.getX()][position.getY()].getFigureType(),
										Modifier.SELECTED));
						unlock();
					});
//				} 
//			else {
//					unlock();	
//				}
			}
		}

	}

	private void unselect() {
		if (selection.get() != null) {
			final int x = selection.get().getX();
			final int y = selection.get().getY();
			FigureType ft = FigureType.NONE;
			if (board.get().getBoard()[x][y] != null) {
				ft = board.get().getBoard()[x][y].getFigureType();
			}
			final Field field = new Field(ft, Modifier.NONE);
			fieldMap.get(selection.get()).set(field);
			selection.set(null);
		}
	}

	@Override
	// TODO include all values
	// TODO Exception if busy?
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((history == null) ? 0 : history.hashCode());
		return result;
	}

	@Override
	// TODO include all values
	// TODO Exception if busy?
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Game other = (Game) obj;
		if (history == null) {
			if (other.history != null) {
				return false;
			}
		} else if (!history.equals(other.history)) {
			return false;
		}
		return true;
	}

}
