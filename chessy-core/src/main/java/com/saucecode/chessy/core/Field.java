package com.saucecode.chessy.core;

import java.util.Objects;

public class Field implements FieldI {

	private final FigureType figureType;

	private final Modifier modifier;

	public Field(FigureType figureType, Modifier modifier) {
		Objects.requireNonNull(figureType, "figureType must not be null");
		Objects.requireNonNull(modifier, "figureType must not be null");
		this.figureType = figureType;
		this.modifier = modifier;
	}

	@Override
	public FigureType getFigure() {
		return figureType;
	}

	@Override
	public Modifier getModifier() {
		return modifier;
	}

	@Override
	public String toString() {
		return "Field [figure=" + figureType + ", modifier=" + modifier + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((figureType == null) ? 0 : figureType.hashCode());
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
		if (figureType != other.figureType)
			return false;
		if (modifier != other.modifier)
			return false;
		return true;
	}

}
