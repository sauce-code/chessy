package com.saucecode.chessy.gui;

import com.saucecode.chessy.core.GameI;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * A subclass of {@link Alert}, which can be used to ask users for permission to exit.
 *
 * @see Alert
 *
 * @author Torben Kr&uuml;ger
 */
public class ExitDialog extends Alert {

	/**
	 * Creates a new {@link ExitDialog}.
	 *
	 * @param game instance of the game, which is about to be closed
	 * @param icon icon used for this dialog
	 */
	public ExitDialog(GameI game, Image icon) {
		super(AlertType.CONFIRMATION);
		setTitle("Exit");
		setHeaderText("Do you really want to exit?");
		setContentText("All progress will be lost.");

		// Get the Stage.
		final Stage stage = (Stage) getDialogPane().getScene().getWindow();

		// Add a custom icon.
		stage.getIcons().add(icon);
	}

}
