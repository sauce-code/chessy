package com.saucecode.chessy.core;

public class Field {
	
	public boolean isSelected() {
		return false;
	}
	
	public boolean isFrom() {
		return false;
	}
	
	public boolean isTo() {
		return false;
	}
	
	public Class<? extends Figure> getType() {
		return null;
	}
	
	public Player getOwner() {
		return null;
	}

	
	public enum Modifier {
		SELECTED,
		FROM,
		TO
	}
	
	
}
