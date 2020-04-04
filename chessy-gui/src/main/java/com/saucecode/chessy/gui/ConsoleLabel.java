package com.saucecode.chessy.gui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class ConsoleLabel extends Label {
	
	public static final Font font = Font.font("Lucida Console");
	
	public ConsoleLabel() {
		super();
		fontProperty().set(font);
	}
			
	public ConsoleLabel(String text) {
		super(text);
		fontProperty().set(font);
	}
	
	public ConsoleLabel(String text, Node graphic) {
		super(text, graphic);
		fontProperty().set(font);
	}

}
