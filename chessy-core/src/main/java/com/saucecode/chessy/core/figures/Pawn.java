package com.saucecode.chessy.core.figures;

import com.saucecode.chessy.core.FigureType;
import com.saucecode.chessy.core.Player;
import com.saucecode.chessy.core.Position;
import com.saucecode.chessy.core.logic.Board;
import com.saucecode.chessy.core.logic.Figure;

/**
 * Represents a Pawn.
 *
 * @see Figure
 *
 * @author Torben Kr&uuml;ger
 */
public class Pawn extends Figure {

	/**
	 * The base value.
	 */
	private final static int VALUE = 100;

	/**
	 * The evaluation matrix.
	 */
	private final static int[][] EVAL = {
			// @formatter:off
			{   0,   5,   5,   0,   5,  10,  50,   0 },
			{   0,  10,  -5,   0,   5,  10,  50,   0 },
			{   0,  10, -10,   0,  10,  20,  50,   0 },
			{   0, -20,   0,  20,  25,  30,  50,   0 },
			{   0, -20,   0,  20,  25,  30,  50,   0 },
			{   0,  10, -10,   0,  10,  20,  50,   0 },
			{   0,  10,  -5,   0,   5,  10,  50,   0 },
			{   0,   5,   5,   0,   5,  10,  50,   0 }
			// @formatter:on
	};

	/**
	 * -1, if the current move hasn't been a en passant. Takes the x-value of the captured piece.
	 */
	private int enPassant;

	/**
	 * -1, if the current move hasn't been a start move (needed for en passant).
	 */
	private int startMove;

	/**
	 * Creates a new {@link Bishop}.
	 *
	 * @param owner owner
	 * @param board board, to which this figure belongs
	 * @param x     x-position
	 * @param y     y-position
	 */
	public Pawn(Player owner, Board board, int x, int y) {
		super(owner, board, x, y);
		enPassant = -1;
		startMove = -1;
	}

	@Override
	public String toString() {
		return super.toString() + 'P';
	}

	@Override
	protected boolean isSquareReachable(int toX, int toY) {

		if (!super.isSquareReachable(toX, toY)) {
			return false;
		}

		// walking straight forward
		if (toX == x && board.getFigure(toX, toY) == null) {

			// if on start position, one and two in y is allowed
			if ((owner == Player.WHITE && y == 1) || (owner == Player.BLACK && y == 6)) {

				if ((owner == Player.WHITE && toY - 2 == y && board.getFigure(toX, toY - 1) == null) // wenn
																										// feld
																										// dazwischen
																										// frei
																										// ist
						|| (owner == Player.BLACK && toY + 2 == y && board.getFigure(toX, toY + 1) == null)) {
					startMove = x;
					return true;
				}

				startMove = -1;
			}

			// if not on start position, one is allowed
			if ((owner == Player.WHITE && toY - 1 == y) || (owner == Player.BLACK && toY + 1 == y)) {
				return true;
			}
		}

		// capture an opponents piece
		if (owner == Player.WHITE) {
			if (Math.abs(x - toX) == 1 && y + 1 == toY
					&& (board.getFigure(toX, toY) != null && board.getFigure(toX, toY).getOwner() == Player.BLACK)) {
				return true;
			}
		} else {
			if (Math.abs(x - toX) == 1 && y - 1 == toY
					&& (board.getFigure(toX, toY) != null && board.getFigure(toX, toY).getOwner() == Player.WHITE)) {
				return true;
			}
		}

		// en passant
		if (board.getCurrentPlayer() == Player.BLACK) {

			if (board.getMarker(Player.WHITE) == toX && y == 3 && owner == Player.BLACK) {
				enPassant = toX;
				return true;
			}
		} else {
			if (board.getMarker(Player.BLACK) == toX && y == 4 && owner == Player.WHITE) {
				enPassant = toX;
				return true;
			}
		}
		return false;
	}

	@Override
	protected Figure clone(Board board) {
		return new Pawn(owner, board, x, y);
	}

	@Override
	public Board move(int toX, int toY) {
		Board ret = null;
		if (isSquareReachable(toX, toY)) {
			ret = board.clone();
			ret.resetMarker();
			ret.setFrom(new Position(x, y));
			ret.setTo(new Position(toX, toY));
			if (enPassant != -1) {
				ret.removeFigure(enPassant, y);
			}
			ret.setFigure(toX, toY, ret.removeFigure(x, y));
			ret.getFigure(toX, toY).setX(toX);
			ret.getFigure(toX, toY).setY(toY);
			ret.setMarker(owner, startMove);
			ret.promote();
			if (ret.isInCheck(ret.getCurrentPlayer())) {
				ret = null;
			} else {
				ret.nextPlayer();
				ret.updateStatus();
				ret.evaluate();
			}
		}
		enPassant = -1;
		return ret;
	}

	@Override
	public int getValue() {
		int value = Pawn.VALUE;
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
			return FigureType.PAWN_WHITE;
		case BLACK:
			return FigureType.PAWN_BLACK;
		default:
			throw new InternalError("no such enum");
		}
	}

	@Override
	public String getCode() {
		return "P" + super.getCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + enPassant;
		result = prime * result + startMove;
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
		final Pawn other = (Pawn) obj;
		if (enPassant != other.enPassant) {
			return false;
		}
		if (startMove != other.startMove) {
			return false;
		}
		return true;
	}

}
