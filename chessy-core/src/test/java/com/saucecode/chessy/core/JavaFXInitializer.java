package com.saucecode.chessy.core;

import com.sun.javafx.application.PlatformImpl;

public class JavaFXInitializer {

	public JavaFXInitializer() {
		initFx();
	}

	private synchronized static void initFx() {
		PlatformImpl.startup(() -> {
		});
	}

}
