package com.saucecode.chessy.gui;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class RestartDialog extends Alert {
	
	public RestartDialog() {
		this(AlertType.CONFIRMATION);
	}

	private RestartDialog(AlertType alertType) {
		super(alertType);
		setTitle("Confirmation Dialog");
		setHeaderText("Look, a Confirmation Dialog");
		setContentText("Are you ok with this?");

		Optional<ButtonType> result = showAndWait();
		if (result.get() == ButtonType.OK){
		    // ... user chose OK
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
	}

}
