package com.saucecode.chessy.gui;

import com.saucecode.chessy.core.GameI;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ExitDialog extends Alert {
	
	public ExitDialog(GameI game, Image icon) {
		super(AlertType.CONFIRMATION);
		setTitle("Exit");
		setHeaderText("Do you really want to exit?");
		setContentText("All progress will be lost.");
		
		// Get the Stage.
		Stage stage = (Stage)getDialogPane().getScene().getWindow();

		// Add a custom icon.
		stage.getIcons().add(icon);
	}

}
