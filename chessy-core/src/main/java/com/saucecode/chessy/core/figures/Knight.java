package com.saucecode.chessy.core.figures;

import com.saucecode.chessy.core.FigureType;
import com.saucecode.chessy.core.Player;
import com.saucecode.chessy.core.logic.Board;
import com.saucecode.chessy.core.logic.Figure;

/**
 * Represents a Knight.
 *
 * @see Figure
 *
 * @author Torben Kr&uuml;ger
 */
public class Knight extends Figure {

	/**
	 * The base value.
	 */
	private static final int VALUE = 320;

	/**
	 * The evaluation matrix.
	 */
	private final static int[][] EVAL = {
			// @formatter:off
			{ -50, -40, -30, -30, -30, -30, -40, -50 },
			{ -40, -20,   5,   0,   5,   0, -20, -40 },
			{ -30,   0,  10,  15,  15,  10,   0, -30 },
			{ -30,   5,  15,  20,  20,  15,   0, -30 },
			{ -30,   5,  15,  20,  20,  15,   0, -30 },
			{ -30,   0,  10,  15,  15,  10,   0, -30 },
			{ -40, -20,   5,   0,   5,   0, -20, -40 },
			{ -50, -40, -30, -30, -30, -30, -40, -50 }
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
	public Knight(Player owner, Board board, int x, int y) {
		super(owner, board, x, y);
	}

	@Override
	public String toString() {
		return super.toString() + 'N';
	}

	@Override
	protected boolean isSquareReachable(int toX, int toY) {

		if (!super.isSquareReachable(toX, toY)) {
			return false;
		}

		final int absDistX = Math.abs(toX - x);
		final int absDistY = Math.abs(toY - y);
		return (absDistX == 2 && absDistY == 1) || (absDistX == 1 && absDistY == 2);
	}

	@Override
	protected Figure clone(Board board) {
		return new Knight(owner, board, x, y);
	}

	@Override
	public int getValue() {
		int value = Knight.VALUE;
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
	public int hashCode() {
		return super.hashCode();
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
		return true;
	}

	@Override
	public com.saucecode.chessy.core.FigureType getFigureType() {
		switch (owner) {
		case WHITE:
			return FigureType.KNIGHT_WHITE;
		case BLACK:
			return FigureType.KNIGHT_BLACK;
		default:
			throw new InternalError("no such enum");
		}
	}

	@Override
	public String getCode() {
		return "N" + super.getCode();
	}

}
