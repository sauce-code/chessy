package com.saucecode.chessy.core;

import java.util.Objects;

/**
 * In implementation of {@link FieldI}.
 *
 * @author Torben Kr&uuml;ger
 */
public class Field implements FieldI {

	/**
	 * Stored figure type.
	 */
	private final FigureType figureType;

	/**
	 * Stored Modifier.
	 */
	private final Modifier modifier;

	/**
	 * Creates a new Field with given values.
	 *
	 * @param figureType figure type
	 * @param modifier   modifier
	 */
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Field other = (Field) obj;
		if (figureType != other.figureType) {
			return false;
		}
		if (modifier != other.modifier) {
			return false;
		}
		return true;
	}

}
