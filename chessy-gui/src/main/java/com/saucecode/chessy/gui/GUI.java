package com.saucecode.chessy.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.saucecode.chessy.core.FieldI;
import com.saucecode.chessy.core.FieldI.FigureType;
import com.saucecode.chessy.core.Game;
import com.saucecode.chessy.core.GameI;
import com.saucecode.chessy.core.Position;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
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
import javafx.stage.WindowEvent;

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
	 * The StackPanes - one for each rectangle of the chess game.
	 */
	private final StackPane panes[][] = new StackPane[8][8];

	private final Map<FieldI.FigureType, Image> imageMap = new HashMap<>();

	private Menu initMenuHelp() {
		final MenuItem about = new MenuItem("A_bout");
		about.setOnAction(e -> new AboutAlert(imageMap.get(FigureType.KING_WHITE)).showAndWait());

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
		undo.disableProperty().bind(game.undoEnabledPropoerty().not());
		undo.setAccelerator(KeyCombination.keyCombination("Ctrl + Z"));
		undo.setOnAction(e -> {
			if (game.blackAIProperty().get()) {
				game.undo();
			}
			game.undo();
		});

		return new Menu("_Edit", null, undo);
	}

	private Menu initMenuFile() {
		final MenuItem restart = new MenuItem("_Restart");
		restart.disableProperty().bind(game.resetEnabledProperty().not());
		restart.setAccelerator(KeyCombination.keyCombination("F2"));
		restart.setOnAction(e -> {
			Optional<ButtonType> result = new ResetDialog(game, imageMap.get(FigureType.KING_WHITE)).showAndWait();
			if (result.get() == ButtonType.OK) {
				game.reset();
			} else {
				// ... user chose CANCEL or closed the dialog
			}
		});

		final MenuItem exit = new MenuItem("E_xit");
//		exit.setAccelerator(KeyCombination.keyCombination("Alt + F4")); // TODO
		exit.setOnAction(e -> {
			if (game.resetEnabledProperty().get()) {
				// data could be lost. ask if user really wants to quit
				Optional<ButtonType> result = new ExitDialog(game, imageMap.get(FigureType.KING_WHITE)).showAndWait();
				if (result.get() == ButtonType.OK) {
					// user clicked OK
					Platform.exit();
				} else {
					// ... user chose CANCEL or closed the dialog
				}
			} else {
				// no data could be lost. exit without asking
				Platform.exit();
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

		final BorderPane border = new BorderPane(grid);
		border.setTop(initMenuBar());

		GridPane info = new GridPane();

		ConsoleLabel placeholder = new ConsoleLabel("          ");
		info.add(placeholder, 1, 0);

		int i = 0;

		ConsoleLabel scoreWhite = new ConsoleLabel("Score White: ");
		ConsoleLabel scoreWhite2 = new ConsoleLabel("");
		scoreWhite2.textProperty().bind(game.scoreWhiteProperty().asString("%,d"));
		scoreWhite2.setMaxWidth(Double.MAX_VALUE);
		scoreWhite2.setAlignment(Pos.CENTER_RIGHT);
		info.add(scoreWhite, 0, i);
		info.add(scoreWhite2, 1, i);
		i++;

		ConsoleLabel scoreBlack = new ConsoleLabel("Score Black: ");
		ConsoleLabel scoreBlack2 = new ConsoleLabel("");
		scoreBlack2.textProperty().bind(game.scoreBlackProperty().asString("%,d"));
		scoreBlack2.setMaxWidth(Double.MAX_VALUE);
		scoreBlack2.setAlignment(Pos.CENTER_RIGHT);
		info.add(scoreBlack, 0, i);
		info.add(scoreBlack2, 1, i);
		i++;

		ConsoleLabel scoreWhiteRaw = new ConsoleLabel("Score White Total: ");
		ConsoleLabel scoreWhiteRaw2 = new ConsoleLabel("");
		scoreWhiteRaw2.textProperty().bind(game.scoreWhiteTotalProperty().asString("%,d"));
		scoreWhiteRaw2.setMaxWidth(Double.MAX_VALUE);
		scoreWhiteRaw2.setAlignment(Pos.CENTER_RIGHT);
		info.add(scoreWhiteRaw, 0, i);
		info.add(scoreWhiteRaw2, 1, i);
		i++;

		ConsoleLabel scoreBlackRaw = new ConsoleLabel("Score Black Total: ");
		ConsoleLabel scoreBlackRaw2 = new ConsoleLabel("");
		scoreBlackRaw2.textProperty().bind(game.scoreBlackTotalProperty().asString("%,d"));
		scoreBlackRaw2.setMaxWidth(Double.MAX_VALUE);
		scoreBlackRaw2.setAlignment(Pos.CENTER_RIGHT);
		info.add(scoreBlackRaw, 0, i);
		info.add(scoreBlackRaw2, 1, i);
		i++;

		{
			ConsoleLabel blank = new ConsoleLabel("");
			info.add(blank, 0, i);
			i++;
		}

		ConsoleLabel ckeck = new ConsoleLabel("In Check: ");
		ConsoleLabel check2 = new ConsoleLabel("");
		check2.textProperty().bind(game.inCheckProperty().asString());
		check2.setMaxWidth(Double.MAX_VALUE);
		check2.setAlignment(Pos.CENTER_RIGHT);
		info.add(ckeck, 0, i);
		info.add(check2, 1, i);
		i++;

		ConsoleLabel stalemate = new ConsoleLabel("In Stalemate: ");
		ConsoleLabel stalemate2 = new ConsoleLabel("");
		stalemate2.textProperty().bind(game.inStalemateProperty().asString());
		stalemate2.setMaxWidth(Double.MAX_VALUE);
		stalemate2.setAlignment(Pos.CENTER_RIGHT);
		info.add(stalemate, 0, i);
		info.add(stalemate2, 1, i);
		i++;

		ConsoleLabel checkmate = new ConsoleLabel("In Checkmate: ");
		ConsoleLabel checkmate2 = new ConsoleLabel("");
		checkmate2.textProperty().bind(game.inCheckmateProperty().asString());
		checkmate2.setMaxWidth(Double.MAX_VALUE);
		checkmate2.setAlignment(Pos.CENTER_RIGHT);
		info.add(checkmate, 0, i);
		info.add(checkmate2, 1, i);
		i++;

		ConsoleLabel currentPlayer = new ConsoleLabel("Current Player: ");
		ConsoleLabel currentPlayer2 = new ConsoleLabel(game.currentPlayerProperty().get().toString());
		game.currentPlayerProperty().addListener(e -> Platform.runLater(() -> {
			currentPlayer2.setText(game.currentPlayerProperty().get().toString());
		}));
		currentPlayer2.setMaxWidth(Double.MAX_VALUE);
		currentPlayer2.setAlignment(Pos.CENTER_RIGHT);
		info.add(currentPlayer, 0, i);
		info.add(currentPlayer2, 1, i);
		i++;

		ConsoleLabel blank0 = new ConsoleLabel("");
		info.add(blank0, 0, i);
		i++;

		ConsoleLabel selected = new ConsoleLabel("Selected: ");
		ConsoleLabel selected2 = new ConsoleLabel("");
		selected2.textProperty().bind(game.selectionProperty().asString());
		selected2.setMaxWidth(Double.MAX_VALUE);
		selected2.setAlignment(Pos.CENTER_RIGHT);
		info.add(selected, 0, i);
		info.add(selected2, 1, i);
		i++;

		ConsoleLabel from = new ConsoleLabel("From: ");
		ConsoleLabel from2 = new ConsoleLabel("");
		from2.textProperty().bind(game.fromProperty().asString());
		from2.setMaxWidth(Double.MAX_VALUE);
		from2.setAlignment(Pos.CENTER_RIGHT);
		info.add(from, 0, i);
		info.add(from2, 1, i);
		i++;

		ConsoleLabel to = new ConsoleLabel("To: ");
		ConsoleLabel to2 = new ConsoleLabel("");
		to2.textProperty().bind(game.toProperty().asString());
		to2.setMaxWidth(Double.MAX_VALUE);
		to2.setAlignment(Pos.CENTER_RIGHT);
		info.add(to, 0, i);
		info.add(to2, 1, i);
		i++;

		ConsoleLabel blank1 = new ConsoleLabel("");

		info.add(blank1, 0, i);
		i++;

		ConsoleLabel busy = new ConsoleLabel("Busy: ");
		ConsoleLabel busy2 = new ConsoleLabel(Boolean.toString(false));
		busy2.textProperty().bind(game.busyProperty().asString());
		busy2.setMaxWidth(Double.MAX_VALUE);
		busy2.setAlignment(Pos.CENTER_RIGHT);
		info.add(busy, 0, i);
		info.add(busy2, 1, i);
		i++;

		ConsoleLabel progress = new ConsoleLabel("Progress: ");
		ConsoleLabel progress2 = new ConsoleLabel(Boolean.toString(false));
		progress2.textProperty().bind(game.progressProperty().multiply(100.0).asString("%.0f %%"));
		progress2.setMaxWidth(Double.MAX_VALUE);
		progress2.setAlignment(Pos.CENTER_RIGHT);
		info.add(progress, 0, i);
		info.add(progress2, 1, i);
		i++;

		ConsoleLabel calcs = new ConsoleLabel("Calculated Moves: ");
		ConsoleLabel calcs2 = new ConsoleLabel();
		calcs2.textProperty().bind(game.calculatedMovesProperty().asString("%,d"));
		calcs2.setMaxWidth(Double.MAX_VALUE);
		calcs2.setAlignment(Pos.CENTER_RIGHT);
		info.add(calcs, 0, i);
		info.add(calcs2, 1, i);
		i++;

		ConsoleLabel calcTime = new ConsoleLabel("Calculation Time: ");
		ConsoleLabel calcTime2 = new ConsoleLabel();
		calcTime2.textProperty().bind(game.calculationTimeProperty().asString("%,d ms"));
		calcTime2.setMaxWidth(Double.MAX_VALUE);
		calcTime2.setAlignment(Pos.CENTER_RIGHT);
		info.add(calcTime, 0, i);
		info.add(calcTime2, 1, i);
		i++;

		info.setPadding(new Insets(10.0));

		border.setRight(info);

		ProgressBar progressBar = new ProgressBar();
		progressBar.progressProperty().bind(game.progressProperty());
		progressBar.setMaxWidth(Double.MAX_VALUE);
		border.setBottom(progressBar);

		primaryStage.setScene(new Scene(border));
		primaryStage.getIcons().add(imageMap.get(FigureType.KING_WHITE));
		primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
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
		Image pawnB   = new Image(getClass().getResource(  "/pawnb.png").toExternalForm(), imgSize, imgSize, false, false);
		Image pawnW   = new Image(getClass().getResource(  "/pawnw.png").toExternalForm(), imgSize, imgSize, false, false);
		Image rookB   = new Image(getClass().getResource(  "/rookb.png").toExternalForm(), imgSize, imgSize, false, false);
		Image rookW   = new Image(getClass().getResource(  "/rookw.png").toExternalForm(), imgSize, imgSize, false, false);
		Image knightB = new Image(getClass().getResource("/knightb.png").toExternalForm(), imgSize, imgSize, false, false);
		Image knightW = new Image(getClass().getResource("/knightw.png").toExternalForm(), imgSize, imgSize, false, false);
		Image bishopB = new Image(getClass().getResource("/bishopb.png").toExternalForm(), imgSize, imgSize, false, false);
		Image bishopW = new Image(getClass().getResource("/bishopw.png").toExternalForm(), imgSize, imgSize, false, false);
		Image kingB   = new Image(getClass().getResource(  "/kingb.png").toExternalForm(), imgSize, imgSize, false, false);
		Image kingW   = new Image(getClass().getResource(  "/kingw.png").toExternalForm(), imgSize, imgSize, false, false);
		Image queenB  = new Image(getClass().getResource( "/queenb.png").toExternalForm(), imgSize, imgSize, false, false);
		Image queenW  = new Image(getClass().getResource( "/queenw.png").toExternalForm(), imgSize, imgSize, false, false);
		imageMap.put(FigureType.PAWN_BLACK,     pawnB);
		imageMap.put(FigureType.PAWN_WHITE,     pawnW);
		imageMap.put(FigureType.ROOK_BLACK,     rookB);
		imageMap.put(FigureType.ROOK_WHITE,     rookW);
		imageMap.put(FigureType.KNIGHT_BLACK, knightB);
		imageMap.put(FigureType.KNIGHT_WHITE, knightW);
		imageMap.put(FigureType.BISHOP_BLACK, bishopB);
		imageMap.put(FigureType.BISHOP_WHITE, bishopW);
		imageMap.put(FigureType.KING_BLACK,     kingB);
		imageMap.put(FigureType.KING_WHITE,     kingW);
		imageMap.put(FigureType.QUEEN_BLACK,   queenB);
		imageMap.put(FigureType.QUEEN_WHITE,   queenW);
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

				final Rectangle recti = new Rectangle(50, 50);
				Color color = ((i + j) % 2 == 0) ? Color.BLANCHEDALMOND : Color.GREEN;
				recti.setFill(color);

				// recti.addEventFilter(MouseEvent.MOUSE_PRESSED,
				// event -> System.out.println("i clicked it"));
				{
					final int x = i;
					final int y = 7 - j;
					recti.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							game.select(new Position(x, y)); // TODO platform run later?
//							System.out.println("clicked rectangle at x " + x + " y " + y);
						}
					});
				}

				{
					final int x = i;
					final int y = j;
					final StackPane pane = panes[x][y];
					game.fieldProperty(new Position(x, 7 - y)).addListener(new ChangeListener<FieldI>() {
						@Override
						public void changed(ObservableValue<? extends FieldI> observable, FieldI oldValue,
								FieldI newValue) {
							for (Node n : pane.getChildren()) {
								if (n instanceof ImageView) {
									((ImageView) n).setImage(null);
								}
							}
							if (newValue.getFigure() != FigureType.NONE) {
								panes[x][y].getChildren().add(getImageView(imageMap.get(newValue.getFigure())));
							}
							switch (newValue.getModifier()) {
							case FROM:
								recti.setFill(Color.AQUAMARINE);
								break;
							case NONE:
								Color color = ((x + y) % 2 == 0) ? Color.BLANCHEDALMOND : Color.GREEN;
								recti.setFill(color);
								break;
							case SELECTED:
								recti.setFill(Color.CYAN);
								break;
							case TO:
								recti.setFill(Color.AQUAMARINE);
								break;
							default:
								throw new InternalError("no such enum");
							}
						}
					});
				}
				panes[i][j].getChildren().add(recti);
				panes[i][j].getChildren()
						.add(getImageView(imageMap.get(game.fieldProperty(new Position(i, 7 - j)).get().getFigure())));

			}
		}
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

	private void closeWindowEvent(WindowEvent event) {
		if (game.resetEnabledProperty().get()) {
			// data could be lost. ask if user really wants to quit
			Optional<ButtonType> result = new ExitDialog(game, imageMap.get(FigureType.KING_WHITE)).showAndWait();
			if (result.get() == ButtonType.OK) {
				// user clicked OK
				// continue
			} else {
				// ... user chose CANCEL or closed the dialog
				// stop exit
				event.consume();
			}
		} else {
			// no data could be lost
			// continue without asking
		}
	}

}
