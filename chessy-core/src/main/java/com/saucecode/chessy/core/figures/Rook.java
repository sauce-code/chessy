package com.saucecode.chessy.core.figures;

import com.saucecode.chessy.core.FigureType;
import com.saucecode.chessy.core.Player;
import com.saucecode.chessy.core.logic.Board;
import com.saucecode.chessy.core.logic.Figure;

/**
 * Represents a Rook.
 *
 * @see Figure
 *
 * @author Torben Kr&uuml;ger
 */
public class Rook extends Figure {

	/**
	 * {@code true}, if the piece has already been moved in this game false if the piece has not been moved in this game
	 * so far.
	 */
	private boolean hasBeenMoved;

	/**
	 * The base value.
	 */
	private static final int VALUE = 500;

	/**
	 * The evaluation matrix.
	 */
	private final static int[][] EVAL = {
			// @formatter:off
			{   0,  -5,  -5,  -5,  -5,  -5,   5,   0 },
			{   0,   0,   0,   0,   0,   0,  10,   0 },
			{   0,   0,   0,   0,   0,   0,  10,   0 },
			{   5,   0,   0,   0,   0,   0,  10,   0 },
			{   5,   0,   0,   0,   0,   0,  10,   0 },
			{   0,   0,   0,   0,   0,   0,  10,   0 },
			{   0,   0,   0,   0,   0,   0,  10,   0 },
			{   0,  -5,  -5,  -5,  -5,  -5,   5,   0 }
			// @formatter:on
	};

	/**
	 * Creates a new {@link Bishop}.
	 *
	 * @param owner owner
	 * @param board board, to which this figure belongs
	 * @param x     x-position
	 * @param y     y-position
	 */
	public Rook(Player owner, Board board, int x, int y) {
		super(owner, board, x, y);
		hasBeenMoved = false;
	}

	/**
	 * Creates a new {@link Bishop}.
	 *
	 * @param owner        owner
	 * @param board        board, to which this figure belongs
	 * @param x            x-position
	 * @param y            y-position
	 * @param hasBeenMoved {@code true}, if this figure has been moved earlier
	 */
	private Rook(Player owner, Board board, int x, int y, boolean hasBeenMoved) {
		super(owner, board, x, y);
		this.hasBeenMoved = hasBeenMoved;
	}

	/**
	 * Returns whether this figure has been moved.
	 *
	 * @return {@code true}, if this figure has been moved
	 */
	public boolean isHasBeenMoved() {
		return hasBeenMoved;
	}

	@Override
	public String toString() {
		return super.toString() + 'R';
	}

	@Override
	protected boolean isSquareReachable(int toX, int toY) {

		if (!super.isSquareReachable(toX, toY)) {
			return false;
		}

		if (toX == x) { // move on y-axis

			// check if there are figures in between
			int i = Math.min(y, toY);
			final int toI = Math.max(y, toY);

			for (i = i + 1; i < toI; i++) {
				if (board.getFigure(x, i) != null) {
					return false;
				}
			}

			// hasBeenMoved = true;
			return true;

		} else if (toY == y) { // move on x-axis

			// check if there are figures in between
			int i = Math.min(x, toX);
			final int toI = Math.max(x, toX);

			for (i = i + 1; i < toI; i++) {
				if (board.getFigure(i, y) != null) {
					return false;
				}
			}
			// hasBeenMoved = true;
			return true;
		}

		return false;
	}

	@Override
	public Board move(int toX, int toY) {
		final Board temp = super.move(toX, toY);
		if (temp != null && temp.getFigure(toX, toY) != null) {
			((Rook) temp.getFigure(toX, toY)).hasBeenMoved = true;
			temp.updateStatus();
			temp.evaluate();
		}

		return temp;
	}

	@Override
	protected Figure clone(Board board) {
		return new Rook(owner, board, x, y, hasBeenMoved);
	}

	@Override
	public int getValue() {
		int value = Rook.VALUE;
		switch (owner) {
		case WHITE:
			value += EVAL[x][y];
			break;
		case BLACK:
			value += EVAL[x][7 - y];
			break;
		default:
			throw new IllegalArgumentException();
		}
		return value;
	}

	@Override
	public com.saucecode.chessy.core.FigureType getFigureType() {
		switch (owner) {
		case WHITE:
			return FigureType.ROOK_WHITE;
		case BLACK:
			return FigureType.ROOK_BLACK;
		default:
			throw new InternalError("no such enum");
		}
	}

	@Override
	public String getCode() {
		return "R" + super.getCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (hasBeenMoved ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Rook other = (Rook) obj;
		if (hasBeenMoved != other.hasBeenMoved) {
			return false;
		}
		return true;
	}

}
