package com.saucecode.chessy.gui;

import java.util.Optional;

import com.saucecode.chessy.core.FieldI;
import com.saucecode.chessy.core.FigureType;
import com.saucecode.chessy.core.Game;
import com.saucecode.chessy.core.GameI;
import com.saucecode.chessy.core.Position;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
	private final Game game = new Game();

	/**
	 * The GridPane that contains all elements.
	 */
	private final GridPane grid = new GridPane();

	/**
	 * The StackPanes - one for each rectangle of the chess game.
	 */
	private final StackPane panes[][] = new StackPane[8][8];

	/**
	 * Stores all images.
	 */
	private final ImageMap imageMap = new ImageMap();

	/**
	 * Initializes the help menu.
	 *
	 * @return the help menu
	 */
	private Menu initMenuHelp() {
		final MenuItem about = new MenuItem("A_bout");
		about.setOnAction(e -> new AboutAlert(imageMap.get(FigureType.KING_WHITE)).showAndWait());

		return new Menu("_Help", null, about);
	}

	/**
	 * Initializes the settings menu.
	 *
	 * @return the settings menu
	 */
	private Menu initMenuSettings() {
		final CheckMenuItem ai = new CheckMenuItem("_Black Player A.I.");
		ai.selectedProperty().set(game.aiBlackActiveProperty().get());
		game.aiBlackActiveProperty().bind(ai.selectedProperty());

		final Menu menuPly = new Menu("_Ply");
		final ToggleGroup groupPly = new ToggleGroup();
		final RadioMenuItem[] items = new RadioMenuItem[GameI.PLY_MAX];
		for (int i = 0; i < GameI.PLY_MAX; i++) {
			items[i] = new RadioMenuItem(Integer.toString(i + 1));
			items[i].disableProperty().bind(game.busyProperty());
			items[i].setToggleGroup(groupPly);
			menuPly.getItems().add(items[i]);
		}
		groupPly.selectedToggleProperty().addListener((ChangeListener<Toggle>) (ov, oldToggle, newToggle) -> {
			for (int i = 0; i < GameI.PLY_MAX; i++) {
				if (newToggle == items[i]) {
					game.aiBlackPlyProperty().set(i + 1);
				}
			}
		});
		items[game.aiBlackPlyProperty().get() - 1].setSelected(true);

		final CheckMenuItem multiThreaded = new CheckMenuItem("_Multi-Threading");
		multiThreaded.selectedProperty().set(game.multiThreadedProperty().get());
		game.multiThreadedProperty().bind(multiThreaded.selectedProperty());

		return new Menu("_Settings", null, ai, multiThreaded, menuPly);
	}

	/**
	 * Initializes the edit menu.
	 *
	 * @return the edit menu
	 */
	private Menu initMenuEdit() {
		final MenuItem undo = new MenuItem("_Undo");
		undo.disableProperty().bind(game.undoEnabledProperty().not());
		undo.setAccelerator(KeyCombination.keyCombination("Ctrl + Z"));
		undo.setOnAction(e -> {
			if (game.aiBlackActiveProperty().get()) {
				game.undo();
			}
			game.undo();
		});

		return new Menu("_Edit", null, undo);
	}

	/**
	 * Initializes the file menu.
	 *
	 * @return the file menu
	 */
	private Menu initMenuFile() {
		final MenuItem restart = new MenuItem("_Restart");
		restart.disableProperty().bind(game.resetEnabledProperty().not());
		restart.setAccelerator(KeyCombination.keyCombination("F2"));
		restart.setOnAction(e -> {
			final Optional<ButtonType> result = new ResetDialog(game, imageMap.get(FigureType.KING_WHITE))
					.showAndWait();
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
				final Optional<ButtonType> result = new ExitDialog(game, imageMap.get(FigureType.KING_WHITE))
						.showAndWait();
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
	 * Initializes the console.
	 *
	 * @return the console
	 */
	private GridPane initConsole() {
		final GridPane console = new GridPane();

		final ConsoleLabel placeholder = new ConsoleLabel("          ");
		console.add(placeholder, 1, 0);

		int i = 0;

		final ConsoleLabel scoreWhite = new ConsoleLabel("Score White: ");
		final ConsoleLabel scoreWhite2 = new ConsoleLabel("");
		scoreWhite2.textProperty().bind(game.scoreWhiteProperty().asString("%,d"));
		scoreWhite2.setMaxWidth(Double.MAX_VALUE);
		scoreWhite2.setAlignment(Pos.CENTER_RIGHT);
		console.add(scoreWhite, 0, i);
		console.add(scoreWhite2, 1, i);
		i++;

		final ConsoleLabel scoreBlack = new ConsoleLabel("Score Black: ");
		final ConsoleLabel scoreBlack2 = new ConsoleLabel("");
		scoreBlack2.textProperty().bind(game.scoreBlackProperty().asString("%,d"));
		scoreBlack2.setMaxWidth(Double.MAX_VALUE);
		scoreBlack2.setAlignment(Pos.CENTER_RIGHT);
		console.add(scoreBlack, 0, i);
		console.add(scoreBlack2, 1, i);
		i++;

		final ConsoleLabel scoreWhiteRaw = new ConsoleLabel("Score White Total: ");
		final ConsoleLabel scoreWhiteRaw2 = new ConsoleLabel("");
		scoreWhiteRaw2.textProperty().bind(game.scoreWhiteTotalProperty().asString("%,d"));
		scoreWhiteRaw2.setMaxWidth(Double.MAX_VALUE);
		scoreWhiteRaw2.setAlignment(Pos.CENTER_RIGHT);
		console.add(scoreWhiteRaw, 0, i);
		console.add(scoreWhiteRaw2, 1, i);
		i++;

		final ConsoleLabel scoreBlackRaw = new ConsoleLabel("Score Black Total: ");
		final ConsoleLabel scoreBlackRaw2 = new ConsoleLabel("");
		scoreBlackRaw2.textProperty().bind(game.scoreBlackTotalProperty().asString("%,d"));
		scoreBlackRaw2.setMaxWidth(Double.MAX_VALUE);
		scoreBlackRaw2.setAlignment(Pos.CENTER_RIGHT);
		console.add(scoreBlackRaw, 0, i);
		console.add(scoreBlackRaw2, 1, i);
		i++;

		{
			final ConsoleLabel blank = new ConsoleLabel("");
			console.add(blank, 0, i);
			i++;
		}

		final ConsoleLabel ckeck = new ConsoleLabel("In Check: ");
		final ConsoleLabel check2 = new ConsoleLabel("");
		check2.textProperty().bind(game.inCheckProperty().asString());
		check2.setMaxWidth(Double.MAX_VALUE);
		check2.setAlignment(Pos.CENTER_RIGHT);
		console.add(ckeck, 0, i);
		console.add(check2, 1, i);
		i++;

		final ConsoleLabel stalemate = new ConsoleLabel("In Stalemate: ");
		final ConsoleLabel stalemate2 = new ConsoleLabel("");
		stalemate2.textProperty().bind(game.inStalemateProperty().asString());
		stalemate2.setMaxWidth(Double.MAX_VALUE);
		stalemate2.setAlignment(Pos.CENTER_RIGHT);
		console.add(stalemate, 0, i);
		console.add(stalemate2, 1, i);
		i++;

		final ConsoleLabel checkmate = new ConsoleLabel("In Checkmate: ");
		final ConsoleLabel checkmate2 = new ConsoleLabel("");
		checkmate2.textProperty().bind(game.inCheckmateProperty().asString());
		checkmate2.setMaxWidth(Double.MAX_VALUE);
		checkmate2.setAlignment(Pos.CENTER_RIGHT);
		console.add(checkmate, 0, i);
		console.add(checkmate2, 1, i);
		i++;

		final ConsoleLabel currentPlayer = new ConsoleLabel("Current Player: ");
		final ConsoleLabel currentPlayer2 = new ConsoleLabel(game.currentPlayerProperty().get().toString());
		game.currentPlayerProperty().addListener(e -> Platform.runLater(() -> {
			currentPlayer2.setText(game.currentPlayerProperty().get().toString());
		}));
		currentPlayer2.setMaxWidth(Double.MAX_VALUE);
		currentPlayer2.setAlignment(Pos.CENTER_RIGHT);
		console.add(currentPlayer, 0, i);
		console.add(currentPlayer2, 1, i);
		i++;

		final ConsoleLabel blank0 = new ConsoleLabel("");
		console.add(blank0, 0, i);
		i++;

		final ConsoleLabel selected = new ConsoleLabel("Selected: ");
		final ConsoleLabel selected2 = new ConsoleLabel("");
		selected2.textProperty().bind(game.selectionProperty().asString());
		selected2.setMaxWidth(Double.MAX_VALUE);
		selected2.setAlignment(Pos.CENTER_RIGHT);
		console.add(selected, 0, i);
		console.add(selected2, 1, i);
		i++;

		final ConsoleLabel from = new ConsoleLabel("From: ");
		final ConsoleLabel from2 = new ConsoleLabel("");
		from2.textProperty().bind(game.fromProperty().asString());
		from2.setMaxWidth(Double.MAX_VALUE);
		from2.setAlignment(Pos.CENTER_RIGHT);
		console.add(from, 0, i);
		console.add(from2, 1, i);
		i++;

		final ConsoleLabel to = new ConsoleLabel("To: ");
		final ConsoleLabel to2 = new ConsoleLabel("");
		to2.textProperty().bind(game.toProperty().asString());
		to2.setMaxWidth(Double.MAX_VALUE);
		to2.setAlignment(Pos.CENTER_RIGHT);
		console.add(to, 0, i);
		console.add(to2, 1, i);
		i++;

		final ConsoleLabel blank1 = new ConsoleLabel("");

		console.add(blank1, 0, i);
		i++;

		final ConsoleLabel busy = new ConsoleLabel("Busy: ");
		final ConsoleLabel busy2 = new ConsoleLabel(Boolean.toString(false));
		busy2.textProperty().bind(game.busyProperty().asString());
		busy2.setMaxWidth(Double.MAX_VALUE);
		busy2.setAlignment(Pos.CENTER_RIGHT);
		console.add(busy, 0, i);
		console.add(busy2, 1, i);
		i++;

		final ConsoleLabel progress = new ConsoleLabel("Progress: ");
		final ConsoleLabel progress2 = new ConsoleLabel(Boolean.toString(false));
		progress2.textProperty().bind(game.progressProperty().multiply(100.0).asString("%.0f %%"));
		progress2.setMaxWidth(Double.MAX_VALUE);
		progress2.setAlignment(Pos.CENTER_RIGHT);
		console.add(progress, 0, i);
		console.add(progress2, 1, i);
		i++;

		final ConsoleLabel calcs = new ConsoleLabel("Calculated Moves: ");
		final ConsoleLabel calcs2 = new ConsoleLabel();
		calcs2.textProperty().bind(game.calculatedMovesProperty().asString("%,d"));
		calcs2.setMaxWidth(Double.MAX_VALUE);
		calcs2.setAlignment(Pos.CENTER_RIGHT);
		console.add(calcs, 0, i);
		console.add(calcs2, 1, i);
		i++;

		final ConsoleLabel calcTime = new ConsoleLabel("Calculation Time: ");
		final ConsoleLabel calcTime2 = new ConsoleLabel();
		calcTime2.textProperty().bind(game.calculationTimeProperty().asString("%,d ms"));
		calcTime2.setMaxWidth(Double.MAX_VALUE);
		calcTime2.setAlignment(Pos.CENTER_RIGHT);
		console.add(calcTime, 0, i);
		console.add(calcTime2, 1, i);
		i++;

		console.setPadding(new Insets(10.0));

		return console;
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

		initializeStackPanes();

		final BorderPane border = new BorderPane(grid);
		border.setTop(initMenuBar());

		border.setRight(initConsole());

		final ProgressBar progressBar = new ProgressBar();
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
	 * Initializes the StackPanes for every rectangle of the chess board and draws the rectangles also sets the
	 * eventListener for clicks for every rectangle.
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
				final Color color = ((i + j) % 2 == 0) ? Color.BLANCHEDALMOND : Color.GREEN;
				recti.setFill(color);

				// recti.addEventFilter(MouseEvent.MOUSE_PRESSED,
				// event -> System.out.println("i clicked it"));
				{
					final int x = i;
					final int y = 7 - j;
					recti.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> game.select(new Position(x, y)));
				}

				{
					final int x = i;
					final int y = j;
					final StackPane pane = panes[x][y];
					game.fieldProperty(new Position(x, 7 - y))
							.addListener((ChangeListener<FieldI>) (observable, oldValue, newValue) -> {
								for (final Node n : pane.getChildren()) {
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
									final Color color1 = ((x + y) % 2 == 0) ? Color.BLANCHEDALMOND : Color.GREEN;
									recti.setFill(color1);
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
							});
				}
				panes[i][j].getChildren().add(recti);
				panes[i][j].getChildren()
						.add(getImageView(imageMap.get(game.fieldProperty(new Position(i, 7 - j)).get().getFigure())));

			}
		}
	}

	/**
	 * This returns an ImageView containing the transfered image and sets the setMouseTransparent-Property of the
	 * ImageView to true (so you can click it).
	 *
	 * @param image the image to be set
	 * @return the generated ImageView
	 */
	private ImageView getImageView(Image image) {
		final ImageView view = new ImageView(image);
		view.setMouseTransparent(true);
		return view;
	}

	/**
	 * Checks if the application can be closed or if the user should be asked for permission, because data could be
	 * lost. This should be called, whenever the application is about to be closed.
	 *
	 * @param event window close request
	 */
	private void closeWindowEvent(WindowEvent event) {
		if (game.resetEnabledProperty().get()) {
			// data could be lost. ask if user really wants to quit
			final Optional<ButtonType> result = new ExitDialog(game, imageMap.get(FigureType.KING_WHITE)).showAndWait();
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
