package com.saucecode.chessy.gui;

import java.util.HashMap;

import com.saucecode.chessy.core.FigureType;

import javafx.scene.image.Image;

/**
 * Helper class for initializing piece images. Maps every {@link FigureType} to an {@link Image}.
 * 
 * @author Torben Kr&uuml;ger
 */
public class ImageMap extends HashMap<FigureType, Image> {

	/**
	 * The Size of each image.
	 */
	public static final int IMG_SIZE = 40;

	/**
	 * Generated Serial Version UID.
	 */
	private static final long serialVersionUID = -1298667811144340329L;

	/**
	 * Creates a new {@link ImageMap} and puts all images.
	 */
	public ImageMap() {
		// @formatter:off
		final Image pawnB   = new Image(getClass().getResource(  "/img/pawnb.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image pawnW   = new Image(getClass().getResource(  "/img/pawnw.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image rookB   = new Image(getClass().getResource(  "/img/rookb.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image rookW   = new Image(getClass().getResource(  "/img/rookw.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image knightB = new Image(getClass().getResource("/img/knightb.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image knightW = new Image(getClass().getResource("/img/knightw.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image bishopB = new Image(getClass().getResource("/img/bishopb.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image bishopW = new Image(getClass().getResource("/img/bishopw.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image kingB   = new Image(getClass().getResource(  "/img/kingb.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image kingW   = new Image(getClass().getResource(  "/img/kingw.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image queenB  = new Image(getClass().getResource( "/img/queenb.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		final Image queenW  = new Image(getClass().getResource( "/img/queenw.png").toExternalForm(), IMG_SIZE, IMG_SIZE, false, false);
		put(FigureType.PAWN_BLACK,     pawnB);
		put(FigureType.PAWN_WHITE,     pawnW);
		put(FigureType.ROOK_BLACK,     rookB);
		put(FigureType.ROOK_WHITE,     rookW);
		put(FigureType.KNIGHT_BLACK, knightB);
		put(FigureType.KNIGHT_WHITE, knightW);
		put(FigureType.BISHOP_BLACK, bishopB);
		put(FigureType.BISHOP_WHITE, bishopW);
		put(FigureType.KING_BLACK,     kingB);
		put(FigureType.KING_WHITE,     kingW);
		put(FigureType.QUEEN_BLACK,   queenB);
		put(FigureType.QUEEN_WHITE,   queenW);
		// @formatter:on
	}

}
