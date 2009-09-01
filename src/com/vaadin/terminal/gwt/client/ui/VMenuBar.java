package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VMenuBar extends Widget implements Paintable,
        CloseHandler<PopupPanel> {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "v-menubar";

    /** For server connections **/
    protected String uidlId;
    protected ApplicationConnection client;

    protected final VMenuBar hostReference = this;
    protected String submenuIcon = null;
    protected boolean collapseItems = true;
    protected CustomMenuItem moreItem = null;

    // Construct an empty command to be used when the item has no command
    // associated
    protected static final Command emptyCommand = null;

    /** Widget fields **/
    protected boolean subMenu;
    protected ArrayList<CustomMenuItem> items;
    protected Element containerElement;
    protected VOverlay popup;
    protected VMenuBar visibleChildMenu;
    protected VMenuBar parentMenu;
    protected CustomMenuItem selected;

    public VMenuBar() {
        // Create an empty horizontal menubar
        this(false);
    }

    public VMenuBar(boolean subMenu) {
        super();
        setElement(DOM.createDiv());

        items = new ArrayList<CustomMenuItem>();
        popup = null;
        visibleChildMenu = null;

        Element table = DOM.createTable();
        Element tbody = DOM.createTBody();
        DOM.appendChild(getElement(), table);
        DOM.appendChild(table, tbody);

        if (!subMenu) {
            setStyleName(CLASSNAME);
            Element tr = DOM.createTR();
            DOM.appendChild(tbody, tr);
            containerElement = tr;
        } else {
            setStyleName(CLASSNAME + "-submenu");
            containerElement = tbody;
        }
        this.subMenu = subMenu;

        sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
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
        if (!getItems().isEmpty()) {
            clearItems();
        }

        UIDL options = uidl.getChildUIDL(0);

        if (options.hasAttribute("submenuIcon")) {
            submenuIcon = client.translateVaadinUri(uidl.getChildUIDL(0)
                    .getStringAttribute("submenuIcon"));
        } else {
            submenuIcon = null;
        }

        collapseItems = options.getBooleanAttribute("collapseItems");

        if (collapseItems) {
            UIDL moreItemUIDL = options.getChildUIDL(0);
            StringBuffer itemHTML = new StringBuffer();

            if (moreItemUIDL.hasAttribute("icon")) {
                itemHTML.append("<img src=\""
                        + client.translateVaadinUri(moreItemUIDL
                                .getStringAttribute("icon"))
                        + "\" align=\"left\" />");
            }
            itemHTML.append(moreItemUIDL.getStringAttribute("text"));

            moreItem = new CustomMenuItem(itemHTML.toString(), emptyCommand);
        }

        UIDL uidlItems = uidl.getChildUIDL(1);
        Iterator<Object> itr = uidlItems.getChildIterator();
        Stack<Iterator<Object>> iteratorStack = new Stack<Iterator<Object>>();
        Stack<VMenuBar> menuStack = new Stack<VMenuBar>();
        VMenuBar currentMenu = this;

        while (itr.hasNext()) {
            UIDL item = (UIDL) itr.next();
            CustomMenuItem currentItem = null;

            String itemText = item.getStringAttribute("text");
            final int itemId = item.getIntAttribute("id");

            boolean itemHasCommand = item.getBooleanAttribute("command");

            // Construct html from the text and the optional icon
            StringBuffer itemHTML = new StringBuffer();

            if (item.hasAttribute("icon")) {
                itemHTML.append("<img src=\""
                        + client.translateVaadinUri(item
                                .getStringAttribute("icon"))
                        + "\" align=\"left\" />");
            }

            itemHTML.append(itemText);

            if (currentMenu != this && item.getChildCount() > 0
                    && submenuIcon != null) {
                itemHTML.append("<img src=\"" + submenuIcon
                        + "\" align=\"right\" />");
            }

            Command cmd = null;

            if (itemHasCommand) {
                // Construct a command that fires onMenuClick(int) with the
                // item's id-number
                cmd = new Command() {
                    public void execute() {
                        hostReference.onMenuClick(itemId);
                    }
                };
            }

            currentItem = currentMenu.addItem(itemHTML.toString(), cmd);

            if (item.getChildCount() > 0) {
                menuStack.push(currentMenu);
                iteratorStack.push(itr);
                itr = item.getChildIterator();
                currentMenu = new VMenuBar(true);
                currentItem.setSubMenu(currentMenu);
            }

            while (!itr.hasNext() && !iteratorStack.empty()) {
                itr = iteratorStack.pop();
                currentMenu = menuStack.pop();
            }
        }// while

        // we might need to collapse the top-level menu
        // Only needed if there is more than 1 top level item
        // TODO and if width is defined
        if (collapseItems && getItems().size() > 1) {

            int topLevelWidth = 0;

            int ourWidth = getOffsetWidth();

            int i = 0;
            for (; i < getItems().size() && topLevelWidth < ourWidth; i++) {
                CustomMenuItem item = getItems().get(i);
                topLevelWidth += item.getOffsetWidth();
            }

            if (topLevelWidth > getOffsetWidth()) {
                ArrayList<CustomMenuItem> toBeCollapsed = new ArrayList<CustomMenuItem>();
                VMenuBar collapsed = new VMenuBar(true);
                for (int j = i - 2; j < getItems().size(); j++) {
                    toBeCollapsed.add(getItems().get(j));
                }

                for (int j = 0; j < toBeCollapsed.size(); j++) {
                    CustomMenuItem item = toBeCollapsed.get(j);
                    removeItem(item);

                    // it's ugly, but we have to insert the submenu icon
                    if (item.getSubMenu() != null && submenuIcon != null) {
                        StringBuffer itemText = new StringBuffer(item.getHTML());
                        itemText.append("<img src=\"");
                        itemText.append(submenuIcon);
                        itemText.append("\" align=\"right\" />");
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

    /** Widget methods **/

    /**
     * Returns a list of items in this menu
     */
    public List<CustomMenuItem> getItems() {
        return items;
    }

    /**
     * Remove all the items in this menu
     */
    public void clearItems() {
        Element e = getContainingElement();
        while (DOM.getChildCount(e) > 0) {
            DOM.removeChild(e, DOM.getChild(e, 0));
        }
        items.clear();
    }

    /**
     * Returns the containing element of the menu
     *
     * @return
     */
    public Element getContainingElement() {
        return containerElement;
    }

    /**
     * Returns a new child element to add an item to
     *
     * @return
     */
    public Element getNewChildElement() {
        if (subMenu) {
            Element tr = DOM.createTR();
            DOM.appendChild(getContainingElement(), tr);
            return tr;
        } else {
            return getContainingElement();
        }

    }

    /**
     * Add a new item to this menu
     *
     * @param html
     *            items text
     * @param cmd
     *            items command
     * @return the item created
     */
    public CustomMenuItem addItem(String html, Command cmd) {
        CustomMenuItem item = new CustomMenuItem(html, cmd);
        addItem(item);
        return item;
    }

    /**
     * Add a new item to this menu
     *
     * @param item
     */
    public void addItem(CustomMenuItem item) {
        DOM.appendChild(getNewChildElement(), item.getElement());
        item.setParentMenu(this);
        item.setSelected(false);
        items.add(item);
    }

    /**
     * Remove the given item from this menu
     *
     * @param item
     */
    public void removeItem(CustomMenuItem item) {
        if (items.contains(item)) {
            int index = items.indexOf(item);
            Element container = getContainingElement();

            DOM.removeChild(container, DOM.getChild(container, index));
            items.remove(index);
        }
    }

    /*
     * @see
     * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user
     * .client.Event)
     */
    @Override
    public void onBrowserEvent(Event e) {
        super.onBrowserEvent(e);

        Element targetElement = DOM.eventGetTarget(e);
        CustomMenuItem targetItem = null;
        for (int i = 0; i < items.size(); i++) {
            CustomMenuItem item = items.get(i);
            if (DOM.isOrHasChild(item.getElement(), targetElement)) {
                targetItem = item;
            }
        }

        if (targetItem != null) {
            switch (DOM.eventGetType(e)) {

            case Event.ONCLICK:
                itemClick(targetItem);
                break;

            case Event.ONMOUSEOVER:
                itemOver(targetItem);
                break;

            case Event.ONMOUSEOUT:
                itemOut(targetItem);
                break;
            }
        }
    }

    /**
     * When an item is clicked
     *
     * @param item
     */
    public void itemClick(CustomMenuItem item) {
        if (item.getCommand() != null) {
            setSelected(null);

            if (visibleChildMenu != null) {
                visibleChildMenu.hideChildren();
            }

            hideParents();
            DeferredCommand.addCommand(item.getCommand());

        } else {
            if (item.getSubMenu() != null
                    && item.getSubMenu() != visibleChildMenu) {
                setSelected(item);
                showChildMenu(item);
            }
        }
    }

    /**
     * When the user hovers the mouse over the item
     *
     * @param item
     */
    public void itemOver(CustomMenuItem item) {
        setSelected(item);

        boolean menuWasVisible = visibleChildMenu != null;

        if (menuWasVisible && visibleChildMenu != item.getSubMenu()) {
            popup.hide();
            visibleChildMenu = null;
        }

        if (item.getSubMenu() != null && (parentMenu != null || menuWasVisible)
                && visibleChildMenu != item.getSubMenu()) {
            showChildMenu(item);
        }
    }

    /**
     * When the mouse is moved away from an item
     *
     * @param item
     */
    public void itemOut(CustomMenuItem item) {
        if (visibleChildMenu != item.getSubMenu() || visibleChildMenu == null) {
            hideChildMenu(item);
            setSelected(null);
        }
    }

    /**
     * Shows the child menu of an item. The caller must ensure that the item has
     * a submenu.
     *
     * @param item
     */
    public void showChildMenu(CustomMenuItem item) {
        popup = new VOverlay(true, false, true);
        popup.setWidget(item.getSubMenu());
        popup.addCloseHandler(this);

        if (subMenu) {
            popup.setPopupPosition(item.getParentMenu().getAbsoluteLeft()
                    + item.getParentMenu().getOffsetWidth(), item
                    .getAbsoluteTop());
        } else {
            popup.setPopupPosition(item.getAbsoluteLeft(), item.getParentMenu()
                    .getAbsoluteTop()
                    + item.getParentMenu().getOffsetHeight());
        }

        item.getSubMenu().onShow();
        visibleChildMenu = item.getSubMenu();
        item.getSubMenu().setParentMenu(this);

        popup.show();
    }

    /**
     * Hides the submenu of an item
     *
     * @param item
     */
    public void hideChildMenu(CustomMenuItem item) {
        if (visibleChildMenu != null
                && !(visibleChildMenu == item.getSubMenu())) {
            popup.hide();

        }
    }

    /**
     * When the menu is shown.
     */
    public void onShow() {
        if (!items.isEmpty()) {
            (items.get(0)).setSelected(true);
        }
    }

    /**
     * Recursively hide all child menus
     */
    public void hideChildren() {
        if (visibleChildMenu != null) {
            visibleChildMenu.hideChildren();
            popup.hide();
        }
    }

    /**
     * Recursively hide all parent menus
     */
    public void hideParents() {

        if (visibleChildMenu != null) {
            popup.hide();
            setSelected(null);
        }

        if (getParentMenu() != null) {
            getParentMenu().hideParents();
        }
    }

    /**
     * Returns the parent menu of this menu, or null if this is the top-level
     * menu
     *
     * @return
     */
    public VMenuBar getParentMenu() {
        return parentMenu;
    }

    /**
     * Set the parent menu of this menu
     *
     * @param parent
     */
    public void setParentMenu(VMenuBar parent) {
        parentMenu = parent;
    }

    /**
     * Returns the currently selected item of this menu, or null if nothing is
     * selected
     *
     * @return
     */
    public CustomMenuItem getSelected() {
        return selected;
    }

    /**
     * Set the currently selected item of this menu
     *
     * @param item
     */
    public void setSelected(CustomMenuItem item) {
        // If we had something selected, unselect
        if (item != selected && selected != null) {
            selected.setSelected(false);
        }
        // If we have a valid selection, select it
        if (item != null) {
            item.setSelected(true);
        }

        selected = item;
    }

    /**
     * Listener method, fired when this menu is closed
     */
    public void onClose(CloseEvent<PopupPanel> event) {
        hideChildren();
        if (event.isAutoClosed()) {
            hideParents();
        }
        // setSelected(null);
        visibleChildMenu = null;
        popup = null;

    }

    /**
     *
     * A class to hold information on menu items
     *
     */
    private class CustomMenuItem extends UIObject implements HasHTML {

        protected String html = null;
        protected Command command = null;
        protected VMenuBar subMenu = null;
        protected VMenuBar parentMenu = null;

        public CustomMenuItem(String html, Command cmd) {
            setElement(DOM.createTD());

            setHTML(html);
            setCommand(cmd);
            setSelected(false);

            addStyleName("menuitem");
        }

        public void setSelected(boolean selected) {
            if (selected) {
                addStyleDependentName("selected");
            } else {
                removeStyleDependentName("selected");
            }
        }

        /*
         * setters and getters for the fields
         */

        public void setSubMenu(VMenuBar subMenu) {
            this.subMenu = subMenu;
        }

        public VMenuBar getSubMenu() {
            return subMenu;
        }

        public void setParentMenu(VMenuBar parentMenu) {
            this.parentMenu = parentMenu;
        }

        public VMenuBar getParentMenu() {
            return parentMenu;
        }

        public void setCommand(Command command) {
            this.command = command;
        }

        public Command getCommand() {
            return command;
        }

        public String getHTML() {
            return html;
        }

        public void setHTML(String html) {
            this.html = html;
            DOM.setInnerHTML(getElement(), html);
        }

        public String getText() {
            return html;
        }

        public void setText(String text) {
            setHTML(text);

        }
    }

}// class VMenuBar
