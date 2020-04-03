package com.saucecode.chessy.gui;

import java.util.Optional;

import com.saucecode.chessy.core.Board;
import com.saucecode.chessy.core.Game;
import com.saucecode.chessy.core.GameI;
import com.saucecode.chessy.core.Selection;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * This class provides the Gui for the chess game.
 * 
 * @author Torben Kr&uuml;ger
 */
public class GUI extends Application {

	/**
	 * The game.
	 */
	private Game game = new Game();

	/**
	 * The GridPane that contains all elements.
	 */
	private GridPane grid = new GridPane();

	/**
	 * The Size of each image.
	 */
	private final int imgSize = 40;

	/**
	 * All the Images.
	 */
	private Image pawnB, pawnW, rookB, rookW, knightB, knightW, bishopB, bishopW, kingB, kingW, queenB, queenW;

	/**
	 * The StackPanes - one for each rectangle of the chess game.
	 */
	private final StackPane panes[][] = new StackPane[GameI.DIM][GameI.DIM];

	private Menu initMenuHelp() {
		final MenuItem about = new MenuItem("A_bout");
		about.setOnAction(e -> new AboutAlert(kingW).showAndWait());

		return new Menu("_Help", null, about);
	}

	private Menu initMenuSettings() {
		final CheckMenuItem ai = new CheckMenuItem("_Black Player A.I.");
		ai.selectedProperty().set(game.blackAIProperty().get());
		game.blackAIProperty().bind(ai.selectedProperty());
		
		final Menu menuPly = new Menu("Ply");
		final ToggleGroup groupPly = new ToggleGroup();
		RadioMenuItem[] items = new RadioMenuItem[GameI.PLY_MAX];
		for (int i = 0; i < GameI.PLY_MAX; i++) {
			items[i] = new RadioMenuItem(Integer.toString(i + 1));
			items[i].setToggleGroup(groupPly);
			menuPly.getItems().add(items[i]);
		}
		groupPly.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
				for (int i = 0; i < GameI.PLY_MAX; i++) {
					if (newToggle == items[i]) {
						game.plyProperty().set(i + 1);
					}
				}
			}
		});
		items[game.plyProperty().get() - 1].setSelected(true);
		
		final CheckMenuItem multiThreaded = new CheckMenuItem("_Multi-Threading");
		multiThreaded.selectedProperty().set(game.multiThreadedProperty().get());
		game.multiThreadedProperty().bind(multiThreaded.selectedProperty());

		return new Menu("_Settings", null, ai, multiThreaded, menuPly);
	}

	private Menu initMenuEdit() {
		final MenuItem undo = new MenuItem("_Undo");
		undo.disableProperty().bind(game.undoable().not());
		undo.setAccelerator(KeyCombination.keyCombination("Ctrl + Z"));
		undo.setOnAction(e -> {
			if (game.blackAIProperty().get()) {
				game.undo();
			}
			game.undo();
			drawBoard();
		});

		return new Menu("_Edit", null, undo);
	}

	private Menu initMenuFile() {
		final MenuItem restart = new MenuItem("_Restart");
		restart.disableProperty().bind(game.resettable().not());
		restart.setAccelerator(KeyCombination.keyCombination("F2"));
		restart.setOnAction(e -> {
			Optional<ButtonType> result = new ResetDialog(game, kingW).showAndWait();
			if (result.get() == ButtonType.OK) {
			    game.reset();
			} else {
			    // ... user chose CANCEL or closed the dialog
			}
		});

		final MenuItem exit = new MenuItem("E_xit");
		exit.setAccelerator(KeyCombination.keyCombination("Alt + F4"));
		exit.setOnAction(e -> {
			Optional<ButtonType> result = new ExitDialog(game, kingW).showAndWait();
			if (result.get() == ButtonType.OK) {
				Platform.exit();
			} else {
			    // ... user chose CANCEL or closed the dialog
			}
		});

		return new Menu("_File", null, restart, new SeparatorMenuItem(), exit);
	}

	/**
	 * Initializes the menu bar and returns it.
	 *
	 * @return the initialized menubar
	 */
	private MenuBar initMenuBar() {
		final MenuBar menuBar = new MenuBar(initMenuFile(), initMenuEdit(), initMenuSettings(), initMenuHelp());
		menuBar.setUseSystemMenuBar(true);
		return menuBar;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args arguments, unused
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Chessy"); // TODO constant

		initializeGrid();

		initializeImages();
		initializeStackPanes();
		drawBoard();

		final BorderPane border = new BorderPane(grid);
		border.setTop(initMenuBar());

		GridPane info = new GridPane();
		
		int i = 0;
		
		Label scoreWhite = new Label("Score White: ");
		Label scoreWhite2 = new Label("");
		scoreWhite2.textProperty().bind(game.boardValueWhiteProperty().asString());
		scoreWhite2.setMaxWidth(Double.MAX_VALUE);
		scoreWhite2.setAlignment(Pos.CENTER_RIGHT);
		info.add(scoreWhite, 0, i);
		info.add(scoreWhite2, 1, i);
		i++;
		
		Label scoreBlack = new Label("Score Black: ");
		Label scoreBlack2 = new Label("");
		scoreBlack2.textProperty().bind(game.boardValueBlackProperty().asString());
		scoreBlack2.setMaxWidth(Double.MAX_VALUE);
		scoreBlack2.setAlignment(Pos.CENTER_RIGHT);
		info.add(scoreBlack, 0, i);
		info.add(scoreBlack2, 1, i);
		i++;
		
		Label ckeck = new Label("In Check: ");
		Label check2 = new Label("");
		check2.textProperty().bind(game.inCheckProperty().asString());
		check2.setMaxWidth(Double.MAX_VALUE);
		check2.setAlignment(Pos.CENTER_RIGHT);
		info.add(ckeck, 0, i);
		info.add(check2, 1, i);
		i++;
		
		Label stalemate = new Label("In Stalemate: ");
		Label stalemate2 = new Label("");
		stalemate2.textProperty().bind(game.inStalemateProperty().asString());
		stalemate2.setMaxWidth(Double.MAX_VALUE);
		stalemate2.setAlignment(Pos.CENTER_RIGHT);
		info.add(stalemate, 0, i);
		info.add(stalemate2, 1, i);
		i++;
		
		Label checkmate = new Label("In Checkmate: ");
		Label checkmate2 = new Label("");
		checkmate2.textProperty().bind(game.inCheckmateProperty().asString());
		checkmate2.setMaxWidth(Double.MAX_VALUE);
		checkmate2.setAlignment(Pos.CENTER_RIGHT);
		info.add(checkmate, 0, i);
		info.add(checkmate2, 1, i);
		i++;
		
		Label currentPlayer = new Label("Current Player: ");
		Label currentPlayer2 = new Label(game.currentPlayerProperty().get().toString());
		game.currentPlayerProperty().addListener(e -> Platform.runLater(() -> {
			currentPlayer2.setText(game.currentPlayerProperty().get().toString());
		}));
		currentPlayer2.setMaxWidth(Double.MAX_VALUE);
		currentPlayer2.setAlignment(Pos.CENTER_RIGHT);
		info.add(currentPlayer, 0, i);
		info.add(currentPlayer2, 1, i);
		i++;
		
		Label blank0 = new Label("");
		info.add(blank0, 0, i);
		i++;
		
		Label selected = new Label("Selected: ");
		Label selected2 = new Label("");
		selected2.textProperty().bind(game.selectionProperty().asString());
		selected2.setMaxWidth(Double.MAX_VALUE);
		selected2.setAlignment(Pos.CENTER_RIGHT);
		info.add(selected, 0, i);
		info.add(selected2, 1, i);
		i++;
		
		Label from = new Label("From: ");
		Label from2 = new Label("");
		from2.textProperty().bind(game.fromProperty().asString());
		from2.setMaxWidth(Double.MAX_VALUE);
		from2.setAlignment(Pos.CENTER_RIGHT);
		info.add(from, 0, i);
		info.add(from2, 1, i);
		i++;
		
		Label to = new Label("To: ");
		Label to2 = new Label("");
		to2.textProperty().bind(game.toProperty().asString());
		to2.setMaxWidth(Double.MAX_VALUE);
		to2.setAlignment(Pos.CENTER_RIGHT);
		info.add(to, 0, i);
		info.add(to2, 1, i);
		i++;
		
		Label blank1 = new Label("");
		info.add(blank1, 0, i);
		i++;
		
		Label busy = new Label("Busy: ");
		Label busy2 = new Label(Boolean.toString(false));
		busy2.textProperty().bind(game.busyProperty().asString());
		busy2.setMaxWidth(Double.MAX_VALUE);
		busy2.setAlignment(Pos.CENTER_RIGHT);
		info.add(busy, 0, i);
		info.add(busy2, 1, i);
		i++;
		
		Label progress = new Label("Progress: ");
		Label progress2 = new Label(Boolean.toString(false));
		progress2.textProperty().bind(game.progressProperty().asString("%.2f"));
		progress2.setMaxWidth(Double.MAX_VALUE);
		progress2.setAlignment(Pos.CENTER_RIGHT);
		info.add(progress, 0, i);
		info.add(progress2, 1, i);
		i++;
		
		info.setMaxWidth(120.0);
		info.setMinWidth(120.0);

		border.setRight(info);
		
		ProgressBar progressBar = new ProgressBar();
		progressBar.progressProperty().bind(game.progressProperty());
		progressBar.setMaxWidth(Double.MAX_VALUE);
		border.setBottom(progressBar);

		game.boardProperty().addListener(new ChangeListener<Board>() {
			@Override
			public void changed(ObservableValue<? extends Board> observable, Board oldValue, Board newValue) {
				Platform.runLater(() -> drawBoard());
			}
		});

		game.selectionProperty().addListener(new ChangeListener<Selection>() {
			@Override
			public void changed(ObservableValue<? extends Selection> observable, Selection oldValue,
					Selection newValue) {
				Platform.runLater(() -> drawBoard());
			}
		});
		
		game.fromProperty().addListener(new ChangeListener<Selection>() {
			@Override
			public void changed(ObservableValue<? extends Selection> observable, Selection oldValue,
					Selection newValue) {
				Platform.runLater(() -> drawBoard());
			}
		});
		
		game.toProperty().addListener(new ChangeListener<Selection>() {
			@Override
			public void changed(ObservableValue<? extends Selection> observable, Selection oldValue,
					Selection newValue) {
				Platform.runLater(() -> drawBoard());
			}
		});

		primaryStage.setScene(new Scene(border));
		primaryStage.getIcons().add(kingW);
		primaryStage.show();
	}

	/**
	 * Sets the properties of the grid.
	 */
	private void initializeGrid() {
		grid.setHgap(1);
		grid.setVgap(1); // space between rows
//		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setGridLinesVisible(true);
		grid.setAlignment(Pos.CENTER);
	}

	/**
	 * Loads the Images.
	 */
	private void initializeImages() {
		// @formatter:off
		pawnB = new Image(getClass().getResource("/pawnb.png").toExternalForm(), imgSize, imgSize, false, false);
		pawnW = new Image(getClass().getResource("/pawnw.png").toExternalForm(), imgSize, imgSize, false, false);
		rookB = new Image(getClass().getResource("/rookb.png").toExternalForm(), imgSize, imgSize, false, false);
		rookW = new Image(getClass().getResource("/rookw.png").toExternalForm(), imgSize, imgSize, false, false);
		knightB = new Image(getClass().getResource("/knightb.png").toExternalForm(), imgSize, imgSize, false, false);
		knightW = new Image(getClass().getResource("/knightw.png").toExternalForm(), imgSize, imgSize, false, false);
		bishopB = new Image(getClass().getResource("/bishopb.png").toExternalForm(), imgSize, imgSize, false,
				false);
		bishopW = new Image(getClass().getResource("/bishopw.png").toExternalForm(), imgSize, imgSize, false,
				false);
		kingB = new Image(getClass().getResource("/kingb.png").toExternalForm(), imgSize, imgSize, false, false);
		kingW = new Image(getClass().getResource("/kingw.png").toExternalForm(), imgSize, imgSize, false, false);
		queenB = new Image(getClass().getResource("/queenb.png").toExternalForm(), imgSize, imgSize, false, false);
		queenW = new Image(getClass().getResource("/queenw.png").toExternalForm(), imgSize, imgSize, false, false);
		// @formatter:on
	}

	/**
	 * Initializes the StackPanes for every rectangle of the chess board and draws
	 * the rectangles also sets the eventListener for clicks for every rectangle.
	 */
	private void initializeStackPanes() {

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				panes[i][j] = new StackPane();
				panes[i][j].setAlignment(Pos.CENTER);
				// label.setMouseTransparent(true);
				// grid.add(panes[i][j], i, (7 + startBoardY) - j);
				grid.add(panes[i][j], i, j + 1);

				Rectangle recti = new Rectangle(50, 50);
				Color color = ((i + j) % 2 == 0) ? Color.BLANCHEDALMOND : Color.GREEN;
				recti.setFill(color);

				// recti.addEventFilter(MouseEvent.MOUSE_PRESSED,
				// event -> System.out.println("i clicked it"));

				final int x = i;
				final int y = 7 - j;
				recti.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						game.select(new Selection(x, y)); // TODO platform run later?
						System.out.println("clicked rectangle at x " + x + " y " + y);
					}
				});

				panes[i][j].getChildren().add(recti);

			}
		}
	}

	/**
	 * Puts a pawn at the given position (<b>chess coordinates</b>).
	 * 
	 * @param black true - if the pawn is black, false - if the pawn is white
	 * @param x     coordinate on the chess board of the pawn
	 * @param y     coordinate on the chess board of the pawn
	 */
	private void putPawn(boolean black, int x, int y) {
		Image image = (black) ? pawnB : pawnW;
		panes[x][y].getChildren().add(getImageView(image));
	}

	/**
	 * Puts a rook at the given position (<b>chess coordinates</b>).
	 * 
	 * @param black true - if the rook is black, false - if the rook is white
	 * @param x     coordinate on the chess board of the rook
	 * @param y     coordinate on the chess board of the rook
	 */
	private void putRook(boolean black, int x, int y) {
		Image image = (black) ? rookB : rookW;
		panes[x][y].getChildren().add(getImageView(image));
	}

	/**
	 * Puts a knight at the given position (<b>chess coordinates</b>).
	 * 
	 * @param black true - if the knight is black, false - if the knight is white
	 * @param x     coordinate on the chess board of the knight
	 * @param y     coordinate on the chess board of the knight
	 */
	private void putKnight(boolean black, int x, int y) {
		Image image = (black) ? knightB : knightW;
		panes[x][y].getChildren().add(getImageView(image));
	}

	/**
	 * Puts a bishop at the given position (<b>chess coordinates</b>).
	 * 
	 * @param black true - if the bishop is black, false - if the bishop is white
	 * @param x     coordinate on the chess board of the bishop
	 * @param y     coordinate on the chess board of the bishop
	 */
	private void putBishop(boolean black, int x, int y) {
		Image image = (black) ? bishopB : bishopW;
		panes[x][y].getChildren().add(getImageView(image));
	}

	/**
	 * Puts a queen at the given position (<b>chess coordinates</b>).
	 * 
	 * @param black true - if the queen is black, false - if the queen is white
	 * @param x     coordinate on the chess board of the queen
	 * @param y     coordinate on the chess board of the queen
	 */
	private void putQueen(boolean black, int x, int y) {
		Image image = (black) ? queenB : queenW;
		panes[x][y].getChildren().add(getImageView(image));
	}

	/**
	 * Puts a king at the given position (<b>chess coordinates</b>).
	 * 
	 * @param black true - if the king is black, false - if the king is white
	 * @param x     coordinate on the chess board of the king
	 * @param y     coordinate on the chess board of the king
	 */
	private void putKing(boolean black, int x, int y) {
		Image image = (black) ? kingB : kingW;
		panes[x][y].getChildren().add(getImageView(image));
	}

	/**
	 * This returns an ImageView containing the transfered image and sets the
	 * setMouseTransparent-Property of the ImageView to true (so you can click it).
	 * 
	 * @param image the image to be set
	 * @return the generated ImageView
	 */
	private ImageView getImageView(Image image) {
		ImageView view = new ImageView(image);
		view.setMouseTransparent(true);
		return view;
	}

	/**
	 * Draws the board.
	 */
	private void drawBoard() {

		deleteAllPieces();
		setColors();
		Selection select = game.selectionProperty().get();
		if (select != null) {
			((Rectangle)panes[select.getX()][7 - select.getY()].getChildren().get(0)).setFill(Color.CYAN);
		}
		Selection from = game.fromProperty().get();
		if (from != null) {
			((Rectangle)panes[from.getX()][7 - from.getY()].getChildren().get(0)).setFill(Color.AQUAMARINE);
		}
		Selection to = game.toProperty().get();
		if (to != null) {
			((Rectangle)panes[to.getX()][7 - to.getY()].getChildren().get(0)).setFill(Color.AQUAMARINE);
		}

//		if (select != null) {
//			int depth = 200; //Setting the uniform variable for the glow width and height
//			 
//			DropShadow borderGlow= new DropShadow();
//			borderGlow.setOffsetY(0f);
//			borderGlow.setOffsetX(0f);
//			borderGlow.setColor(Color.CYAN);
//			borderGlow.setWidth(depth);
//			borderGlow.setHeight(depth);
//			 
//			panes[select.getX()][7 - select.getY()].setEffect(borderGlow);
//		}

		Board board = game.boardProperty().get();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				if (board.getFigure(x, y) != null) {
					switch (board.getFigure(x, y).toString()) {

					// change coords
					case "WB":
						putBishop(false, x, 7 - y);
						break;
					case "BB":
						putBishop(true, x, 7 - y);
						break;
					case "WK":
						putKing(false, x, 7 - y);
						break;
					case "BK":
						putKing(true, x, 7 - y);
						break;
					case "WN": // knight
						putKnight(false, x, 7 - y);
						break;
					case "BN": // knight
						putKnight(true, x, 7 - y);
						break;
					case "WP":
						putPawn(false, x, 7 - y);
						break;
					case "BP":
						putPawn(true, x, 7 - y);
						break;
					case "WQ":
						putQueen(false, x, 7 - y);
						break;
					case "BQ":
						putQueen(true, x, 7 - y);
						break;
					case "WR":
						putRook(false, x, 7 - y);
						break;
					case "BR":
						putRook(true, x, 7 - y);
						break;
					}
				}
			}
		}
	}

	private void setColors() {
		for (int x = 0; x < 8; x++ ) {
			for (int y = 0; y < 8; y ++) {
				Color color = ((x + y) % 2 == 0) ? Color.BLANCHEDALMOND : Color.GREEN;
				((Rectangle)panes[x][y].getChildren().get(0)).setFill(color);
			}
		}
	}

	/**
	 * Delete the ImageView of the given Piece.
	 * 
	 * @param x x-coordinate of the piece
	 * @param y y-coordinate of the piece
	 */
	private void deletePiece(int x, int y) {
		for (Node n : panes[x][y].getChildren()) {
			if (n instanceof ImageView) {
				((ImageView) n).setImage(null);
			}
		}
	}

	/**
	 * Deletes all pieces.
	 */
	private void deleteAllPieces() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				deletePiece(x, y);
			}
		}
	}

}
