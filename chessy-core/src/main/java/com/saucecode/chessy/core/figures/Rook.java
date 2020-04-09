package com.saucecode.chessy.core.figures;

import com.saucecode.chessy.core.FieldI;
import com.saucecode.chessy.core.logic.Board;
import com.saucecode.chessy.core.logic.Figure;
import com.saucecode.chessy.core.logic.Player;

public class Rook extends Figure {

	/**
	 * true if the piece has already been moved in this game false if the piece
	 * has not been moved in this game so far
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

	public Rook(Player owner, Board game, int x, int y) {
		super(owner, game, x, y);
		hasBeenMoved = false;
	}

	private Rook(Player owner, Board game, int x, int y, boolean hasBeenMoved) {
		super(owner, game, x, y);
		this.hasBeenMoved = hasBeenMoved;
	}

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
			int toI = Math.max(y, toY);

			for (i = i + 1; i < toI; i++) {
				if (this.board.getFigure(x, i) != null) {
					return false;
				}
			}

			//hasBeenMoved = true;
			return true;

		} else if (toY == y) { // move on x-axis

			// check if there are figures in between
			int i = Math.min(x, toX);
			int toI = Math.max(x, toX);

			for (i = i + 1; i < toI; i++) {
				if (this.board.getFigure(i, y) != null) {
					return false;
				}
			}
			//hasBeenMoved = true;
			return true;
		}

		return false;
	}

	@Override 
	public Board move(int toX, int toY) {
		Board temp = super.move(toX, toY);
		if (temp != null && temp.getFigure(toX, toY) != null ) {
			((Rook)temp.getFigure(toX, toY)).hasBeenMoved = true;	
			temp.updateStatus();
			temp.evaluate();
		}
		
		return temp;
	}
	
	@Override
	protected Figure clone(Board board) {
		return new Rook(this.owner, board, this.x, this.y, this.hasBeenMoved);
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
	public com.saucecode.chessy.core.FieldI.FigureType getFigureType() {
		switch (owner) {
		case WHITE:
			return FieldI.FigureType.ROOK_WHITE;
		case BLACK:
			return FieldI.FigureType.ROOK_BLACK;
		default:
			throw new InternalError("no such enum");
		}
	}
	
	@Override
	public String getCode() {
		return "R" + super.getCode();
	}

}
