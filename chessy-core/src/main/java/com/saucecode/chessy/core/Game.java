package com.saucecode.chessy.core;

import java.util.Stack;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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

	/**
	 * The current board.
	 */
	private Board board;

	private final SimpleStringProperty boardValueWhite = new SimpleStringProperty();

	private final SimpleStringProperty boardValueBlack = new SimpleStringProperty();

	private final SimpleObjectProperty<Board> boardProperty = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty isBusyProperty = new SimpleBooleanProperty(BUSY_STD);

	private final SimpleObjectProperty<Selection> selectionProperty = new SimpleObjectProperty<>();

	private final SimpleIntegerProperty plyProperty = new SimpleIntegerProperty(PLY_STD);

	private final SimpleBooleanProperty blackAIProperty = new SimpleBooleanProperty(BLACK_AI_STD);

	private final SimpleObjectProperty<Player> inCheckProperty = new SimpleObjectProperty<>();

	private final SimpleObjectProperty<Player> inStalemateProperty = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty gameOverProperty = new SimpleBooleanProperty(false);

	private final SimpleObjectProperty<Player> currentPlayer = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty resettable = new SimpleBooleanProperty(false);

	private final SimpleBooleanProperty undoable = new SimpleBooleanProperty(false);

	/**
	 * Creates a new game.
	 */
	public Game() {
		this.history = new Stack<Board>();
		this.board = new Board();

		undoable.bind(resettable);

		boardProperty.addListener(new ChangeListener<Board>() {

			@Override
			public void changed(ObservableValue<? extends Board> observable, Board oldValue, Board newValue) {
				Platform.runLater(() -> {
					boardValueWhite.set(Integer.toString(board.getValue(Player.WHITE)));
					boardValueBlack.set(Integer.toString(board.getValue(Player.BLACK)));
					switch (board.getCurrentState()) {
					case CHECK_BLACK:
						inCheckProperty.set(Player.BLACK);
						break;
					case CHECK_WHITE:
						inCheckProperty.set(Player.BLACK);
						break;
					case CHECKMATE_BLACK:
						// TODO
						break;
					case CHECKMATE_WHITE:
						// TODO
						break;
					case STALEMATE_BLACK:
						inStalemateProperty.set(Player.BLACK);
						break;
					case STALEMATE_WHITE:
						inStalemateProperty.set(Player.BLACK);
						break;
					case NONE:
						inCheckProperty.set(null);
						inStalemateProperty.set(null);
						break;
					default:
						throw new IllegalStateException("no such state");
					}
					currentPlayer.set(board.getCurrentPlayer());

					resettable.set(history.size() > 0);

				});
			}

		});

		blackAIProperty.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println("oldValue: " + oldValue);
				System.out.println("newValue: " + newValue);
				System.out.println(board);
				if (getBoard().getCurrentPlayer() == Player.BLACK) {
					move(plyProperty.get());
				}
			}
		});

		boardProperty.set(board);
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
		Board temp = board.move(fromX, fromY, toX, toY);
		if (temp != null) {
			history.push(board);
			board = temp;
			boardProperty.set(temp);
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
				isBusyProperty.set(true);
				Board temp = board.getMax(ply);
				if (temp != null) {
					for (int i = 0; i < ply - 1; i++) {
						temp = temp.getPrevious();
					}
					history.push(board);
					board = temp;
					boardProperty.set(temp);
				}
				isBusyProperty.set(false);
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
			board = history.pop();
			boardProperty.set(board);
		}
	}

	public Board getBoard() {
		return board;
	}

	@Override
	public String toString() {
		return board.toString();
	}

	@Override
	public SimpleStringProperty boardValueWhiteProperty() {
		return boardValueWhite;
	}

	@Override
	public SimpleStringProperty boardValueBlackProperty() {
		return boardValueBlack;
	}

	@Override
	public SimpleObjectProperty<Board> boardProperty() {
		return boardProperty;
	}

	@Override
	public SimpleBooleanProperty busyProperty() {
		return isBusyProperty;
	}

	@Override
	public void select(Selection s) {
		if (s == null) {
			throw new NullPointerException("s must not be null");
		}

		// unselect
		if (s.equals(selectionProperty.get())) {
			selectionProperty.set(null);
		} else {
			int x = s.getX();
			int y = s.getY();

			if (getBoard().getFigure(x, y) == null
					|| getBoard().getFigure(x, y).getOwner() != getBoard().getCurrentPlayer()) {
				// empty space clicked or enemy figure clicked

				if (selectionProperty.get() != null) { // means a figure was
														// clicked in previous click
					// System.out.println("a figure was clicked in previous click
					// -->move!");
					// System.out.println("from: " + clickedX + "|" + clickedY + "
					// to " + x + "|" + y);

					if (move(selectionProperty.get().getX(), selectionProperty.get().getY(), x, y)) {

						if (blackAIProperty().get() && getBoard().getCurrentPlayer() == Player.BLACK) {

							move(plyProperty.get());

						}
					}
				}
				selectionProperty.set(null);
			} else {
				// figure clicked
				if (getBoard().getFigure(x, y).getOwner() == getBoard().getCurrentPlayer()) {
					selectionProperty.set(new Selection(x, y));
					System.out.println("figure clicked and saved");
				}
			}
		}

	}

	@Override
	public SimpleObjectProperty<Selection> selectionProperty() {
		return selectionProperty;
	}

	@Override
	public SimpleIntegerProperty plyProperty() {
		return plyProperty;
	}

	@Override
	public SimpleBooleanProperty blackAIProperty() {
		return blackAIProperty;
	}

	@Override
	public void reset() {
		if (resettable.get()) {
			// TODO
		}
	}

	@Override
	public SimpleObjectProperty<Player> inCheckProperty() {
		return inCheckProperty;
	}

	@Override
	public SimpleObjectProperty<Player> inStalemateProperty() {
		return inStalemateProperty;
	}

	@Override
	public SimpleBooleanProperty gameOverProperty() {
		return gameOverProperty; // TODO
	}

	@Override
	public SimpleObjectProperty<Player> currentPlayerProperty() {
		return currentPlayer;
	}

	@Override
	public SimpleBooleanProperty resettable() {
		return resettable;
	}

	@Override
	public SimpleBooleanProperty undoable() {
		return undoable;
	}

}
