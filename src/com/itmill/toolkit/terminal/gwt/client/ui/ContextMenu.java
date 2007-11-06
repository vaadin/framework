package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class ContextMenu extends PopupPanel {

	private ActionOwner actionOwner;

	private CMenuBar menu = new CMenuBar();

	private int left;

	private int top;

	/**
	 * This method should be used only by Client object as only one per client
	 * should exists. Request an instance via client.getContextMenu();
	 * 
	 * @param cli
	 *            to be set as an owner of menu
	 */
	public ContextMenu() {
		super(true);
		setWidget(menu);
		setStyleName("i-contextmenu");
	}

	/**
	 * Sets the element from which to build menu
	 * 
	 * @param ao
	 */
	public void setActionOwner(ActionOwner ao) {
		this.actionOwner = ao;
	}

	/**
	 * Shows context menu at given location.
	 * 
	 * @param left
	 * @param top
	 */
	public void showAt(int left, int top) {
		this.left = left;
		this.top = top;
		menu.clearItems();
		Action[] actions = actionOwner.getActions();
		for (int i = 0; i < actions.length; i++) {
			Action a = actions[i];
			menu.addItem(new MenuItem(a.getHTML(), true, a));
		}

		this.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				// mac FF gets bad width due GWT popups overflow hacks,
				// re-determine width
				offsetWidth = menu.getOffsetWidth();
				int left = ContextMenu.this.left;
				int top = ContextMenu.this.top;
				if (offsetWidth + left > Window.getClientWidth()) {
					left = left - offsetWidth;
					if (left < 0)
						left = 0;
				}
				if (offsetHeight + top > Window.getClientHeight()) {
					top = top - offsetHeight;
					if (top < 0)
						top = 0;
				}
				setPopupPosition(left, top);
			}
		});
	}

	public void showAt(ActionOwner ao, int left, int top) {
		setActionOwner(ao);
		showAt(left, top);
	}

	/**
	 * Extend standard Gwt MenuBar to set proper settings and to override
	 * onPopupClosed method so that PopupPanel gets closed.
	 */
	class CMenuBar extends MenuBar {
		public CMenuBar() {
			super(true);
		}

		public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
			super.onPopupClosed(sender, autoClosed);
			ContextMenu.this.hide();
		}
	}
}
