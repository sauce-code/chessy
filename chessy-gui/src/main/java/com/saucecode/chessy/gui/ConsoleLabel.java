package com.saucecode.chessy.gui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class ConsoleLabel extends Label {

	static {
		Font.loadFonts(ConsoleLabel.class.getResource("/JetBrainsMono-1.0.3/ttf/JetBrainsMono-Regular.ttf").toExternalForm(), 12.0);
	}

	public static final Font font = Font.font("JetBrains Mono");

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
