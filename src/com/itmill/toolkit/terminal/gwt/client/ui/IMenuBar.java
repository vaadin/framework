package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IMenuBar extends MenuBar implements Paintable {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "i-menubar";

    /** For server connections **/
    protected String uidlId;
    protected ApplicationConnection client;

    protected final IMenuBar hostReference = this;
    protected static final boolean vertical = true;
    protected String submenuIcon = null;
    protected boolean collapseItems = true;

    protected MenuItem moreItem = null;

    // Construct an empty command to be used when the item has no command
    // associated
    protected static final Command emptyCommand = null;

    /**
     * The constructor should first call super() to initialize the component and
     * then handle any initialization relevant to IT Mill Toolkit.
     */
    public IMenuBar() {
        // Create an empty horizontal menubar
        super();
        DOM.setStyleAttribute(this.getElement(), "white-space", "nowrap");

        // This method call of the Paintable interface sets the component
        // style name in DOM tree
        setStyleName(CLASSNAME);
    }

    public IMenuBar(boolean vertical) {
        super(vertical);
        DOM.setStyleAttribute(this.getElement(), "white-space", "nowrap");

        // This method call of the Paintable interface sets the component
        // style name in DOM tree
        setStyleName(CLASSNAME + "_submenu");
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

        // For future connections
        this.client = client;
        uidlId = uidl.getId();

        // Empty the menu every time it receives new information
        if (!this.getItems().isEmpty()) {
            this.clearItems();
        }

        UIDL options = uidl.getChildUIDL(0);
        // For GWT 1.5
        //this.setAnimationEnabled(options.getBooleanAttribute("animationEnabled"
        // ))
        // ;

        if (options.hasAttribute("submenuIcon")) {
            submenuIcon = client.translateToolkitUri(uidl.getChildUIDL(0)
                    .getStringAttribute("submenuIcon"));
        } else {
            submenuIcon = null;
        }

        collapseItems = options.getBooleanAttribute("collapseItems");

        if (collapseItems) {
            UIDL moreItemUIDL = options.getChildUIDL(0);
            StringBuffer itemHTML = new StringBuffer();
            itemHTML.append("<p>");
            if (moreItemUIDL.hasAttribute("icon")) {
                itemHTML.append("<img src=\""
                        + client.translateToolkitUri(moreItemUIDL
                                .getStringAttribute("icon"))
                        + "\" align=\"left\" />");
            }
            itemHTML.append(moreItemUIDL.getStringAttribute("text"));
            itemHTML.append("</p>");
            moreItem = new MenuItem(itemHTML.toString(), true, emptyCommand);
        }

        UIDL items = uidl.getChildUIDL(1);
        Iterator itr = items.getChildIterator();
        Stack iteratorStack = new Stack();
        Stack menuStack = new Stack();
        MenuBar currentMenu = this;

        // int topLevelWidth = 0;

        while (itr.hasNext()) {
            UIDL item = (UIDL) itr.next();
            MenuItem currentItem = null; // For receiving the item

            String itemText = item.getStringAttribute("text");
            final int itemId = item.getIntAttribute("id");

            boolean itemHasCommand = item.getBooleanAttribute("command");

            // Construct html from the text and the optional icon
            StringBuffer itemHTML = new StringBuffer();

            itemHTML.append("<p>");

            if (item.hasAttribute("icon")) {
                itemHTML.append("<img src=\""
                        + client.translateToolkitUri(item
                                .getStringAttribute("icon"))
                        + "\" align=\"left\" />");
            }

            itemHTML.append(itemText);

            if (currentMenu != this && item.getChildCount() > 0
                    && submenuIcon != null) {
                itemHTML.append("<img src=\"" + submenuIcon
                        + "\" align=\"right\" />");
            }

            itemHTML.append("</p>");

            Command cmd = null;

            // Check if we need to create a command to this item
            if (itemHasCommand) {
                // Construct a command that fires onMenuClick(int) with the
                // item's id-number
                cmd = new Command() {
                    public void execute() {
                        hostReference.onMenuClick(itemId);
                    }
                };
            }

            currentItem = currentMenu.addItem(itemHTML.toString(), true, cmd);

            if (item.getChildCount() > 0) {
                menuStack.push(currentMenu);
                iteratorStack.push(itr);
                itr = item.getChildIterator();
                currentMenu = new IMenuBar(vertical);
                currentItem.setSubMenu(currentMenu);
            }

            while (!itr.hasNext() && !iteratorStack.empty()) {
                itr = (Iterator) iteratorStack.pop();
                currentMenu = (MenuBar) menuStack.pop();
            }
        }// while

        // we might need to collapse the top-level menu
        if (collapseItems) {
            int topLevelWidth = 0;

            int ourWidth = this.getOffsetWidth();

            int i = 0;
            for (; i < getItems().size() && topLevelWidth < ourWidth; i++) {
                MenuItem item = (MenuItem) getItems().get(i);
                topLevelWidth += item.getOffsetWidth();
            }

            if (topLevelWidth > this.getOffsetWidth()) {
                ArrayList toBeCollapsed = new ArrayList();
                MenuBar collapsed = new IMenuBar(vertical);
                for (int j = i - 2; j < getItems().size(); j++) {
                    toBeCollapsed.add(getItems().get(j));
                }

                for (int j = 0; j < toBeCollapsed.size(); j++) {
                    MenuItem item = (MenuItem) toBeCollapsed.get(j);
                    removeItem(item);

                    // it's ugly, but we have to insert the submenu icon
                    if (item.getSubMenu() != null && submenuIcon != null) {
                        String itemHTML = item.getHTML();
                        StringBuffer itemText = new StringBuffer(itemHTML
                                .substring(0, itemHTML.length() - 4));
                        itemText.append("<img src=\"" + submenuIcon
                                + "\" align=\"right\" /></p>");
                        item.setHTML(itemText.toString());
                    }

                    collapsed.addItem(item);
                }

                moreItem.setSubMenu(collapsed);
                addItem(moreItem);
            }
        }
    }// updateFromUIDL

    /**
     * This is called by the items in the menu and it communicates the
     * information to the server
     * 
     * @param clickedItemId
     *            id of the item that was clicked
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
