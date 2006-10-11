package com.enably.tk.demo.gogame;

import java.util.Iterator;
import java.util.LinkedList;

public class Game {

	// States for the positions on the game board.
	/** Empty position. */
	public static final int EMPTY = 0;
	/** Black stone. */
	public static final int BLACK = 1;
	/** White stone */
	public static final int WHITE = 2;

	// Two-dimentionel array holding the current state of the game.
	private int[][] state;

	// List of listeners listening to game events.
	private LinkedList listeners = new LinkedList();

	// Names of the players.
	private String blackPlayer;
	private String whitePlayer;

	// Number of moves made so far.
	private int moves = 0;

	// Number of captured stones
	private int captured[] = { 0, 0, 0 };

	/** Creates a new game with the specified board size and player names.
	 */
	public Game(int boardSize, String blackPlayer, String whitePlayer) {

		// Assign player names.
		this.blackPlayer = blackPlayer;
		this.whitePlayer = whitePlayer;

		// Initialize board.
		state = new int[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++)
			for (int j = 0; j < boardSize; j++)
				state[i][j] = EMPTY;
	}

	/** Gets the current state of the game as a two-dimentional array
	 * representing the board, with the states Game.EMPTY, Game.BLACK 
	 * and Game.WHITE.
	 */
	public int[][] getState() {
		return state;
	}

	/** Adds a black or white stone to the specified position on the
	 * board.
	 */
	public void addStone(int i, int j, boolean isBlack) {
		if (state[i][j] == EMPTY) {
			state[i][j] = isBlack ? BLACK : WHITE;
			moves++;
			removeDeadStonesAround(i, j);
			for (Iterator li = listeners.iterator(); li.hasNext();)
				 ((Game.Listener) li.next()).stoneIsAdded(this, i, j, isBlack);
		}
	}

	/** Adds a listener to the list of listeners
	 */
	public void addGameListener(Game.Listener listener) {
		listeners.add(listener);
	}

	/** Removes a listener from the list of listeners.
	 */
	public void removeGameListener(Game.Listener listener) {
		listeners.remove(listener);
	}

	/** Interface for implementing a listener listening to Go-game events.
	 */
	public interface Listener {
		/** Called whenever a stone is added to the game.
		 */
		public void stoneIsAdded(Game game, int i, int j, boolean isBlack);
	}

	/** This function returns the state of the game as a string.
	 */
	public String toString() {
		return blackPlayer
			+ " (Black) vs. "
			+ whitePlayer
			+ " (White) ("
			+ state.length
			+ "x"
			+ state[0].length
			+ ", "
			+ moves
			+ " moves done"
			+ (captured[WHITE] > 0
				? (", Black has captured " + captured[WHITE] + " stones")
				: "")
			+ (captured[BLACK] > 0
				? (", White has captured " + captured[BLACK] + " stones")
				: "")
			+ ")";
	}

	/** Gets the black player's name.
	 */
	public String getBlackPlayer() {
		return blackPlayer;
	}

	/** Gets the number of moves so far.
	 */
	public int getMoves() {
		return moves;
	}

	/** Gets the white player's name.
	 */
	public String getWhitePlayer() {
		return whitePlayer;
	}

	/** Remove dead stones. Removes stones that are dead as
	 * defined by the rules of go. The state is only checked for
	 * the four stones surrounding the last stone added */
	private void removeDeadStonesAround(int lastx, int lasty) {

		// Remove possible victims of attack
		removeIfDead(lastx - 1, lasty, lastx, lasty);
		removeIfDead(lastx + 1, lasty, lastx, lasty);
		removeIfDead(lastx, lasty + 1, lastx, lasty);
		removeIfDead(lastx, lasty - 1, lastx, lasty);

		// Remove stones on suicide
		removeIfDead(lastx, lasty, -1, -1);
	}

	/** Remove area, if it is dead. This fairly complicated algorithm 
	 * tests if area starting from (x,y) is dead and removes it in
	 * such case. The last stone (lastx,lasty) is always alive. */
	private void removeIfDead(int x, int y, int lastx, int lasty) {

		// Only check the stones on the board
		int width = state.length;
		int height = state[0].length;
		if (x < 0 || y < 0 || x >= width || y >= width)
			return;

		// Not dead if empty of same color than the last stone
		int color = state[x][y];
		if (color == EMPTY
			|| (lastx >= 0 && lasty >= 0 && color == state[lastx][lasty]))
			return;

		// Check areas by growing
		int checked[][] = new int[state.length][state[0].length];
		checked[x][y] = color;
		while (true) {
			boolean stillGrowing = false;
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++)
					for (int o = 0; o < 4; o++)
						if (checked[i][j] == EMPTY) {
							int nx = i;
							int ny = j;
							switch (o) {
								case 0 :
									nx++;
									break;
								case 1 :
									nx--;
									break;
								case 2 :
									ny++;
									break;
								case 3 :
									ny--;
									break;
							}
							if (nx >= 0
								&& ny >= 0
								&& nx < width
								&& ny < height
								&& checked[nx][ny] == color) {
								checked[i][j] = state[i][j];
								if (checked[i][j] == color)
									stillGrowing = true;
								else if (checked[i][j] == EMPTY)
									
									// Freedom found
									return;
							}

						}
			// If the area stops growing and no freedoms found,
			// it is dead. Remove it
			if (!stillGrowing) {
				for (int i = 0; i < width; i++)
					for (int j = 0; j < height; j++)
						if (checked[i][j] == color) {
							state[i][j] = EMPTY;
							captured[color]++;
						}
				return;
			}
		}
	}
	
	/** Get the number of white stones captures */
	public int getCapturedWhiteStones() {
		return captured[WHITE];	
	}	

	/** Get the number of black stones captures */
	public int getCapturedBlackStones() {
		return captured[BLACK];	
	}	
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */