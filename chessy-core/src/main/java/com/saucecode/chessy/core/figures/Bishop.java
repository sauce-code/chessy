package com.saucecode.chessy.core.figures;

import com.saucecode.chessy.core.Board;
import com.saucecode.chessy.core.Figure;
import com.saucecode.chessy.core.Player;

public class Bishop extends Figure {

	/**
	 * The base value.
	 */
	private final static int VALUE = 330;

	/**
	 * The evaluation matrix.
	 */
	private final static int[][] EVAL = {
			// @formatter:off
			{ -20, -10, -10, -10, -10, -10, -10, -20 },
			{ -10,   5,  10,   0,   5,   0,   0, -10 },
			{ -10,   0,  10,  10,   5,   5,   0, -10 },
			{ -10,   0,  10,  10,  10,  10,   0, -10 },
			{ -10,   0,  10,  10,  10,  10,   0, -10 },
			{ -10,   0,  10,  10,   5,   5,   0, -10 },
			{ -10,   5,  10,   0,   5,   0,   0, -10 },
			{ -20, -10, -10, -10, -10, -10, -10, -20 }
			// @formatter:on
	};

	public Bishop(Player owner, Board game, int x, int y) {
		super(owner, game, x, y);
	}

	@Override
	public String toString() {
		return super.toString() + 'B';
	}

	@Override
	protected boolean isSquareReachable(int toX, int toY) {

		if (!super.isSquareReachable(toX, toY)) {
			return false;
		}

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

		return false;
	}

	@Override
	protected Figure clone(Board board) {
		return new Bishop(this.owner, board, this.x, this.y);
	}

	@Override
	public int getValue() {
		int value = Bishop.VALUE;
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

}
