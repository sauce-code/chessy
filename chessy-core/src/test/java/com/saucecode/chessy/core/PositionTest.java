package com.saucecode.chessy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PositionTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
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
	void testGetX() {
		final Position pos = new Position(1, 2);
		assertEquals(1, pos.getX());
	}

	@Test
	void testGetY() {
		final Position pos = new Position(4, 7);
		assertEquals(7, pos.getY());
	}

	@Test
	void testEquals() {
		final Position pos1 = new Position(6, 3);
		final Position pos2 = new Position(6, 3);
		assertEquals(pos1, pos2);
	}

	@Test
	void testHashEquals() {
		final Position pos1 = new Position(0, 5);
		final Position pos2 = new Position(0, 5);
		assertEquals(pos1.hashCode(), pos2.hashCode());
	}

	@Test
	void testExceptionXNegative() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Position(-1, 5);
		});
	}

	@Test
	void testExceptionXPositive() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Position(9, 3);
		});
	}

	@Test
	void testExceptionYNegative() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Position(4, -2);
		});
	}

	@Test
	void testExceptionYPositive() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Position(7, 8);
		});
	}

}
