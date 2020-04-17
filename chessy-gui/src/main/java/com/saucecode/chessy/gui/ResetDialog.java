package com.saucecode.chessy.gui;

import com.saucecode.chessy.core.GameI;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * A subclass of {@link Alert}, which can be used to ask users for permission to reset.
 *
 * @see Alert
 *
 * @author Torben Kr&uuml;ger
 */
public class ResetDialog extends Alert {

	/**
	 * Creates a new {@link ResetDialog}.
	 *
	 * @param game instance of the game, which is about to be reset
	 * @param icon icon used for this dialog
	 */
	public ResetDialog(GameI game, Image icon) {
		super(AlertType.CONFIRMATION);
		setTitle("Restart");
		setHeaderText("Do you really want to start a new game?");
		setContentText("All progress will be lost.");

		// Get the Stage.
		final Stage stage = (Stage) getDialogPane().getScene().getWindow();

		// Add a custom icon.
		stage.getIcons().add(icon);
	}

}
