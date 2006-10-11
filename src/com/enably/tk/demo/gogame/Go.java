package com.enably.tk.demo.gogame;

import com.enably.tk.Application;
import com.enably.tk.data.Item;
import com.enably.tk.data.Property;
import com.enably.tk.data.util.IndexedContainer;
import com.enably.tk.event.*;
import com.enably.tk.ui.*;

/** The classic game 'Go' as an example for the Millstone framework. 
 *
 * @author IT Mill Ltd.
 * @see com.enably.tk.Application
 */
public class Go
	extends Application
	implements Action.Handler, Property.ValueChangeListener {

	/* An IndexedContainer will hold the list of players - it can be
	 * displayed directly by the 'Table' ui component. */
	private static IndexedContainer players = new IndexedContainer();
	
	// This action will be triggered when a player challenges another.
	private static Action challengeAction = new Action("Challenge", null);

	// The players can have a current game.
	static {
		players.addContainerProperty("Current game", Game.class, null);
	}

	// The layout
	private CustomLayout layout = new CustomLayout("goroom");
	
	// Label to be displayed in the login window.
	private TextField loginName = new TextField("Who are you stranger?", "");

	// Button for leaving the game
	private Button leaveButton = new Button("Leave game", this, "close");

	// Button for logging in		
	private Button loginButton = new Button("Enter game", this, "login");
	

	// A 'Table' ui component will be used to show the list of players.
 	private Table playersTable;
 	
 	// Our Go-board ('Board') component.
	private Board board = null;

	/** The initialization method that is the only requirement for
	 * inheriting the com.enably.tk.service.Application class. It will
	 * be automatically called by the framework when a user accesses the
	 * application.
	 * We'll initialize our components here.
	 */
	public void init() {

		// Use the GO theme that includes support for goboard component
		// and goroom layout
		setTheme("gogame");

		// Initialize main window with created layout
		addWindow(new Window("Game of GO",layout));
		
		// Xreate a table for showing the players in the IndexedContainer 'players'.
		playersTable = new Table("Players", players);
		playersTable.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
		playersTable.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_ID);
		playersTable.addActionHandler(this);
		playersTable.setPageBufferingEnabled(false);

		// Add the Table and a Button to the main window. 		
		layout.addComponent(playersTable,"players");
		layout.addComponent(leaveButton,"logoutbutton");

		// Hide game components
		leaveButton.setVisible(false);
		playersTable.setVisible(false);

		// Add login functionality
		layout.addComponent(loginName,"loginname");
		loginButton.dependsOn(loginName);
		layout.addComponent(loginButton,"loginbutton");
	}

	/** This function is called when a player tries to log in.
	 */
	public void login() {
		String name = loginName.toString();
		if (name.length() > 0 && !players.containsId(name)) {

			// Login successful
			setUser(name);

			// Add user to player list.
			Item user = players.addItem(name);
			((Property.ValueChangeNotifier) 
				user.getItemProperty("Current game")).addListener(this);

			// Update visible components
			layout.removeComponent(loginName);
			layout.removeComponent(loginButton);
			leaveButton.setVisible(true);
			playersTable.setVisible(true);
		}
	}

	// On logout, remove user from the player list
	public void close() {
		if (getUser() != null) {
			
			// Remove user from the player list.
			players.removeItem(getUser());
		}
		super.close();
	}


	/** Implementing the Action.Handler interface - this function returns
	 * the available actions.
	 * @see com.enably.tk.event.Action.Handler
	 */
	public Action[] getActions(Object target, Object source) {
		Property p = players.getContainerProperty(target, "Current game");
		if (p != null && target != null && !target.equals(getUser())) {
			Game game = (Game) p.getValue();
			if (game == null) {
				return new Action[] { challengeAction };
			}
		}

		return new Action[] {
		};
	}

	/** Implementing the Action.Handler interface - this function handles
	 * the specified action.
	 * @see com.enably.tk.event.Action.Handler
	 */
	public void handleAction(Action action, Object sender, Object target) {
		if (action == challengeAction) {
			Property p = players.getContainerProperty(target, "Current game");
			if (p != null && target != null && !target.equals(getUser())) {
				Game game = (Game) p.getValue();
				if (game == null) {
					game = new Game(9, (String) getUser(), (String) target);
					p.setValue(game);
					players.getContainerProperty(getUser(), "Current game").setValue(
						game);
				}
			}
		}
	}

	/** Implementing the Property.ValueChangeListener interface - this function 
	 * is called when a value change event occurs.
	 * @see com.enably.tk.data.Property.ValueChangeListener
	 */
	public void valueChange(Property.ValueChangeEvent event) {
		if (board != null)
			layout.removeComponent(board);
		Game game = (Game) event.getProperty().getValue();
		if (game != null) {
			board = new Board(game, game.getBlackPlayer() == getUser());
			layout.addComponent(board,"board");
		}
	}
}

/* This Millstone sample code is public domain. *  
 * For more information see www.millstone.org.  */