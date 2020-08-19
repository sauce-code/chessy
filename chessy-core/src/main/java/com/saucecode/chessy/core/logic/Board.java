package com.saucecode.chessy.core.logic;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.saucecode.chessy.core.Player;
import com.saucecode.chessy.core.Position;
import com.saucecode.chessy.core.figures.Bishop;
import com.saucecode.chessy.core.figures.King;
import com.saucecode.chessy.core.figures.Knight;
import com.saucecode.chessy.core.figures.Pawn;
import com.saucecode.chessy.core.figures.Queen;
import com.saucecode.chessy.core.figures.Rook;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a board of a chess game.
 *
 * @author Torben Kr&uuml;ger
 */
public class Board {
	
	public static final int CHECKMATE_SCORE = 100_000;

	/**
	 * Static {@link Logger} instance for the class {@link Board}.
	 */
	final static Logger logger = Logger.getLogger(Board.class);

	/**
	 * The current state of the game.
	 */
	private State state;

	/**
	 * The current player.
	 */
	private Player currentPlayer;

	/**
	 * Stores the figures.
	 */
	private final Figure[][] figures;

	/**
	 * The previous board.
	 */
	private final Board previous;

	/**
	 * Saves the x-coordinate of a pawn that was moved 2 squares (beginning-move) by white in the last move. This is
	 * needed for 'en passant'.
	 */
	private int markerWhiteX;

	/**
	 * Saves the x-coordinate of a pawn that was moved 2 squares (beginning-move) by black in the last move. This is
	 * needed for 'en passant'.
	 */
	private int markerBlackX;

	/**
	 * White's score for this board.
	 */
	private int scoreWhiteTotal;

	/**
	 * Black's score for this board.
	 */
	private int scoreBlackTotal;

	/**
	 * From position.
	 */
	private Position from;

	/**
	 * To position.
	 */
	private Position to;

	/**
	 * Creates a new {@link Board} and spawns all {@link Figure}.
	 */
	public Board() {
		state = State.NONE;
		currentPlayer = Player.WHITE;
		figures = new Figure[8][8];
		// @formatter:off
		figures[0][0] = new   Rook(Player.WHITE, this, 0, 0);
		figures[1][0] = new Knight(Player.WHITE, this, 1, 0);
		figures[2][0] = new Bishop(Player.WHITE, this, 2, 0);
		figures[3][0] = new  Queen(Player.WHITE, this, 3, 0);
		figures[4][0] = new   King(Player.WHITE, this, 4, 0);
		figures[5][0] = new Bishop(Player.WHITE, this, 5, 0);
		figures[6][0] = new Knight(Player.WHITE, this, 6, 0);
		figures[7][0] = new   Rook(Player.WHITE, this, 7, 0);
		figures[0][1] = new   Pawn(Player.WHITE, this, 0, 1);
		figures[1][1] = new   Pawn(Player.WHITE, this, 1, 1);
		figures[2][1] = new   Pawn(Player.WHITE, this, 2, 1);
		figures[3][1] = new   Pawn(Player.WHITE, this, 3, 1);
		figures[4][1] = new   Pawn(Player.WHITE, this, 4, 1);
		figures[5][1] = new   Pawn(Player.WHITE, this, 5, 1);
		figures[6][1] = new   Pawn(Player.WHITE, this, 6, 1);
		figures[7][1] = new   Pawn(Player.WHITE, this, 7, 1);
		figures[0][6] = new   Pawn(Player.BLACK, this, 0, 6);
		figures[1][6] = new   Pawn(Player.BLACK, this, 1, 6);
		figures[2][6] = new   Pawn(Player.BLACK, this, 2, 6);
		figures[3][6] = new   Pawn(Player.BLACK, this, 3, 6);
		figures[4][6] = new   Pawn(Player.BLACK, this, 4, 6);
		figures[5][6] = new   Pawn(Player.BLACK, this, 5, 6);
		figures[6][6] = new   Pawn(Player.BLACK, this, 6, 6);
		figures[7][6] = new   Pawn(Player.BLACK, this, 7, 6);
		figures[0][7] = new   Rook(Player.BLACK, this, 0, 7);
		figures[1][7] = new Knight(Player.BLACK, this, 1, 7);
//		figures[2][4] = new Bishop(Player.BLACK, this, 2, 4);
		figures[2][7] = new Bishop(Player.BLACK, this, 2, 7);
		figures[3][7] = new  Queen(Player.BLACK, this, 3, 7);
//		figures[5][5] = new  Queen(Player.BLACK, this, 5, 5);
		figures[4][7] = new   King(Player.BLACK, this, 4, 7);
		figures[5][7] = new Bishop(Player.BLACK, this, 5, 7);
		figures[6][7] = new Knight(Player.BLACK, this, 6, 7);
		figures[7][7] = new   Rook(Player.BLACK, this, 7, 7);
		// @formatter:on
		previous = null;
		markerWhiteX = -1;
		markerBlackX = -1;
		evaluate();
	}

	/**
	 * Returns a clone from a specific board and sets that as its previous board.
	 *
	 * @param board mother instance, which will be cloned
	 */
	private Board(Board board) {
		state = board.state;
		currentPlayer = board.currentPlayer;
		figures = new Figure[8][8];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (board.figures[x][y] != null) {
					figures[x][y] = board.figures[x][y].clone(this);
				}
			}
		}
		previous = board;
	}

	/**
	 * Returns the board.
	 *
	 * @return the board
	 */
	public Figure[][] getBoard() {
		return figures;
	}

	/**
	 * Returns the figure of a certain tile.
	 *
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return
	 *         <ul>
	 *         <li>figure of the selected tile</li>
	 *         <li>{@code null}, if the tile is empty</li>
	 *         </ul>
	 */
	public Figure getFigure(int x, int y) {
		return figures[x][y];
	}

	/**
	 * Sets a figure on the board.
	 *
	 * @param x      x coordinate
	 * @param y      y coordinate
	 * @param figure the figure to be set
	 */
	public void setFigure(int x, int y, Figure figure) {
		figures[x][y] = figure;
	}

	/**
	 * Removes a piece.<br>
	 * If the selected square is empty, nothing happens.
	 *
	 * @param x x-coordinate of the square
	 * @param y y-coordinate of the square
	 * @return
	 *         <ul>
	 *         <li>the removed piece</li>
	 *         <li>{@code null}, if the square was empty</li>
	 *         </ul>
	 */
	public Figure removeFigure(int x, int y) {
		final Figure ret = figures[x][y];
		figures[x][y] = null;
		return ret;
	}

	/**
	 * Moves a figure to a square.
	 *
	 * @param fromX x-coordinate of origin
	 * @param fromY y-coordinate of origin
	 * @param toX   x-coordinate of destiny
	 * @param toY   y-coordinate of destiny
	 * @return
	 *         <ul>
	 *         <li>the resulting board</li>
	 *         <li>{@code null}, if the move is not valid</li>
	 *         </ul>
	 */
	public Board move(int fromX, int fromY, int toX, int toY) {
		Board ret = null;
		if (figures[fromX][fromY] != null && figures[fromX][fromY].getOwner() == currentPlayer) {
			ret = figures[fromX][fromY].move(toX, toY);
		}
		return ret;
	}

	/**
	 * Returns the current player.
	 *
	 * @return the current player
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Returns the current state.
	 *
	 * @return current state
	 */
	public State getCurrentState() {
		return state;
	}

	/**
	 * Sets the next player as the current palyer.
	 */
	public void nextPlayer() {
		currentPlayer = (currentPlayer == Player.WHITE ? Player.BLACK : Player.WHITE);
	}

	@Override
	public Board clone() {
		return new Board(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("current player: ");
		sb.append(currentPlayer);
		sb.append('\n');
		for (int y = 7; y >= 0; y--) {
			sb.append("+---+---+---+---+---+---+---+---+");
			sb.append('\n');
			for (int x = 0; x < 8; x++) {
				sb.append('|');
				if (figures[x][y] == null) {
					sb.append("   ");
				} else {
					sb.append(figures[x][y]);
					sb.append(' ');
				}
			}
			sb.append('|');
			sb.append(' ');
			sb.append(y + 1);
			sb.append('\n');
		}
		sb.append("+---+---+---+---+---+---+---+---+");
		sb.append('\n');
		sb.append("  A   B   C   D   E   F   G   H");
		return sb.toString();
	}

	/**
	 * Returns whether a player is in check or not.
	 *
	 * @param player the player to be tested
	 * @return {@code true}, if the player is in check
	 */
	public boolean isInCheck(Player player) {
		// TODO remove parameter player - unused
		final Figure king = getFigure(King.class, player);
		if (king != null) {
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					if ((figures[x][y] != null) && (figures[x][y].getOwner() != player)
							&& figures[x][y].isSquareReachable(king.getX(), king.getY())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns the {@link King} of a player.
	 *
	 * @param player owner of the king being searched
	 * @return
	 *         <ul>
	 *         <li>the king of the player</li>
	 *         <li>{@code null}, if the king doesn't exist</li>
	 *         </ul>
	 */
	public Figure getKing(Player player) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if ((figures[x][y] != null) && (figures[x][y] instanceof King)
						&& (figures[x][y].getOwner() == player)) {
					return figures[x][y];
				}
			}
		}
		return null;
	}

	/**
	 * Returns a figure of a player
	 *
	 * @param figure the type of piece which shall be searched for
	 * @param player the owner of the figure
	 * @return the figure of the player
	 */
	public Figure getFigure(Class<?> figure, Player player) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if ((figures[x][y] != null) && (figure.isInstance(figures[x][y]))
						&& (figures[x][y].getOwner() == player)) {
					return figures[x][y];
				}
			}
		}
		return null;
	}

	/**
	 * Returns the previous Board.
	 *
	 * @return the previous board
	 */
	public Board getPrevious() {
		return previous;
	}

	/**
	 * Checks the board for {@link Pawn} at the end of the board. If there are any, they will be promoted.
	 */
	public void promote() {
		for (int x = 0; x < 8; x++) {
			if (figures[x][0] != null && figures[x][0] instanceof Pawn) {
				figures[x][0] = new Queen(Player.BLACK, this, x, 0);
			}
		}
		for (int x = 0; x < 8; x++) {
			if (figures[x][7] != null && figures[x][7] instanceof Pawn) {
				figures[x][7] = new Queen(Player.WHITE, this, x, 7);
			}
		}
	}

	/**
	 * Returns the x-marker of a player.
	 *
	 * @param player the player
	 * @return the x-marker of the player
	 */
	public int getMarker(Player player) {
		switch (player) {
		case WHITE:
			return markerWhiteX;
		case BLACK:
			return markerBlackX;
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Sets the x-marker for a player.
	 *
	 * @param player the player
	 * @param marker the x-marker for the palyer
	 */
	public void setMarker(Player player, int marker) {
		switch (player) {
		case WHITE:
			markerWhiteX = marker;
			break;
		case BLACK:
			markerBlackX = marker;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Resets the x marker for the current player.
	 */
	public void resetMarker() {
		switch (currentPlayer) {
		case WHITE:
			markerWhiteX = -1;
			break;
		case BLACK:
			markerBlackX = -1;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Evaluates the board for both players and stores these values.
	 */
	public void evaluate() {
		scoreWhiteTotal = 0;
		scoreBlackTotal = 0;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (figures[x][y] != null) {
					switch (figures[x][y].getOwner()) {
					case WHITE:
						scoreWhiteTotal += figures[x][y].getValue();
						break;
					case BLACK:
						scoreBlackTotal += figures[x][y].getValue();
						break;
					default:
						throw new IllegalArgumentException();
					}
				}
			}
		}
	}

	/**
	 * Updates the current {@link State}.
	 */
	public void updateStatus() {
		if (isInCheck(currentPlayer)) {
			state = (currentPlayer == Player.WHITE) ? State.CHECK_WHITE : State.CHECK_BLACK;
			if (isInCheckmate(currentPlayer)) {
				state = (currentPlayer == Player.WHITE) ? State.CHECKMATE_WHITE : State.CHECKMATE_BLACK;
			}
		} else if (isInStalemate(currentPlayer)) {
			state = (currentPlayer == Player.WHITE) ? State.STALEMATE_WHITE : State.STALEMATE_BLACK;
		} else {
			state = State.NONE;
		}
	}

	/**
	 * Returns whether a player is in checkmate or not.<br>
	 * This method expects that <b>the current player is in check</b>.
	 *
	 * @param player the player to be checked
	 * @return {@code true}, if the player is in checkmate
	 */
	private boolean isInCheckmate(Player player) {
		// TODO remove parameter player - unused
		for (int fromX = 0; fromX < 8; fromX++) {
			for (int fromY = 0; fromY < 8; fromY++) {
				if (figures[fromX][fromY] != null && figures[fromX][fromY].getOwner() == currentPlayer) {
					for (int toX = 0; toX < 8; toX++) {
						for (int toY = 0; toY < 8; toY++) {
							final Board temp = figures[fromX][fromY].move(toX, toY);
							if (temp != null && !temp.isInCheck(currentPlayer)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Returns whether a player is in stalemate or not.<br>
	 * This method expects that <b>the current player is not in check</b>.
	 *
	 * @param player the player to be checked
	 * @return {@code true}, if the player is in stalemate
	 */
	private boolean isInStalemate(Player player) {
		// TODO remove parameter player - unused
		for (int fromX = 0; fromX < 8; fromX++) {
			for (int fromY = 0; fromY < 8; fromY++) {
				if (figures[fromX][fromY] != null && figures[fromX][fromY].getOwner() == currentPlayer) {
					for (int toX = 0; toX < 8; toX++) {
						for (int toY = 0; toY < 8; toY++) {
							if (figures[fromX][fromY].isSquareReachable(toX, toY)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Returns the value for the current player.
	 *
	 * @return value for the current palyer
	 */
	public int getScore() {
		return getScore(currentPlayer);
	}

	/**
	 * Returns the score of a player.
	 *
	 * @param player player, which's score shall be calculated
	 * @return score of a player
	 */
	public int getScore(Player player) {
		switch (state) {
		case CHECKMATE_BLACK:
			if (player == Player.WHITE) {
				return CHECKMATE_SCORE;
			} else {
				return -CHECKMATE_SCORE;
			}
		case CHECKMATE_WHITE:
			if (player == Player.WHITE) {
				return -CHECKMATE_SCORE;
			} else {
				return CHECKMATE_SCORE;
			}
		case STALEMATE_BLACK:
			return 0;
		case STALEMATE_WHITE:
			return 0;
		default:
			// TODO
			break;
		}
		if (player == Player.WHITE) {
			return scoreWhiteTotal - scoreBlackTotal;
		} else {
			return scoreBlackTotal - scoreWhiteTotal;
		}
	}

	/**
	 * Returns the total score of a player.
	 *
	 * @param player player, which's score shall be calculated
	 * @return total score of a player
	 */
	public int getScoreTotal(Player player) {
		if (player == Player.WHITE) {
			return scoreWhiteTotal;
		} else {
			return scoreBlackTotal;
		}
	}

	public Board getMax(int ply, DoubleProperty progress, boolean multiThreaded, AtomicInteger count) {
		if (isGameOVer()) {
			return chain(ply);
		}
		if (multiThreaded) {
			return getMaxMultiThreaded(ply, progress, count);
		} else {
			int figuresToMove = 0;

			progress.set(0.0); // TODO eventuell verschieben

			for (int fromX = 0; fromX < 8; fromX++) {
				for (int fromY = 0; fromY < 8; fromY++) {
					if (figures[fromX][fromY] != null && figures[fromX][fromY].getOwner() == currentPlayer) {
						figuresToMove++;
					}
				}
			}
			final double step = 1.0 / figuresToMove;
			Board max = null;
			Board temp;
			for (int fromX = 0; fromX < 8; fromX++) {
				for (int fromY = 0; fromY < 8; fromY++) {
					if (figures[fromX][fromY] != null && figures[fromX][fromY].getOwner() == currentPlayer) {
						for (int toX = 0; toX < 8; toX++) {
							for (int toY = 0; toY < 8; toY++) {
								temp = figures[fromX][fromY].move(toX, toY);
								if (temp != null) {
									count.incrementAndGet();
								}
								if ((temp != null) && (ply > 1)) {
									temp = temp.getMaxSingleThreaded(ply - 1, count);
								}
								if ((max == null) || ((temp != null)
										&& (temp.getScore(currentPlayer) > max.getScore(currentPlayer)))) {
									max = temp;
								}
							}
						}
						Platform.runLater(() -> progress.set(progress.get() + step));
					}
				}
			}
			return max;
		}
	}

	// funktioniert das so, wie es soll?
	private Board chain(int ply) {
		Board ret = clone();
		while (ply > 1) {
			ret = ret.clone();
			ret.nextPlayer();
			ply--;
		}
		return ret;
	}

	private Board getMaxSingleThreaded(int ply, AtomicInteger count) {
		if (isGameOVer()) {
			return chain(ply);
		}
		Board max = null;
		Board temp;
		for (int fromX = 0; fromX < 8; fromX++) {
			for (int fromY = 0; fromY < 8; fromY++) {
				if (figures[fromX][fromY] != null && figures[fromX][fromY].getOwner() == currentPlayer) {
					for (int toX = 0; toX < 8; toX++) {
						for (int toY = 0; toY < 8; toY++) {
							temp = figures[fromX][fromY].move(toX, toY);
							if (temp != null) {
								count.incrementAndGet();
							}
							if ((temp != null) && (ply > 1)) {
								temp = temp.getMaxSingleThreaded(ply - 1, count);
							}
							if ((max == null) || ((temp != null)
									&& (temp.getScore(currentPlayer) > max.getScore(currentPlayer)))) {
								max = temp;
							}
						}
					}
				}
			}
		}
		return max;
	}

	private Board getMaxMultiThreaded(int ply, DoubleProperty progress, AtomicInteger count) {
		if (isGameOVer()) {
			return chain(ply);
		}
		// TODO
		final ObjectProperty<Board> max = new SimpleObjectProperty<>(null);
		int threadCount = 0;

		progress.set(0.0); // TODO eventuell verschieben

		for (int fromX = 0; fromX < 8; fromX++) {
			for (int fromY = 0; fromY < 8; fromY++) {
				if (figures[fromX][fromY] != null && figures[fromX][fromY].getOwner() == currentPlayer) {
					threadCount++;
				}
			}
		}

		final CountDownLatch latch = new CountDownLatch(threadCount);

		for (int fromX = 0; fromX < 8; fromX++) {
			for (int fromY = 0; fromY < 8; fromY++) {
				if (figures[fromX][fromY] != null && figures[fromX][fromY].getOwner() == currentPlayer) {
					final int x = fromX;
					final int y = fromY;
					final double step = 1.0 / threadCount;
					final Thread thread = new Thread(() -> {
						logger.debug(figures[x][y].getCode() + " started");
						Board temp = null;
						final AtomicInteger countInternal = new AtomicInteger();
						for (int toX = 0; toX < 8; toX++) {
							for (int toY = 0; toY < 8; toY++) {
								temp = figures[x][y].move(toX, toY);
								if (temp != null) {
									countInternal.incrementAndGet();
								}
								if ((temp != null) && (ply > 1)) {
									// TODO DEBUG
									final Board a = temp;
									final Board b = a.getMaxSingleThreaded(ply - 1, countInternal);
									temp = b;
								}
								if ((max.get() == null) || ((temp != null)
										&& (temp.getScore(currentPlayer) > max.get().getScore(currentPlayer)))) {
									max.set(temp);
								}
							}
						}
						count.addAndGet(countInternal.get());
						String countInternalString = new DecimalFormat().format(countInternal.get()).toString();
						latch.countDown();
						Platform.runLater(() -> progress.set(progress.get() + step));
						logger.debug(figures[x][y].getCode() + " finished, calculated " + countInternalString
								+ " possible moves");
					});
					thread.setDaemon(true);
					thread.start();
				}
			}
		}
		try {
			latch.await();
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return max.get();
	}

	public Board getMin(int ply, AtomicInteger count) {
		if (isGameOVer()) {
			return chain(ply);
		}
		Board min = null;
		Board temp;
		for (int fromX = 0; fromX < 8; fromX++) {
			for (int fromY = 0; fromY < 8; fromY++) {
				if (figures[fromX][fromY] != null && figures[fromX][fromY].getOwner() == currentPlayer) {
					for (int toX = 0; toX < 8; toX++) {
						for (int toY = 0; toY < 8; toY++) {
							temp = figures[fromX][fromY].move(toX, toY);
							if (temp != null) {
								count.incrementAndGet();
							}
							if ((temp != null) && (ply > 1)) {
								temp = temp.getMaxSingleThreaded(ply - 1, count);
							}
							if ((min == null) || ((temp != null) && (temp.getScore() < min.getScore()))) {
								min = temp;
							}
						}
					}
				}
			}
		}
		return min;
	}

	/**
	 * Returns from position.
	 *
	 * @return from from position
	 */
	public Position getFrom() {
		return from;
	}

	/**
	 * Sets from position.
	 *
	 * @param from from position
	 */
	public void setFrom(Position from) {
		this.from = from;
	}

	/**
	 * Returns to position.
	 *
	 * @return to to position
	 */
	public Position getTo() {
		return to;
	}

	/**
	 * Sets to position.
	 *
	 * @param to to position
	 */
	public void setTo(Position to) {
		this.to = to;
	}

	/**
	 * Tells whether the game is over.
	 *
	 * @return {@code true}, if the game is over, which is the case if:
	 *         <ul>
	 *         <li>black is checkmated</li>
	 *         <li>white is checkmated</li>
	 *         <li>black is stalemated</li>
	 *         <li>white is stalemated</li>
	 *         </ul>
	 */
	private boolean isGameOVer() {
		return (state == State.CHECKMATE_BLACK) || (state == State.CHECKMATE_WHITE) || (state == State.STALEMATE_BLACK)
				|| (state == State.STALEMATE_WHITE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentPlayer == null) ? 0 : currentPlayer.hashCode());
		result = prime * result + Arrays.deepHashCode(figures);
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + markerBlackX;
		result = prime * result + markerWhiteX;
		result = prime * result + ((previous == null) ? 0 : previous.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + scoreBlackTotal;
		result = prime * result + scoreWhiteTotal;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Board other = (Board) obj;
		if (currentPlayer != other.currentPlayer) {
			return false;
		}
		if (!Arrays.deepEquals(figures, other.figures)) {
			return false;
		}
		if (from == null) {
			if (other.from != null) {
				return false;
			}
		} else if (!from.equals(other.from)) {
			return false;
		}
		if (markerBlackX != other.markerBlackX) {
			return false;
		}
		if (markerWhiteX != other.markerWhiteX) {
			return false;
		}
		if (previous == null) {
			if (other.previous != null) {
				return false;
			}
		} else if (!previous.equals(other.previous)) {
			return false;
		}
		if (state != other.state) {
			return false;
		}
		if (to == null) {
			if (other.to != null) {
				return false;
			}
		} else if (!to.equals(other.to)) {
			return false;
		}
		if (scoreBlackTotal != other.scoreBlackTotal) {
			return false;
		}
		if (scoreWhiteTotal != other.scoreWhiteTotal) {
			return false;
		}
		return true;
	}

}
