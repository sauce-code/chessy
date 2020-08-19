package com.saucecode.chessy.core;

import com.sun.javafx.application.PlatformImpl;

class JavaFXInitializer {

	public JavaFXInitializer() {
		initFx();
	}

	private synchronized static void initFx() {
		PlatformImpl.startup(() -> {
		});
	}

}
