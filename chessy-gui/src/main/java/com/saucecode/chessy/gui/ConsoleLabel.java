package com.saucecode.chessy.gui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 * Simple subclass of {@link Label}.
 *
 * @see Label
 *
 * @author Torben Kr&uuml;ger
 */
public class ConsoleLabel extends Label {

	// load fonts here
	static {
		Font.loadFonts(
				ConsoleLabel.class.getResource("/JetBrainsMono-1.0.3/ttf/JetBrainsMono-Regular.ttf").toExternalForm(),
				12.0);
	}

	/**
	 * font used for all instances of {@link ConsoleLabel}.
	 */
	public static final Font font = Font.font("JetBrains Mono");

	/**
	 * Creates a new {@link ConsoleLabel}.
	 */
	public ConsoleLabel() {
		super();
		fontProperty().set(font);
	}

	/**
	 * Creates a new {@link ConsoleLabel} with given initial text.
	 *
	 * @param text initial text
	 */
	public ConsoleLabel(String text) {
		super(text);
		fontProperty().set(font);
	}

	/**
	 * Creates a new {@link ConsoleLabel} with given initial text and initial graphic.
	 *
	 * @param text    initial text
	 * @param graphic initial graphic
	 */
	public ConsoleLabel(String text, Node graphic) {
		super(text, graphic);
		fontProperty().set(font);
	}

}
