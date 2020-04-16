package com.saucecode.chessy.core.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoundedIntegerPropertyTest {

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
	void test() {
		new BoundedIntegerProperty(0, -1, 2);
	}

	@Test
	void test2() {
		assertThrows(IllegalArgumentException.class, () -> new BoundedIntegerProperty(0, 3, 2));
	}

	@Test
	void test3() {
		final BoundedIntegerProperty bip = new BoundedIntegerProperty(0, -2, 4);
		assertThrows(IllegalArgumentException.class, () -> bip.set(-4));
	}

	@Test
	void test4() {
		final BoundedIntegerProperty bip = new BoundedIntegerProperty(0, -5, 7);
		System.out.println(bip);
		assertThrows(IllegalArgumentException.class, () -> bip.set(8));
	}

}
