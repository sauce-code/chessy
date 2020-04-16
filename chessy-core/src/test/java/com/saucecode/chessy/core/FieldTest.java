package com.saucecode.chessy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FieldTest {

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
	void testGetter() {
		final Field field = new Field(FigureType.BISHOP_BLACK, Modifier.FROM);
		assertEquals(FigureType.BISHOP_BLACK, field.getFigure());
		assertEquals(Modifier.FROM, field.getModifier());
	}

	@Test
	void testEquals() {
		final Field field1 = new Field(FigureType.PAWN_WHITE, Modifier.TO);
		final Field field2 = new Field(FigureType.PAWN_WHITE, Modifier.TO);
		assertEquals(field1, field2);
	}

	@Test
	void testHashEquals() {
		final Field field1 = new Field(FigureType.ROOK_BLACK, Modifier.SELECTED);
		final Field field2 = new Field(FigureType.ROOK_BLACK, Modifier.SELECTED);
		assertEquals(field1.hashCode(), field2.hashCode());
	}

	@Test
	void testFigureNull() {
		assertThrows(NullPointerException.class, () -> new Field(null, Modifier.SELECTED));
	}

	@Test
	void testModifierNull() {
		assertThrows(NullPointerException.class, () -> new Field(FigureType.KNIGHT_WHITE, null));
	}

}
