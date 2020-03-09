package com.saucecode.chessy.core;

import java.util.Stack;

/**
 * Represents a chess game.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Chess">https://en.wikipedia.org/
 *      wiki/Chess</a>
 * @author Torben Kr&uuml;ger
 */
public class Game {

	/**
	 * Saves all previous boards.
	 */
	private Stack<Board> history;

	/**
	 * The current board.
	 */
	private Board board;

	/**
	 * Creates a new game.
	 */
	public Game() {
		this.history = new Stack<Board>();
		this.board = new Board();
	}

	/**
	 * Moves a piece from one square to another square.
	 * 
	 * @throws IllegalArgumentException
	 *             if at least one of the arguments doesn't fit the dimension of
	 *             a chess game
	 * @param fromX
	 *            x origin
	 * @param fromY
	 *            y origin
	 * @param toX
	 *            x destiny
	 * @param toY
	 *            y destiny
	 * @return {@code true}, if move was valid
	 */
	public boolean move(int fromX, int fromY, int toX, int toY) {
		if (!isValid(fromX) || !isValid(fromY) || !isValid(toX) || !isValid(toY)) {
			throw new IllegalArgumentException("The dimensions have to be between 0 (inclusive) and 7 (inclusive).");
		}
		Board temp = board.move(fromX, fromY, toX, toY);
		if (temp != null) {
			history.push(board);
			board = temp;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Makes a move on the current board, using an AI.
	 * 
	 * @param ply
	 *            number of plies the A.I shall look ahead
	 * @return
	 * 		<ul>
	 *         <li>{@code true}, if the move was successful</li>
	 *         <li>{@code false}, if the current player is
	 *         <a href="https://en.wikipedia.org/wiki/Checkmate">checkmated</a>
	 *         </li>
	 *         <li>{@code false}, if the current player is
	 *         <a href="https://en.wikipedia.org/wiki/Stalemate">stalemated</a>
	 *         </li>
	 *         </ul>
	 */
	public boolean move(int ply) {
		Board temp = board.getMax(ply);
		if (temp != null) {
			for (int i = 0; i < ply - 1; i++) {
				temp = temp.getPrevious();
			}
			history.push(board);
			board = temp;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns whether a coordinate is valid or not.
	 * 
	 * @param coordinate
	 *            the coordinate which shall be checked
	 * @return {@code true}, if the coordinate is valid
	 */
	private boolean isValid(int coordinate) {
		return (coordinate >= 0) && (coordinate < 8);
	}

	/**
	 * Undos the last move.<br>
	 * If the game is already in the initial state, nothing happens.
	 */
	public void undo() {
		if (history.size() > 0) {
			board = history.pop();
		}
	}

	public Board getBoard() {
		return board;
	}

	@Override
	public String toString() {
		return board.toString();
	}

}
