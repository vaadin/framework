package com.enably.tk.demo.gogame;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.enably.tk.terminal.PaintException;
import com.enably.tk.terminal.PaintTarget;
import com.enably.tk.ui.AbstractComponent;

public class Board extends AbstractComponent implements Game.Listener {

	// The game.
	private Game game;
	
	// Players color.
	private boolean playerPlaysBlack;
	
	// Last played coordinates.
	private int lastX = -1;
	private int lastY = -1;

	/** Creates board for the specified game, assuming the player plays
	 * the specified color.
	 */
	public Board(Game game, boolean playerPlaysBlack) {
		this.game = game;
		this.playerPlaysBlack = playerPlaysBlack;
		game.addGameListener(this);
	}

	/** Called on variable change. If the 'move' variable is found, a stone
	 * is placed for the player accordingly.
	 */
	public void changeVariables(Object source, Map variables) {
		if (variables.containsKey("move")) {
			StringTokenizer st =
				new StringTokenizer((String) variables.get("move"), ",");
			try {
				int i = Integer.valueOf(st.nextToken()).intValue();
				int j = Integer.valueOf(st.nextToken()).intValue();
				game.addStone(i, j, playerPlaysBlack);
			} catch (NumberFormatException ignore) {
			} catch (NoSuchElementException ignore) {
			}
		}
	}

	/** Tag for XML output.
	 */
	public String getTag() {
		return "goboard";
	}


	/** Paint the board to XML.
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		target.addAttribute("xmlns", "GO Sample Namespace");
		target.addAttribute("whitename", game.getWhitePlayer());
		target.addAttribute("blackname", game.getBlackPlayer());
		target.addAttribute("moves", game.getMoves());
		target.addAttribute("whitescaptured", game.getCapturedWhiteStones());
		target.addAttribute("blackscaptured", game.getCapturedBlackStones());
		int[][] state = game.getState();
		for (int j = 0; j < state.length; j++) {
			target.startTag("row");
			for (int i = 0; i < state.length; i++) {
				target.startTag("col");
				if (state[i][j] == Game.EMPTY)
					target.addAttribute("move", "" + i + "," + j);
				else {
					if (i == lastX && j == lastY)
						target.addAttribute(
							"stone",
							state[i][j] == Game.BLACK
								? "black-last"
								: "white-last");
					else
						target.addAttribute(
							"stone",
							state[i][j] == Game.BLACK ? "black" : "white");
				}
				target.endTag("col");
			}
			target.endTag("row");
		}

		target.addVariable(this, "move", "");
	}

	/** Destructor.
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		if (game != null)
			game.removeGameListener(this);
	}

	/** Implementing the Game.Listener interface, called when a stone is added.
	 */
	public void stoneIsAdded(Game game, int i, int j, boolean isBlack) {
		lastX = i;
		lastY = j;
		requestRepaint();
	}
}
