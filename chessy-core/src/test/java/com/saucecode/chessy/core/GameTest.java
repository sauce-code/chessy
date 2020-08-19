package com.saucecode.chessy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.saucecode.chessy.core.logic.Board;

class GameTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		new JavaFXInitializer();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testClone() throws InterruptedException {
		final Game game1 = new Game();
		final Game game2 = new Game();
		while (game1.busyProperty().get()) {
			Thread.sleep(1L);
		}
		while (game2.busyProperty().get()) {
			Thread.sleep(1L);
		}
		assertEquals(game1, game2);
	}

	@Test
	void testABC() throws InterruptedException {
		final Game game = new Game();
		while (game.busyProperty().get()) {
			Thread.sleep(1L);
		}
		assertEquals(Player.WHITE, game.currentPlayerProperty().get());
	}

	@Test
	void testCurrentPlayer() throws InterruptedException {
		final Game game = new Game();
		final GameBuffer gameBuffer = new GameBuffer(game);
		assertEquals(Player.WHITE, game.currentPlayerProperty().get());
		gameBuffer.select(new Position(0, 1));
		gameBuffer.select(new Position(0, 2)); // a.i. move happens here
		assertEquals(Player.WHITE, game.currentPlayerProperty().get());
	}

	@Test
	void testCheckMate() throws InterruptedException {
		final Game game = new Game();
		final GameBuffer gameBuffer = new GameBuffer(game);
		game.aiBlackActiveProperty().set(false);

		assertEquals(Player.WHITE, game.currentPlayerProperty().get());

		// move white pawn f2 f3
		gameBuffer.select(new Position(5, 1));
		gameBuffer.select(new Position(5, 2));
//		assertFalse(game.busyProperty().get());
//		Player aaa = game.currentPlayerProperty().get();
//		System.out.println(game);
//		System.out.println(game.currentPlayerProperty().get());
//		System.out.println(game.getCurrentPlayer());
		assertEquals(Player.BLACK, game.currentPlayerProperty().get());
		assertEquals(Player.BLACK, game.getCurrentPlayer());

		// move black pawn e7 e6
		gameBuffer.select(new Position(4, 6));
		gameBuffer.select(new Position(4, 5));

		assertEquals(Player.WHITE, game.currentPlayerProperty().get());

		// move white pawn g2 g4
		gameBuffer.select(new Position(6, 1));
		gameBuffer.select(new Position(6, 3));

		assertEquals(Player.BLACK, game.currentPlayerProperty().get());

		// move black queen d8 h4
		gameBuffer.select(new Position(3, 7));
		gameBuffer.select(new Position(7, 3));

		// white should be in checkmate now
		assertEquals(Player.WHITE, game.inCheckmateProperty().get());
		assertEquals(-Board.CHECKMATE_SCORE, game.scoreWhiteProperty().get());
		assertEquals(Board.CHECKMATE_SCORE, game.scoreBlackProperty().get());
	}

	@Test
	void test2() {
		final Game game = new Game();
		assertEquals(0, game.scoreWhiteProperty().get());
	}

	@Test
	void test3() {
		final Game game = new Game();
		assertEquals(false, game.gameOverProperty().get());
	}

}
