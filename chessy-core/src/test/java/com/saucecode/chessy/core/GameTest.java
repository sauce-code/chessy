package com.saucecode.chessy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.saucecode.chessy.core.GameI.Player;

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
		Game game1 = new Game();
		Game game2 = new Game();
		Thread.sleep(1_000L); // TODO nicht so
		assertEquals(game1, game2);
	}
	
	@Test
	void testABC() throws InterruptedException {
		Game game = new Game();
		Thread.sleep(1_000L);
		assertEquals(Player.WHITE, game.currentPlayerProperty().get());
	}
	
	@Test
	void test2() {
		Game game = new Game();
		assertEquals(0, game.boardValueWhiteProperty().get());
	}
	
	@Test
	void test3() {
		Game game = new Game();
		assertEquals(false, game.gameOverProperty().get());
	}

}
