package com.saucecode.chessy.core;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public interface GameI {
	
	public static final int PLY_STD = 4;
	
	public static final int PLY_MIN = 1;
	
	public static final int PLY_MAX = 5;
	
	public static final int DIM = 8;
	
	public static final boolean BUSY_STD = false;
	
	public static final boolean BLACK_AI_STD = true;

	public SimpleStringProperty boardValueWhiteProperty();
	
	public SimpleStringProperty boardValueBlackProperty();
	
	public SimpleObjectProperty<Board> boardProperty();
	
	public SimpleBooleanProperty busyProperty();
	
	public void select(Selection s);
	
	public SimpleObjectProperty<Selection> selectionProperty();
	
	public SimpleIntegerProperty plyProperty();
	
	public SimpleBooleanProperty blackAIProperty();
	
	public void reset();
	
	public SimpleObjectProperty<Player> inCheckProperty();
	
	public SimpleObjectProperty<Player> inStalemateProperty();
	
	public SimpleBooleanProperty gameOverProperty();
	
	public SimpleObjectProperty<Player> currentPlayerProperty();
	
	public SimpleBooleanProperty resettable();
	
	public SimpleBooleanProperty undoable();
	
}
