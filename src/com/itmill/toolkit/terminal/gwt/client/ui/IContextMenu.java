package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class IContextMenu extends PopupPanel {
	
	private IActionOwner actionOwner;
	
	private CMenuBar menu = new CMenuBar();
	
	/**
	 * This method should be used only by Client object as
	 * only one per client should exists. Request an instance
	 * via client.getContextMenu();
	 * 
	 * @param cli to be set as an owner of menu
	 */
	public IContextMenu() {
		super(true);
		setWidget(menu);
		setStyleName("i-contextmenu");
	}
	
	/**
	 * Sets the element from which to build menu
	 * @param ao
	 */
	public void setActionOwner(IActionOwner ao) {
		this.actionOwner = ao;
	}
	
	/**
	 * Shows context menu at given location.
	 * 
	 * @param left
	 * @param top
	 */
	public void showAt(int left, int top) {
		menu.clearItems();
		IAction[] actions = actionOwner.getActions();
		for (int i = 0; i < actions.length; i++) {
			IAction a = actions[i];
			menu.addItem(new MenuItem(a.getHTML(), true, a));
		}
		
		setPopupPosition(left, top);
		
		show();
	}

	public void showAt(IActionOwner ao, int left, int top) {
		setActionOwner(ao);
		showAt(left, top);
		
	}

	/**
	 * Extend standard Gwt MenuBar to set proper settings and 
	 * to override onPopupClosed method so that PopupPanel gets
	 * closed.
	 */
	class CMenuBar extends MenuBar {
		public CMenuBar() {
			super(true);
		}

		public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
			super.onPopupClosed(sender, autoClosed);
			IContextMenu.this.hide();
		}
	}
}
