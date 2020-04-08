package com.saucecode.chessy.core.logic;

import com.saucecode.chessy.core.FieldI;
import com.saucecode.chessy.core.Position;

/**
 * Represents a figure.
 * 
 * @author Torben Kr&uuml;ger
 */
public abstract class Figure {

	/**
	 * The owner.
	 */
	protected Player owner;

	/**
	 * The board on which the figure is placed.
	 */
	protected Board board;

	/**
	 * The x-coordinate of this figure on the board.
	 */
	protected int x;

	/**
	 * The y-coordinate of this figure on the board.
	 */
	protected int y;

	protected Figure(Player owner, Board board, int x, int y) {
		this.owner = owner;
		this.board = board;
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the owner.
	 * 
	 * @return the owner
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Moves this figure to a desired location
	 * 
	 * @param toX
	 *            x-coordinate of target location
	 * @param toY
	 *            y-coordinate of target location
	 * @return
	 * 		<ul>
	 *         <li>the resulting board of the move</li>
	 *         <li>{@code null}, if the move is not valid</li>
	 *         </ul>
	 */
	public Board move(int toX, int toY) {
		Board ret = null;
		if (isSquareReachable(toX, toY)) {
			ret = board.clone();
			ret.resetMarker();
			ret.setFrom(new Position(x, y));
			ret.setTo(new Position(toX, toY));
			ret.setFigure(toX, toY, ret.removeFigure(x, y));
			ret.getFigure(toX, toY).setX(toX);
			ret.getFigure(toX, toY).setY(toY);
			ret.promote();
			if (ret.isInCheck(ret.getCurrentPlayer())) {
				ret = null;
			} else {
				ret.nextPlayer();
				ret.updateStatus();
				ret.evaluate();
			}
		}
		return ret;
	}

	/**
	 * Returns whether a square is reachable or not. <br>
	 * A square is not reachable if it contains a piece from the same owner or
	 * if it is not reachable according to the the chess rules.
	 * 
	 * @param toX
	 *            x-coordinate of target location
	 * @param toY
	 *            y-coordinate of target location
	 * @return {@code true}, if the move is valid
	 */
	protected boolean isSquareReachable(int x, int y) {
		return !this.hasSameOwner(x, y);
	}

	@Override
	public String toString() {
		return owner.toString();
	}

	protected abstract Figure clone(Board board);

	/**
	 * Returns the x-coordinate.
	 * 
	 * @return the x-coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x-coordinate.
	 * 
	 * @param x
	 *            x-coordinate
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns the y-coordinate.
	 * 
	 * @return the y-coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y-coordinate.
	 * 
	 * @param y
	 *            y-coordinate
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns if the piece has the same owner as the piece on the transfered
	 * coordinates
	 * 
	 * @param pieceX
	 *            x-coordinate
	 * @param pieceY
	 *            y-coordinate
	 * @return {@code true}, if both have the same owner
	 */
	protected boolean hasSameOwner(int pieceX, int pieceY) {
		return (board.getFigure(pieceX, pieceY) != null) && (owner == board.getFigure(pieceX, pieceY).getOwner());
	}

	/**
	 * Returns the value of this piece for a player.
	 */
	public abstract int getValue();
	
	public abstract FieldI.FigureType getFigureType();

}
