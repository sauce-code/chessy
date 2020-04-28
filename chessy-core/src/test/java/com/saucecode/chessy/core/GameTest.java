package com.saucecode.chessy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.saucecode.chessy.core.logic.Board;

class GameTest {
	
	public static final long WAIT_INTERVAL = 1L;
	
	private void wait(Game game) throws InterruptedException {
		while (game.isLocked()) {
			Thread.sleep(WAIT_INTERVAL);
		}
	}

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
		wait(game);
		assertEquals(Player.WHITE, game.currentPlayerProperty().get());
		game.select(new Position(0, 1));
		wait(game);
		game.select(new Position(0, 2));
		wait(game); // a.i. move happens here
		assertEquals(Player.WHITE, game.currentPlayerProperty().get());
	}
	
	@Test
	void testCheckMate() throws InterruptedException {
		final Game game = new Game();
		wait(game);
		game.aiBlackActiveProperty().set(false);
		
		assertEquals(Player.WHITE, game.currentPlayerProperty().get());
		
		// move white pawn f2 f3
		game.select(new Position(5, 1));
		wait(game);
		game.select(new Position(5, 2));
		wait(game);
		
		assertEquals(Player.BLACK, game.currentPlayerProperty().get());
		
		// move black pawn e7 e6
		game.select(new Position(4, 6));
		wait(game);
		game.select(new Position(4, 5));
		wait(game);
		
		assertEquals(Player.WHITE, game.currentPlayerProperty().get());
		
		// move white pawn g2 g4
		game.select(new Position(6, 1));
		wait(game);
		game.select(new Position(6, 3));
		wait(game);
		
		assertEquals(Player.BLACK, game.currentPlayerProperty().get());
		
		// move black queen d8 h4
		game.select(new Position(3, 7));
		wait(game);
		game.select(new Position(7, 3));
		wait(game);
		
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
