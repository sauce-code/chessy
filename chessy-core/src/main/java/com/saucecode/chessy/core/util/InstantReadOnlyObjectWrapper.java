package com.saucecode.chessy.core.util;

import javafx.beans.property.ReadOnlyObjectWrapper;

public class InstantReadOnlyObjectWrapper<T> extends ReadOnlyObjectWrapper<T> {

	private T instant;
	
	@Override
	public void set(T newValue) {
		// TODO Auto-generated method stub
		super.set(newValue);
		instant = newValue;
	}
	
}
