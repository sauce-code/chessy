package com.saucecode.chessy.gui;

import com.saucecode.chessy.core.GameI;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ResetDialog extends Alert {

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
