package com.saucecode.chessy.gui;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;

/**
 * A about info alert box.
 *
 * @author Torben Kr&uuml;ger
 */
public class AboutAlert extends Alert {

	/**
	 * Creates a new About Alert.
	 *
	 * @param logo the icon, which shall be used
	 */
	public AboutAlert(Image logo) {
		super(AlertType.INFORMATION);
		final ImageView image = new ImageView(logo);
		image.setFitWidth(80.0);
		image.setFitHeight(80.0);
		setGraphic(image);
		initStyle(StageStyle.UTILITY);
		setTitle("About");
		setHeaderText("Chessy " + AboutAlert.class.getPackage().getImplementationVersion());
		final StringBuilder ct = new StringBuilder();
		ct.append("https://github.com/sauce-code/chessy\n");
		ct.append('\n');
		ct.append("Coded by Torben Kr\u00fcger\n");
		ct.append("Images by Igor Krizanovskij\n");
		ct.append("Font JetBrains Mono by JetBrains\n");
		setContentText(ct.toString());
	}

}
