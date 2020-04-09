package com.saucecode.chessy.core.figures;

import com.saucecode.chessy.core.FieldI;
import com.saucecode.chessy.core.Position;
import com.saucecode.chessy.core.logic.Board;
import com.saucecode.chessy.core.logic.Figure;
import com.saucecode.chessy.core.logic.Player;

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
	 * -1, if the current move hasn't been a en passant. Takes the x-value of
	 * the captured piece.
	 */
	private int enPassant;

	/**
	 * -1, if the current move hasn't been a start move (needed for en passant).
	 */
	private int startMove;

	public Pawn(Player owner, Board game, int x, int y) {
		super(owner, game, x, y);
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
		if (toX == this.x && board.getFigure(toX, toY) == null) {

			// if on start position, one and two in y is allowed
			if ((this.owner == Player.WHITE && y == 1) || (this.owner == Player.BLACK && y == 6)) {

				if ((this.owner == Player.WHITE && toY - 2 == this.y && this.board.getFigure(toX, toY - 1) == null) // wenn
																													// feld
																													// dazwischen
																													// frei
																													// ist
						|| (this.owner == Player.BLACK && toY + 2 == this.y
								&& this.board.getFigure(toX, toY + 1) == null)) {
					this.startMove = x;
					return true;
				}

				this.startMove = -1;
			}

			// if not on start position, one is allowed
			if ((this.owner == Player.WHITE && toY - 1 == this.y)
					|| (this.owner == Player.BLACK && toY + 1 == this.y)) {
				return true;
			}
		}

		// capture an opponents piece
		if (this.owner == Player.WHITE) {
			if (Math.abs(this.x - toX) == 1 && this.y + 1 == toY
					&& (board.getFigure(toX, toY) != null && board.getFigure(toX, toY).getOwner() == Player.BLACK)) {
				return true;
			}
		} else {
			if (Math.abs(this.x - toX) == 1 && this.y - 1 == toY
					&& (board.getFigure(toX, toY) != null && board.getFigure(toX, toY).getOwner() == Player.WHITE)) {
				return true;
			}
		}

		// en passant
		if (board.getCurrentPlayer() == Player.BLACK) {

			if (board.getMarker(Player.WHITE) == toX && y == 3 && this.owner == Player.BLACK) {
				enPassant = toX;
				return true;
			}
		} else {
			if (board.getMarker(Player.BLACK) == toX && y == 4 && this.owner == Player.WHITE) {
				enPassant = toX;
				return true;
			}
		}
		return false;
	}

	@Override
	protected Figure clone(Board board) {
		return new Pawn(this.owner, board, this.x, this.y);
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
	public com.saucecode.chessy.core.FieldI.FigureType getFigureType() {
		switch (owner) {
		case WHITE:
			return FieldI.FigureType.PAWN_WHITE;
		case BLACK:
			return FieldI.FigureType.PAWN_BLACK;
		default:
			throw new InternalError("no such enum");
		}
	}

	@Override
	public String getCode() {
		return "P" + super.getCode();
	}
	
}
