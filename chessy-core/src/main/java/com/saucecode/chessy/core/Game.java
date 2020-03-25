package com.saucecode.chessy.core;

import java.util.Stack;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

	private final SimpleStringProperty boardValueWhite = new SimpleStringProperty();

	private final SimpleStringProperty boardValueBlack = new SimpleStringProperty();

	private final SimpleObjectProperty<Board> board = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty busy = new SimpleBooleanProperty(BUSY_STD);

	private final SimpleObjectProperty<Selection> selection = new SimpleObjectProperty<>();

	private final SimpleIntegerProperty ply = new SimpleIntegerProperty(PLY_STD);

	private final SimpleBooleanProperty blackAI = new SimpleBooleanProperty(BLACK_AI_STD);

	private final SimpleObjectProperty<Player> inCheck = new SimpleObjectProperty<>();

	private final SimpleObjectProperty<Player> inStalemate = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty gameOver = new SimpleBooleanProperty(false);

	private final SimpleObjectProperty<Player> currentPlayer = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty resettable = new SimpleBooleanProperty(false);

	private final SimpleBooleanProperty undoable = new SimpleBooleanProperty(false);

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
					boardValueWhite.set(Integer.toString(board.get().getValue(Player.WHITE)));
					boardValueBlack.set(Integer.toString(board.get().getValue(Player.BLACK)));
					switch (board.get().getCurrentState()) {
					case CHECK_BLACK:
						inCheck.set(Player.BLACK);
						break;
					case CHECK_WHITE:
						inCheck.set(Player.BLACK);
						break;
					case CHECKMATE_BLACK:
						// TODO
						break;
					case CHECKMATE_WHITE:
						// TODO
						break;
					case STALEMATE_BLACK:
						inStalemate.set(Player.BLACK);
						break;
					case STALEMATE_WHITE:
						inStalemate.set(Player.BLACK);
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
				busy.set(true);
				Board temp = board.get().getMax(ply);
				if (temp != null) {
					for (int i = 0; i < ply - 1; i++) {
						temp = temp.getPrevious();
					}
					history.push(board.get());
					board.set(temp);
				}
				busy.set(false);
				System.out.println(Thread.currentThread().getName() + " ended");
				return null;
			}
		};
		Thread thread = new Thread(task);
		thread.start();
//		Board temp = board.getMax(ply);
//		if (temp != null) {
//			for (int i = 0; i < ply - 1; i++) {
//				temp = temp.getPrevious();
//			}
//			history.push(board);
//			board = temp;
//			boardProperty.set(temp);
//			return true;
//		} else {
//			return false;
//		}
	}

	/**
	 * Undos the last move.<br>
	 * If the game is already in the initial state, nothing happens.
	 */
	public void undo() {
		if (history.size() > 0) {
			board.set(history.pop());
		}
	}
	
	@Override
	public String toString() {
		return board.get().toString();
	}

	@Override
	public StringProperty boardValueWhiteProperty() {
		return boardValueWhite;
	}

	@Override
	public StringProperty boardValueBlackProperty() {
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

				if (selection.get() != null) { // means a figure was
														// clicked in previous click
					// System.out.println("a figure was clicked in previous click
					// -->move!");
					// System.out.println("from: " + clickedX + "|" + clickedY + "
					// to " + x + "|" + y);

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
					System.out.println("figure clicked and saved");
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
	public ObjectProperty<Player> inCheckProperty() {
		return inCheck;
	}

	@Override
	public ObjectProperty<Player> inStalemateProperty() {
		return inStalemate;
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

}
