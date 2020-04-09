package com.saucecode.chessy.core.figures;

import com.saucecode.chessy.core.FieldI;
import com.saucecode.chessy.core.logic.Board;
import com.saucecode.chessy.core.logic.Figure;
import com.saucecode.chessy.core.logic.Player;

public class Queen extends Figure {

	/**
	 * The base value.
	 */
	private final static int VALUE = 900;

	/**
	 * The evaluation matrix.
	 */
	private final static int[][] EVAL = {
			// @formatter:off
			{ -20, -10, -10,   0,  -5, -10, -10, -20 },
			{ -10,   0,   5,   0,   0,   0,   0, -10 },
			{ -10,   5,   5,   5,   5,   5,   0, -10 },
			{  -5,   0,   5,   5,   5,   5,   0,  -5 },
			{  -5,   0,   5,   5,   5,   5,   0,  -5 },
			{ -10,   0,   5,   5,   5,   5,   0, -10 },
			{ -10,   0,   0,   0,   0,   0,   0, -10 },
			{ -20, -10, -10,  -5,  -5, -10, -10, -20 }
			// @formatter:on
	};

	public Queen(Player owner, Board game, int x, int y) {
		super(owner, game, x, y);
	}

	@Override
	public String toString() {
		return super.toString() + 'Q';
	}

	@Override
	protected boolean isSquareReachable(int toX, int toY) {
		if (!super.isSquareReachable(toX, toY)) {
			return false;
		}

		// TODO: Check this very well!!!

		// like the bishop
		// check if the to-Position is on a diagonal
		int distanceX = Math.abs(toX - x);

		// check if the to-Position is on a diagonal
		if (distanceX == Math.abs(toY - y)) {

			// check if something is in between
			if (toX > x) {

				for (int i = 1; i < distanceX; i++) {

					if (toY > y && board.getFigure(x + i, y + i) != null) {
						return false;
					}

					if (toY < y && board.getFigure(x + i, y - i) != null) {
						return false;
					}
				}

			} else { // toX < x

				for (int i = 1; i < distanceX; i++) {

					if (toY > y && board.getFigure(x - i, y + i) != null) {
						return false;
					}

					if (toY < y && board.getFigure(x - i, y - i) != null) {
						return false;
					}

				}
			}

			// nothing is in between
			return true;
		}

		// like the rook
		if (toX - x == 0) { // move on y-axis

			// check if there are figures in between
			int i = (toY > y) ? y : toY;
			int toI = (toY > y) ? toY : y;

			for (i = i + 1; i < toI; i++) {
				if (this.board.getFigure(x, i) != null) {
					return false;
				}
			}

			return true;

		} else if (toY - y == 0) { // move on x-axis

			// check if there are figures in between
			int i = (toX > x) ? x : toX;
			int toI = (toX > x) ? toX : x;

			for (i = i + 1; i < toI; i++) {
				if (this.board.getFigure(i, y) != null) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	@Override
	protected Figure clone(Board board) {
		return new Queen(this.owner, board, this.x, this.y);
	}

	@Override
	public int getValue() {
		int value = Queen.VALUE;
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
	public com.saucecode.chessy.core.FieldI.FigureType getFigureType() {
		switch (owner) {
		case WHITE:
			return FieldI.FigureType.QUEEN_WHITE;
		case BLACK:
			return FieldI.FigureType.QUEEN_BLACK;
		default:
			throw new InternalError("no such enum");
		}
	}
	
	@Override
	public String getCode() {
		return "Q" + super.getCode();
	}

}
