package com.saucecode.chessy.core;

public class Field implements FieldI {

	private final FigureType figure;

	private final Modifier modifier;

	public Field(FigureType figure, Modifier modifier) {
		this.figure = figure;
		this.modifier = modifier;
	}

	@Override
	public FigureType getFigure() {
		return figure;
	}

	@Override
	public Modifier getModifier() {
		return modifier;
	}

	@Override
	public String toString() {
		return "Field [figure=" + figure + ", modifier=" + modifier + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((figure == null) ? 0 : figure.hashCode());
		result = prime * result + ((modifier == null) ? 0 : modifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (figure != other.figure)
			return false;
		if (modifier != other.modifier)
			return false;
		return true;
	}

}
