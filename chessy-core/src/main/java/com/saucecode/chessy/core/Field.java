package com.saucecode.chessy.core;

public class Field implements FieldI {

	private final Figure figure;

	private final Modifier modifier;

	public Field(Figure figure, Modifier modifier) {
		this.figure = figure;
		this.modifier = modifier;
	}

	@Override
	public Figure getFigure() {
		return figure;
	}

	@Override
	public Modifier getModifier() {
		return modifier;
	}

}
