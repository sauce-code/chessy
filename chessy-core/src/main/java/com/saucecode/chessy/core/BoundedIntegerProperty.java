package com.saucecode.chessy.core;

import javafx.beans.property.SimpleIntegerProperty;

public class BoundedIntegerProperty extends SimpleIntegerProperty {

	private final int lowerInclusive;

	private final int upperInclusive;

	public BoundedIntegerProperty(int initialValue, int lowerInclusive, int upperInclusive) {
		super(initialValue);
		this.lowerInclusive = lowerInclusive;
		this.upperInclusive = upperInclusive;
	}

	@Override
	public void set(int newValue) {
		if (newValue < lowerInclusive) {
			throw new IllegalArgumentException("newValue must not be smaller than " + lowerInclusive);
		}
		if (newValue > upperInclusive) {
			throw new IllegalArgumentException("newValue must not be bigger than " + upperInclusive);
		}
		super.set(newValue);
	}

}
