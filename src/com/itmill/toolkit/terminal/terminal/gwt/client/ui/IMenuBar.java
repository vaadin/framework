package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Stack;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IMenuBar extends MenuBar implements Paintable {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "i-menubar";

    /** Component identifier in UIDL communications. */
    protected String uidlId;

    /** Reference to the server connection object. */
    protected ApplicationConnection client;

    /** A host reference for the Command objects */
    protected final IMenuBar hostReference = this;

    /**
     * The constructor should first call super() to initialize the component and
     * then handle any initialization relevant to IT Mill Toolkit.
     */
    public IMenuBar() {
        // The superclass has a lot of relevant initialization
        super();

        // This method call of the Paintable interface sets the component
        // style name in DOM tree
        setStyleName(CLASSNAME);
    }

    /**
     * This method must be implemented to update the client-side component from
     * UIDL data received from server.
     * 
     * This method is called when the page is loaded for the first time, and
     * every time UI changes in the component are received from the server.
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and let the containing layout manage caption, etc.
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Save reference to server connection object to be able to send
        // user interaction later
        this.client = client;

        // Save the UIDL identifier for the component
        uidlId = uidl.getId();

        // Empty the menu every time it receives new information
        if (!this.getItems().isEmpty()) {
            this.clearItems();
        }

        /* Get tree received from server and actualize it in the GWT-MenuBar */

        // For GWT 1.5
        // this.setAnimationEnabled(uidl.getBooleanAttribute("animationEnabled"));
        UIDL items = uidl.getChildUIDL(1);
        Iterator itr = items.getChildIterator();
        Stack iteratorStack = new Stack();
        Stack menuStack = new Stack();
        MenuBar currentMenu = this;

        // Construct an empty command to be used when the item has no command
        // associated
        Command emptyCommand = new Command() {
            public void execute() {
            }
        };

        while (itr.hasNext()) {
            UIDL item = (UIDL) itr.next();
            MenuItem menuItem = null; // For receiving the item

            String itemText = item.getStringAttribute("text");
            final int itemId = item.getIntAttribute("id");

            boolean itemHasCommand = item.getBooleanAttribute("command");

            // Construct html from the text and the optional icon
            if (!item.hasAttribute("icon")) {
                itemText = "<p>" + itemText + "</p>";
            } else {
                itemText = "<p>"
                        + "<img src=\""
                        + client.translateToolkitUri(item
                                .getStringAttribute("icon")) + "\"</img>"
                        + itemText + "</p>";
            }

            // Check if we need to attach a command to this item
            if (itemHasCommand) {
                // Construct a command that fires onMenuClick(int) with the
                // item's id-number
                Command normalCommand = new Command() {
                    public void execute() {
                        hostReference.onMenuClick(itemId);
                    }
                };

                menuItem = currentMenu.addItem(itemText, true, normalCommand);

            } else {
                menuItem = currentMenu.addItem(itemText, true, emptyCommand);
            }

            if (item.getChildCount() > 0) {
                menuStack.push(currentMenu);
                iteratorStack.push(itr);
                itr = item.getChildIterator();
                currentMenu = new MenuBar(true);
                menuItem.setSubMenu(currentMenu);
            }

            if (!itr.hasNext() && !iteratorStack.empty()) {
                itr = (Iterator) iteratorStack.pop();
                currentMenu = (MenuBar) menuStack.pop();
            }
        }// while

    }// updateFromUIDL

    /**
     * This is called by the items in the menu and it communicates the
     * information to the server
     * 
     * @param clickedItemId
     *                id of the item that was clicked
     */
    public void onMenuClick(int clickedItemId) {
        // Updating the state to the server can not be done before
        // the server connection is known, i.e., before updateFromUIDL()
        // has been called.
        if (uidlId != null && client != null) {
            // Communicate the user interaction parameters to server. This call
            // will initiate an AJAX request to the server.
            client.updateVariable(uidlId, "clickedId", clickedItemId, true);
        }
    }
}// class IMenuBar
