package com.saucecode.chessy.core.util;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * A subclass of {@link SimpleIntegerProperty}, which checks given values
 * implicit, if they are within in a certain range, given by
 * {@link #lowerInclusive} and {@link #upperInclusive}.
 * 
 * @see SimpleIntegerProperty
 * 
 * @author Torben Kr&uumlger
 */
public class BoundedIntegerProperty extends SimpleIntegerProperty {

	/**
	 * Lower border, inclusive.
	 */
	private final int lowerInclusive;

	/**
	 * Upper border, inclusive.
	 */
	private final int upperInclusive;

	/**
	 * Creates a new {@link BoundedIntegerProperty}.
	 * 
	 * @param initialValue   initialValue
	 * @param lowerInclusive lower border, inclusive
	 * @param upperInclusive upper border, inclusive
	 */
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
