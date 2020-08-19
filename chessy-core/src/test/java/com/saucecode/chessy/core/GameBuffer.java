package com.saucecode.chessy.core;

class GameBuffer {

	public static final long WAIT_INTERVAL = 1L;

	private final Game game;

	public GameBuffer(Game game) throws InterruptedException {
		this.game = game;
		waitForGame();
	}

	public void select(Position position) throws InterruptedException {
		game.select(position);
		waitForGame();
	}

	private void waitForGame() throws InterruptedException {
		while (game.isLocked() || game.busyProperty().get()) {
			Thread.sleep(WAIT_INTERVAL);
		}
		Thread.sleep(WAIT_INTERVAL); // not good, but works for now
	}

}
