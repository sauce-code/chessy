package com.saucecode.chessy.core.util;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * A subclass of {@link SimpleIntegerProperty}, which checks given values implicitly, if they are within in a certain
 * range, given by {@link #lowerInclusive} and {@link #upperInclusive}.
 *
 * @see SimpleIntegerProperty
 *
 * @author Torben Kr&uuml;ger
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
	 * @throws IllegalArgumentException if {@code lowerInclusive} is greater than {@code upperInclusive}
	 */
	public BoundedIntegerProperty(int initialValue, int lowerInclusive, int upperInclusive) {
		super(initialValue);
		if (lowerInclusive > upperInclusive) {
			throw new IllegalArgumentException("lowerInclusive must not be greater than upperInclusive");
		}
		this.lowerInclusive = lowerInclusive;
		this.upperInclusive = upperInclusive;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws IllegalArgumentException if {@code newValue} is smaller than {@link #lowerInclusive}
	 * @throws IllegalArgumentException if {@code newValue} is greater than {@link #upperInclusive}
	 */
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

	@Override
	public String toString() {
		return "BoundedIntegerProperty [lowerInclusive=" + lowerInclusive + ", upperInclusive=" + upperInclusive
				+ ", getValue()=" + getValue() + "]";
	}

}
