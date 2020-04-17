package com.saucecode.chessy.core.figures;

import com.saucecode.chessy.core.FigureType;
import com.saucecode.chessy.core.Player;
import com.saucecode.chessy.core.Position;
import com.saucecode.chessy.core.logic.Board;
import com.saucecode.chessy.core.logic.Figure;
import com.saucecode.chessy.core.logic.State;

/**
 * Represents a King.
 *
 * @see Figure
 *
 * @author Torben Kr&uuml;ger
 */
public class King extends Figure {

	/**
	 * The base value.
	 */
	private final static int VALUE = 0;

	/**
	 * The evaluation matrix for mid game.
	 */
	private final static int[][] EVAL_MID_GAME = {
			// @formatter:off
			{  20,  20, -10, -20, -30, -30, -30, -30 },
			{  30,  20, -20, -30, -40, -40, -40, -40 },
			{  10,   0, -20, -30, -40, -40, -40, -40 },
			{   0,   0, -20, -40, -50, -50, -50, -50 },
			{   0,   0, -20, -40, -50, -50, -50, -50 },
			{  10,   0, -20, -30, -40, -40, -40, -40 },
			{  30,  20, -20, -30, -40, -40, -40, -40 },
			{  20,  20, -10, -20, -30, -30, -30, -30 }
			// @formatter:on
	};

	/**
	 * The evaluation matrix for late game..
	 */
	private final static int[][] EVAL_LATE_GAME = {
			// @formatter:off
			{ -50, -30, -30, -30, -30, -30, -30, -50 },
			{ -30, -30, -10, -10, -10, -10, -20, -40 },
			{ -30,   0,  20,  30,  30,  20, -10, -30 },
			{ -30,   0,  30,  40,  40,  30,   0, -20 },
			{   0,   0, -20, -40, -50, -50, -50, -50 },
			{  10,   0, -20, -30, -40, -40, -40, -40 },
			{  30,  20, -20, -30, -40, -40, -40, -40 },
			{  20,  20, -10, -20, -30, -30, -30, -30 }
			// @formatter:on
	};

	// private final static int[][] evalPositionWhiteMiddle = { { -30, -40, -40,
	// -50, -50, -40, -40, -30 },
	// { -30, -40, -40, -50, -50, -40, -40, -30 }, { -30, -40, -40, -50, -50,
	// -40, -40, -30 },
	// { -30, -40, -40, -50, -50, -40, -40, -30 }, { -20, -30, -30, -40, -40,
	// -30, -30, -20 },
	// { -10, -20, -20, -20, -20, -20, -20, -10 }, { 20, 20, 0, 0, 0, 0, 20, 20
	// },
	// { 20, 30, 10, 0, 0, 10, 30, 20 } };
	//
	// private final static int[][] evalPositionBlackMiddle = { { 20, 30, 10, 0,
	// 0, 10, 30, 20 },
	// { 20, 20, 0, 0, 0, 0, 20, 20 }, { -10, -20, -20, -20, -20, -20, -20, -10
	// },
	// { -20, -30, -30, -40, -40, -30, -30, -20 }, { -30, -40, -40, -50, -50,
	// -40, -40, -30 },
	// { -30, -40, -40, -50, -50, -40, -40, -30 }, { -30, -40, -40, -50, -50,
	// -40, -40, -30 },
	// { -30, -40, -40, -50, -50, -40, -40, -30 } };
	//
	// private final static int[][] evalPositionWhiteEnd = { { -50, -40, -30,
	// -20, -20, -30, -40, -50 },
	// { -30, -20, -10, 0, 0, -10, -20, -30 }, { -30, -10, 20, 30, 30, 20, -10,
	// -30 },
	// { -30, -10, 30, 40, 40, 30, -10, -30 }, { -30, -10, 30, 40, 40, 30, -10,
	// -30 },
	// { -30, -10, 20, 30, 30, 20, -10, -30 }, { -30, -30, 0, 0, 0, 0, -30, -30
	// },
	// { -50, -30, -30, -30, -30, -30, -30, -50 } };
	//
	// private final static int[][] evalPositionBlackEnd = { { -50, -30, -30,
	// -30, -30, -30, -30, -50 },
	// { -30, -30, 0, 0, 0, 0, -30, -30 }, { -30, -10, 20, 30, 30, 20, -10, -30
	// },
	// { -30, -10, 30, 40, 40, 30, -10, -30 }, { -30, -10, 30, 40, 40, 30, -10,
	// -30 },
	// { -30, -10, 20, 30, 30, 20, -10, -30 }, { -30, -20, -10, 0, 0, -10, -20,
	// -30 },
	// { -50, -40, -30, -20, -20, -30, -40, -50 } };

	/**
	 * Is {@code true}, if the king has already been moved in the game.
	 */
	private boolean hasBeenMoved;

	/**
	 * Creates a new {@link King}.
	 *
	 * @param owner owner
	 * @param board board, to which this figure belongs
	 * @param x     x-position
	 * @param y     y-position
	 */
	public King(Player owner, Board board, int x, int y) {
		super(owner, board, x, y);
		hasBeenMoved = false;
	}

	/**
	 * Creates a new {@link King}.
	 *
	 * @param owner        owner
	 * @param board        board, to which this figure belongs
	 * @param x            x-position
	 * @param y            y-position
	 * @param hasBeenMoved {@code true}, if this figure has been moved earlier
	 */
	public King(Player owner, Board board, int x, int y, boolean hasBeenMoved) {
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
		return super.toString() + 'K';
	}

	@Override
	protected boolean isSquareReachable(int toX, int toY) {
		if (!super.isSquareReachable(toX, toY)) {
			return false;
		}

		// castling rule to the right
		if (board.getCurrentState() != State.CHECK_BLACK && board.getCurrentState() != State.CHECK_WHITE
				&& !hasBeenMoved && (toX == 6) && (toY == y) && (board.getFigure(7, toY) != null)
				&& (board.getFigure(7, toY) instanceof Rook) && (!((Rook) board.getFigure(7, toY)).isHasBeenMoved())
				&& (board.getFigure(5, toY) == null) && (board.getFigure(6, toY) == null)) {
			final Board temp = board.clone();
			temp.setFigure(5, y, temp.removeFigure(4, y));
			temp.getFigure(5, y).setX(5);
			if (!temp.isInCheck(temp.getCurrentPlayer())) {
				return true;
			}
		}

		// castling rule to the left
		if (board.getCurrentState() != State.CHECK_BLACK && board.getCurrentState() != State.CHECK_WHITE
				&& !hasBeenMoved && (toX == 2) && (toY == y) && (board.getFigure(0, toY) != null)
				&& (board.getFigure(0, toY) instanceof Rook) && (!((Rook) board.getFigure(0, toY)).isHasBeenMoved())
				&& (board.getFigure(3, toY) == null) && (board.getFigure(2, toY) == null)
				&& (board.getFigure(1, toY) == null)) {
			final Board temp = board.clone();
			temp.setFigure(3, y, temp.removeFigure(4, y));
			temp.getFigure(3, y).setX(3);
			if (!temp.isInCheck(temp.getCurrentPlayer())) {
				return true;
			}
		}

		if ((Math.abs(toX - x) < 2) && (Math.abs(toY - y) < 2)) {
//			hasBeenMoved = true; TODO
			return true;
		} else {
			return false;
		}

	}

	@Override
	protected Figure clone(Board board) {
		return new King(owner, board, x, y, hasBeenMoved);
	}

	@Override
	public Board move(int toX, int toY) {
		Board ret = null;
		if (isSquareReachable(toX, toY)) {
			ret = board.clone();
			ret.resetMarker();
			ret.setFrom(new Position(x, y));
			ret.setTo(new Position(toX, toY));
			((King) ret.getFigure(x, y)).hasBeenMoved = true; // TODO stimmt das so?
			ret.setFigure(toX, toY, ret.removeFigure(x, y));
			ret.getFigure(toX, toY).setX(toX);
			ret.getFigure(toX, toY).setY(toY);
			if (Math.abs(toX - x) == 2) {
				if (toX == 2) {
					ret.setFigure(3, y, ret.removeFigure(0, y));
					ret.getFigure(3, y).setX(3);
				}
				if (toX == 6) {
					ret.setFigure(5, y, ret.removeFigure(7, y));
					ret.getFigure(5, y).setX(5);
				}
			}
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
	 * Calculates if the Game is close to the end. The game is close to the end if
	 * <ul>
	 * <li>Both sides have no queen.</li>
	 * <li>Not yet implemented: Every side which has a queen has additionally no other pieces or one minorpiece (B, N)
	 * maximum.</li>
	 * </ul>
	 *
	 * @return {@code true}, if the game is close to the end
	 */
	private boolean isLateGame() {
		// TODO implement second condition
		return (board.getFigure(Queen.class, Player.BLACK) == null
				&& board.getFigure(Queen.class, Player.WHITE) == null);
	}

	@Override
	public int getValue() {
		int value = King.VALUE;
		switch (owner) {
		case WHITE:
			if (isLateGame()) {
				value += EVAL_LATE_GAME[x][y];
			} else {
				value += EVAL_MID_GAME[x][y];
			}
			break;
		case BLACK:
			if (isLateGame()) {
				value += EVAL_LATE_GAME[x][7 - y];
			} else {
				value += EVAL_MID_GAME[x][7 - y];
			}
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
			return FigureType.KING_WHITE;
		case BLACK:
			return FigureType.KING_BLACK;
		default:
			throw new InternalError("no such enum");
		}
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
		final King other = (King) obj;
		if (hasBeenMoved != other.hasBeenMoved) {
			return false;
		}
		return true;
	}

	@Override
	public String getCode() {
		return "K" + super.getCode();
	}

}