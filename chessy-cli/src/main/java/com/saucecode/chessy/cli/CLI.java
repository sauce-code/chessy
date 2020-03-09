package com.saucecode.chessy.cli;

import com.saucecode.chessy.core.Game;

public class CLI {

	public static void main(String[] args) {
		Game game = new Game();
		Terminal terminal = new Terminal();
		int fromX, fromY, toX, toY;
		while (true) {
			System.out.println(game);
			System.out.println("fromX: ");
			fromX = terminal.readInt();
			System.out.println("fromY: ");
			fromY = terminal.readInt();
			System.out.println("toX: ");
			toX = terminal.readInt();
			System.out.println("toY: ");
			toY = terminal.readInt();
			game.move(fromX, fromY, toX, toY);
		}
	}

}
