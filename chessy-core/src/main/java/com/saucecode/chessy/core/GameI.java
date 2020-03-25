package com.saucecode.chessy.core;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public interface GameI {
	
	public static final int PLY_STD = 4;
	
	public static final int PLY_MIN = 1;
	
	public static final int PLY_MAX = 5;
	
	public static final int DIM = 8;
	
	public static final boolean BUSY_STD = false;
	
	public static final boolean BLACK_AI_STD = true;

	public StringProperty boardValueWhiteProperty();
	
	public StringProperty boardValueBlackProperty();
	
	public ObjectProperty<Board> boardProperty();
	
	public BooleanProperty busyProperty();
	
	public void select(Selection s);
	
	public ObjectProperty<Selection> selectionProperty();
	
	public IntegerProperty plyProperty();
	
	public BooleanProperty blackAIProperty();
	
	public void reset();
	
	public ObjectProperty<Player> inCheckProperty();
	
	public ObjectProperty<Player> inStalemateProperty();
	
	public BooleanProperty gameOverProperty();
	
	public ObjectProperty<Player> currentPlayerProperty();
	
	public BooleanProperty resettable();
	
	public BooleanProperty undoable();
	
}