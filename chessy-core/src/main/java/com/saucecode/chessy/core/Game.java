package com.saucecode.chessy.core;

import java.util.Stack;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

	/**
	 * Saves all previous boards.
	 */
	private Stack<Board> history;

	private final SimpleIntegerProperty boardValueWhite = new SimpleIntegerProperty();

	private final SimpleIntegerProperty boardValueBlack = new SimpleIntegerProperty();

	private final SimpleObjectProperty<Board> board = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty busy = new SimpleBooleanProperty(BUSY_STD);

	private final SimpleObjectProperty<Selection> selection = new SimpleObjectProperty<>();

	private final SimpleIntegerProperty ply = new SimpleIntegerProperty(PLY_STD);

	private final SimpleBooleanProperty blackAI = new SimpleBooleanProperty(BLACK_AI_STD);

	private final SimpleObjectProperty<Player> inCheck = new SimpleObjectProperty<>();

	private final SimpleObjectProperty<Player> inStalemate = new SimpleObjectProperty<>();
	
	private final SimpleObjectProperty<Player> inCheckmate = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty gameOver = new SimpleBooleanProperty(false);

	private final SimpleObjectProperty<Player> currentPlayer = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty resettable = new SimpleBooleanProperty(false);

	private final SimpleBooleanProperty undoable = new SimpleBooleanProperty(false);
	
	private final SimpleDoubleProperty progress = new SimpleDoubleProperty();
	
	private final BooleanProperty castlingPossibleBlack = new SimpleBooleanProperty(); // TODO muss weg!
	
	private final BooleanProperty castlingPossibleWhite = new SimpleBooleanProperty(); // TODO muss weg!
	
	private final ObjectProperty<Selection> from = new SimpleObjectProperty<>(null);
	
	private final ObjectProperty<Selection> to = new SimpleObjectProperty<>(null);
	
	private final SimpleBooleanProperty multiThreaded = new SimpleBooleanProperty(MULTI_THREADED_STD);

	/**
	 * Creates a new game.
	 */
	public Game() {
		this.history = new Stack<Board>();


		undoable.bind(resettable);

		board.addListener(new ChangeListener<Board>() {

			@Override
			public void changed(ObservableValue<? extends Board> observable, Board oldValue, Board newValue) {
				Platform.runLater(() -> {
					boardValueWhite.set(board.get().getValue(Player.WHITE));
					boardValueBlack.set(board.get().getValue(Player.BLACK));
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

					resettable.set(history.size() > 0);
					
					from.set(board.get().getFrom());
					to.set(board.get().getTo());
				});
			}

		});

		blackAI.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println("oldValue: " + oldValue);
				System.out.println("newValue: " + newValue);
				System.out.println(board.get());
				if (board.get().getCurrentPlayer() == Player.BLACK) {
					move(ply.get());
				}
			}
		});
		
		busy.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					Platform.runLater(() -> progress.set(0.0)); // TODO beibelassen?
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
	public boolean move(int fromX, int fromY, int toX, int toY) {
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
				System.out.println(Thread.currentThread().getName() + " started");
				Platform.runLater(() -> busy.set(true)); // TODO sollte nicht sein
				Board temp = null;
				temp = board.get().getMax(ply, progress, multiThreaded.get());
				if (temp != null) {
					for (int i = 0; i < ply - 1; i++) {
						temp = temp.getPrevious();
					}
					history.push(board.get());
					board.set(temp);
				}
				Platform.runLater(() -> busy.set(false)); // TODO sollte nicht sein
				System.out.println(Thread.currentThread().getName() + " ended");
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
	public IntegerProperty boardValueWhiteProperty() {
		return boardValueWhite;
	}

	@Override
	public IntegerProperty boardValueBlackProperty() {
		return boardValueBlack;
	}

	@Override
	public ObjectProperty<Board> boardProperty() {
		return board;
	}

	@Override
	public BooleanProperty busyProperty() {
		return busy;
	}

	@Override
	public void select(Selection s) {
		if (s == null) {
			throw new NullPointerException("s must not be null");
		}

		// unselect
		if (s.equals(selection.get())) {
			selection.set(null);
		} else {
			int x = s.getX();
			int y = s.getY();

			if (board.get().getFigure(x, y) == null
					|| board.get().getFigure(x, y).getOwner() != board.get().getCurrentPlayer()) {
				// empty space clicked or enemy figure clicked

				if (selection.get() != null) { 
					// means a figure was clicked in previous click

					if (move(selection.get().getX(), selection.get().getY(), x, y)) {

						if (blackAIProperty().get() && board.get().getCurrentPlayer() == Player.BLACK) {

							move(ply.get());

						}
					}
				}
				selection.set(null);
			} else {
				// figure clicked
				if (board.get().getFigure(x, y).getOwner() == board.get().getCurrentPlayer()) {
					selection.set(new Selection(x, y));
				}
			}
		}

	}

	@Override
	public ObjectProperty<Selection> selectionProperty() {
		return selection;
	}

	@Override
	public IntegerProperty plyProperty() {
		return ply;
	}

	@Override
	public BooleanProperty blackAIProperty() {
		return blackAI;
	}

	@Override
	public void reset() {
		if (resettable.get()) {
			// TODO
		}
	}

	@Override
	public void undo() {
		// TODO check if busy
		if (history.size() > 0) {
			board.set(history.pop());
		}
	}

	@Override
	public ObjectProperty<Player> inCheckProperty() {
		return inCheck;
	}

	@Override
	public ObjectProperty<Player> inStalemateProperty() {
		return inStalemate;
	}

	@Override
	public ObjectProperty<Player> inCheckmateProperty() {
		return inCheckmate;
	}

	@Override
	public BooleanProperty gameOverProperty() {
		return gameOver; // TODO
	}

	@Override
	public ObjectProperty<Player> currentPlayerProperty() {
		return currentPlayer;
	}

	@Override
	public BooleanProperty resettable() {
		return resettable;
	}

	@Override
	public BooleanProperty undoable() {
		return undoable;
	}

	@Override
	public DoubleProperty progressProperty() {
		return progress;
	}

	@Override
	public BooleanProperty castlingPossibleBlackProperty() {
		return castlingPossibleBlack;
	}

	@Override
	public BooleanProperty castlingPossibleWhiteProperty() {
		return castlingPossibleWhite;
	}

	@Override
	public ObjectProperty<Selection> fromProperty() {
		return from;
	}

	@Override
	public ObjectProperty<Selection> toProperty() {
		return to;
	}
	
	@Override
	public BooleanProperty multiThreadedProperty() {
		return multiThreaded;
	}

}
