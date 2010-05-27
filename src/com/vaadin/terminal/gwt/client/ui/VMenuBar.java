/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ContainerResizedListener;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;

public class VMenuBar extends SimpleFocusablePanel implements Paintable,
        CloseHandler<PopupPanel>, ContainerResizedListener, KeyPressHandler,
        KeyDownHandler, BlurHandler, FocusHandler {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "v-menubar";

    /** For server connections **/
    protected String uidlId;
    protected ApplicationConnection client;

    protected final VMenuBar hostReference = this;
    protected String submenuIcon = null;
    protected CustomMenuItem moreItem = null;
    protected VMenuBar collapsedRootItems;

    // Construct an empty command to be used when the item has no command
    // associated
    protected static final Command emptyCommand = null;

    /** Widget fields **/
    protected boolean subMenu;
    protected ArrayList<CustomMenuItem> items;
    protected Element containerElement;
    protected VOverlay popup;
    protected VMenuBar visibleChildMenu;
    protected boolean menuVisible = false;
    protected VMenuBar parentMenu;
    protected CustomMenuItem selected;

    private Timer layoutTimer;

    private boolean enabled = true;

    public VMenuBar() {
        // Create an empty horizontal menubar
        this(false);

        // Navigation is only handled by the root bar
        addFocusHandler(this);
        addBlurHandler(this);

        /*
         * Firefox auto-repeat works correctly only if we use a key press
         * handler, other browsers handle it correctly when using a key down
         * handler
         */
        if (BrowserInfo.get().isGecko()) {
            addKeyPressHandler(this);
        } else {
            addKeyDownHandler(this);
        }
    }

    public VMenuBar(boolean subMenu) {
        super();

        items = new ArrayList<CustomMenuItem>();
        popup = null;
        visibleChildMenu = null;

        containerElement = getElement();

        if (!subMenu) {
            setStylePrimaryName(CLASSNAME);
        } else {
            setStylePrimaryName(CLASSNAME + "-submenu");
        }
        this.subMenu = subMenu;

        sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
                | Event.ONLOAD);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if (!subMenu) {
            setSelected(null);
            hideChildren();
            menuVisible = false;
        }
    }

    @Override
    public void setWidth(String width) {
        Util.setWidthExcludingPaddingAndBorder(this, width, 0);
        if (!subMenu) {
            // Only needed for root level menu
            hideChildren();
            setSelected(null);
            menuVisible = false;
        }
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
        enabled = !uidl.getBooleanAttribute("disabled");

        // For future connections
        this.client = client;
        uidlId = uidl.getId();

        // Empty the menu every time it receives new information
        if (!getItems().isEmpty()) {
            clearItems();
        }

        UIDL options = uidl.getChildUIDL(0);

        // FIXME remove in version 7
        if (options.hasAttribute("submenuIcon")) {
            submenuIcon = client.translateVaadinUri(uidl.getChildUIDL(0)
                    .getStringAttribute("submenuIcon"));
        } else {
            submenuIcon = null;
        }

        if (uidl.hasAttribute("width")) {
            UIDL moreItemUIDL = options.getChildUIDL(0);
            StringBuffer itemHTML = new StringBuffer();

            if (moreItemUIDL.hasAttribute("icon")) {
                itemHTML.append("<img src=\""
                        + client.translateVaadinUri(moreItemUIDL
                                .getStringAttribute("icon")) + "\" class=\""
                        + Icon.CLASSNAME + "\" alt=\"\" />");
            }

            String moreItemText = moreItemUIDL.getStringAttribute("text");
            if ("".equals(moreItemText)) {
                moreItemText = "&#x25BA;";
            }
            itemHTML.append(moreItemText);

            moreItem = new CustomMenuItem(itemHTML.toString(), emptyCommand);
            collapsedRootItems = new VMenuBar(true);
            moreItem.setSubMenu(collapsedRootItems);
            moreItem.addStyleName(CLASSNAME + "-more-menuitem");
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

            boolean itemHasCommand = item.hasAttribute("command");

            // Construct html from the text and the optional icon
            StringBuffer itemHTML = new StringBuffer();
            Command cmd = null;

            if (item.hasAttribute("separator")) {
                itemHTML.append("<span>---</span>");
            } else {
                // Add submenu indicator
                if (item.getChildCount() > 0) {
                    // FIXME For compatibility reasons: remove in version 7
                    String bgStyle = "";
                    if (submenuIcon != null) {
                        bgStyle = " style=\"background-image: url("
                                + submenuIcon
                                + "); text-indent: -999px; width: 1em;\"";
                    }
                    itemHTML.append("<span class=\"" + CLASSNAME
                            + "-submenu-indicator\"" + bgStyle
                            + ">&#x25BA;</span>");
                }

                itemHTML.append("<span class=\"" + CLASSNAME
                        + "-menuitem-caption\">");
                if (item.hasAttribute("icon")) {
                    itemHTML
                            .append("<img src=\""
                                    + client.translateVaadinUri(item
                                            .getStringAttribute("icon"))
                                    + "\" class=\"" + Icon.CLASSNAME
                                    + "\" alt=\"\" />");
                }
                itemHTML.append(Util.escapeHTML(itemText) + "</span>");

                if (itemHasCommand) {
                    // Construct a command that fires onMenuClick(int) with the
                    // item's id-number
                    cmd = new Command() {
                        public void execute() {
                            hostReference.onMenuClick(itemId);
                        }
                    };
                }
            }

            currentItem = currentMenu.addItem(itemHTML.toString(), cmd);
            currentItem.setSeparator(item.hasAttribute("separator"));
            currentItem.setEnabled(!item.hasAttribute("disabled"));
            if (item.hasAttribute("style")) {
                String itemStyle = item.getStringAttribute("style");
                currentItem.addStyleDependentName(itemStyle);
            }

            if (item.getChildCount() > 0) {
                menuStack.push(currentMenu);
                iteratorStack.push(itr);
                itr = item.getChildIterator();
                currentMenu = new VMenuBar(true);
                if (uidl.hasAttribute("style")) {
                    for (String style : uidl.getStringAttribute("style").split(
                            " ")) {
                        currentMenu.addStyleDependentName(style);
                    }
                }
                currentItem.setSubMenu(currentMenu);
            }

            while (!itr.hasNext() && !iteratorStack.empty()) {
                itr = iteratorStack.pop();
                currentMenu = menuStack.pop();
            }
        }// while

        iLayout();

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
        Element e = getContainerElement();
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
    public Element getContainerElement() {
        return containerElement;
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
        if (items.contains(item)) {
            return;
        }
        DOM.appendChild(getContainerElement(), item.getElement());
        item.setParentMenu(this);
        item.setSelected(false);
        items.add(item);
    }

    public void addItem(CustomMenuItem item, int index) {
        if (items.contains(item)) {
            return;
        }
        DOM.insertChild(getContainerElement(), item.getElement(), index);
        item.setParentMenu(this);
        item.setSelected(false);
        items.add(index, item);
    }

    /**
     * Remove the given item from this menu
     * 
     * @param item
     */
    public void removeItem(CustomMenuItem item) {
        if (items.contains(item)) {
            int index = items.indexOf(item);

            DOM.removeChild(getContainerElement(), DOM.getChild(
                    getContainerElement(), index));
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

        // Handle onload events (icon loaded, size changes)
        if (DOM.eventGetType(e) == Event.ONLOAD) {
            requestLayout();
            return;
        }

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
                if (isEnabled() && targetItem.isEnabled()) {
                    itemClick(targetItem);
                }
                break;

            case Event.ONMOUSEOVER:
                if (isEnabled() && targetItem.isEnabled()) {
                    itemOver(targetItem);
                }
                break;

            case Event.ONMOUSEOUT:
                itemOut(targetItem);
                break;
            }
        }
    }

    private boolean isEnabled() {
        return enabled;
    }

    private void requestLayout() {
        if (layoutTimer == null) {
            layoutTimer = new Timer() {
                @Override
                public void run() {
                    layoutTimer = null;
                    iLayout();
                }
            };
        }
        layoutTimer.schedule(100);
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

            hideParents(true);
            menuVisible = false;
            DeferredCommand.addCommand(item.getCommand());

        } else {
            if (item.getSubMenu() != null
                    && item.getSubMenu() != visibleChildMenu) {
                setSelected(item);
                showChildMenu(item);
                menuVisible = true;
            } else if (!subMenu) {
                setSelected(null);
                hideChildren();
                menuVisible = false;
            }
        }
    }

    /**
     * When the user hovers the mouse over the item
     * 
     * @param item
     */
    public void itemOver(CustomMenuItem item) {
        if ((subMenu || menuVisible) && !item.isSeparator()) {
            setSelected(item);
        }

        if (menuVisible && visibleChildMenu != item.getSubMenu()
                && popup != null) {
            popup.hide();
        }

        if (menuVisible && item.getSubMenu() != null
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
        if (visibleChildMenu != item.getSubMenu()) {
            hideChildMenu(item);
            setSelected(null);
        } else if (visibleChildMenu == null) {
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
        final int shadowSpace = 10;

        popup = new VOverlay(true, false, true);
        popup.setStylePrimaryName(CLASSNAME + "-popup");
        popup.setWidget(item.getSubMenu());
        popup.addCloseHandler(this);
        popup.addAutoHidePartner(item.getElement());

        int left = 0;
        int top = 0;
        if (subMenu) {
            left = item.getParentMenu().getAbsoluteLeft()
                    + item.getParentMenu().getOffsetWidth();
            top = item.getAbsoluteTop();
        } else {
            left = item.getAbsoluteLeft();
            top = item.getParentMenu().getAbsoluteTop()
                    + item.getParentMenu().getOffsetHeight();
        }
        popup.setPopupPosition(left, top);

        item.getSubMenu().onShow();
        visibleChildMenu = item.getSubMenu();
        item.getSubMenu().setParentMenu(this);

        popup.show();

        if (left + popup.getOffsetWidth() >= RootPanel.getBodyElement()
                .getOffsetWidth()
                - shadowSpace) {
            if (subMenu) {
                left = item.getParentMenu().getAbsoluteLeft()
                        - popup.getOffsetWidth() - shadowSpace;
            } else {
                left = RootPanel.getBodyElement().getOffsetWidth()
                        - popup.getOffsetWidth() - shadowSpace;
            }
            // Accommodate space for shadow
            if (left < shadowSpace) {
                left = shadowSpace;
            }
            popup.setPopupPosition(left, top);
        }

        // IE7 really tests one's patience sometimes
        // Part of a fix to correct #3850
        if (BrowserInfo.get().isIE7()) {
            popup.getElement().getStyle().setProperty("zoom", "");
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    if (popup == null) {
                        // The child menu can be hidden before this command is
                        // run.
                        return;
                    }

                    if (popup.getElement().getStyle().getProperty("width") == null
                            || popup.getElement().getStyle().getProperty(
                                    "width") == "") {
                        popup.setWidth(popup.getOffsetWidth() + "px");
                    }
                    popup.getElement().getStyle().setProperty("zoom", "1");
                }
            });
        }
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
        // remove possible previous selection
        if (selected != null) {
            selected.setSelected(false);
            selected = null;
        }
        menuVisible = true;
    }

    /**
     * Listener method, fired when this menu is closed
     */
    public void onClose(CloseEvent<PopupPanel> event) {
        hideChildren();
        if (event.isAutoClosed()) {
            hideParents(true);
            menuVisible = false;
        }
        visibleChildMenu = null;
        popup = null;
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
    public void hideParents(boolean autoClosed) {
        if (visibleChildMenu != null) {
            popup.hide();
            setSelected(null);
            menuVisible = !autoClosed;
        }

        if (getParentMenu() != null) {
            getParentMenu().hideParents(autoClosed);
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
     * 
     * A class to hold information on menu items
     * 
     */
    private class CustomMenuItem extends UIObject implements HasHTML {

        protected String html = null;
        protected Command command = null;
        protected VMenuBar subMenu = null;
        protected VMenuBar parentMenu = null;
        protected boolean enabled = true;
        protected boolean isSeparator = false;

        public CustomMenuItem(String html, Command cmd) {
            // We need spans to allow inline-block in IE
            setElement(DOM.createSpan());

            setHTML(html);
            setCommand(cmd);
            setSelected(false);

            setStylePrimaryName(CLASSNAME + "-menuitem");

        }

        public void setSelected(boolean selected) {
            if (selected && !isSeparator) {
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
            if (BrowserInfo.get().isIE6() && client != null) {
                // Find possible icon element
                final NodeList imgs = getElement().getElementsByTagName("IMG");
                if (imgs.getLength() > 0) {
                    client.addPngFix((Element) imgs.getItem(0).cast());
                }
            }
        }

        public String getText() {
            return html;
        }

        public void setText(String text) {
            setHTML(Util.escapeHTML(text));
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (enabled) {
                removeStyleDependentName("disabled");
            } else {
                addStyleDependentName("disabled");
            }
        }

        public boolean isEnabled() {
            return enabled;
        }

        private void setSeparator(boolean separator) {
            isSeparator = separator;
            if (separator) {
                setStyleName(CLASSNAME + "-separator");
            } else {
                setStyleName(CLASSNAME + "-menuitem");
                setEnabled(enabled);
            }
        }

        public boolean isSeparator() {
            return isSeparator;
        }
    }

    /**
     * @author Jouni Koivuviita / IT Mill Ltd.
     */
    private int paddingWidth = -1;

    public void iLayout() {
        // Only collapse if there is more than one item in the root menu and the
        // menu has an explicit size
        if ((getItems().size() > 1 || (collapsedRootItems != null && collapsedRootItems
                .getItems().size() > 0))
                && getElement().getStyle().getProperty("width") != null
                && moreItem != null) {

            // Measure the width of the "more" item
            final boolean morePresent = getItems().contains(moreItem);
            addItem(moreItem);
            final int moreItemWidth = moreItem.getOffsetWidth();
            if (!morePresent) {
                removeItem(moreItem);
            }

            // Measure available space
            if (paddingWidth == -1) {
                int widthBefore = getElement().getClientWidth();
                getElement().getStyle().setProperty("padding", "0");
                paddingWidth = widthBefore - getElement().getClientWidth();
                getElement().getStyle().setProperty("padding", "");
            }
            String overflow = "";
            if (BrowserInfo.get().isIE6()) {
                // IE6 cannot measure available width correctly without
                // overflow:hidden
                overflow = getElement().getStyle().getProperty("overflow");
                getElement().getStyle().setProperty("overflow", "hidden");
            }

            int availableWidth = getElement().getClientWidth() - paddingWidth;

            if (BrowserInfo.get().isIE6()) {
                // IE6 cannot measure available width correctly without
                // overflow:hidden
                getElement().getStyle().setProperty("overflow", overflow);
            }
            int diff = availableWidth - getConsumedWidth();

            removeItem(moreItem);

            if (diff < 0) {
                // Too many items: collapse last items from root menu
                final int widthNeeded = moreItemWidth - diff;
                int widthReduced = 0;

                while (widthReduced < widthNeeded && getItems().size() > 0) {
                    // Move last root menu item to collapsed menu
                    CustomMenuItem collapse = getItems().get(
                            getItems().size() - 1);
                    widthReduced += collapse.getOffsetWidth();
                    removeItem(collapse);
                    collapsedRootItems.addItem(collapse, 0);
                }
            } else if (collapsedRootItems.getItems().size() > 0) {
                // Space available for items: expand first items from collapsed
                // menu
                int widthAvailable = diff + moreItemWidth;
                int widthGrowth = 0;

                while (widthAvailable > widthGrowth) {
                    // Move first item from collapsed menu to the root menu
                    CustomMenuItem expand = collapsedRootItems.getItems()
                            .get(0);
                    collapsedRootItems.removeItem(expand);
                    addItem(expand);
                    widthGrowth += expand.getOffsetWidth();
                    if (collapsedRootItems.getItems().size() > 0) {
                        widthAvailable -= moreItemWidth;
                    }
                    if (widthGrowth > widthAvailable) {
                        removeItem(expand);
                        collapsedRootItems.addItem(expand, 0);
                    } else {
                        widthAvailable = diff;
                    }
                }
            }
            if (collapsedRootItems.getItems().size() > 0) {
                addItem(moreItem);
            }
        }
    }

    private int getConsumedWidth() {
        int w = 0;
        for (CustomMenuItem item : getItems()) {
            if (!collapsedRootItems.getItems().contains(item)) {
                w += item.getOffsetWidth();
            }
        }
        return w;
    }

    /*
     * (non-Javadoc)
     * @see com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google.gwt.event.dom.client.KeyPressEvent)
     */
    public void onKeyPress(KeyPressEvent event) {
        if (handleNavigation(event.getNativeEvent().getKeyCode(), event
                .isControlKeyDown()
                || event.isMetaKeyDown(), event.isShiftKeyDown())) {
            event.preventDefault();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt.event.dom.client.KeyDownEvent)
     */
    public void onKeyDown(KeyDownEvent event) {
        if (handleNavigation(event.getNativeEvent().getKeyCode(), event
                .isControlKeyDown()
                || event.isMetaKeyDown(), event.isShiftKeyDown())) {
            event.preventDefault();
        }
    }
    
    /**
     * Get the key that moves the selection upwards. By default it is the
     * up arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationUpKey() {
        return KeyCodes.KEY_UP;
    }

    /**
     * Get the key that moves the selection downwards. By default it is the
     * down arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationDownKey() {
        return KeyCodes.KEY_DOWN;
    }

    /**
     * Get the key that moves the selection left. By default it is the
     * left arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationLeftKey() {
        return KeyCodes.KEY_LEFT;
    }

    /**
     * Get the key that moves the selection right. By default it is the
     * right arrow key but by overriding this you can change the key to whatever
     * you want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationRightKey() {
        return KeyCodes.KEY_RIGHT;
    }

    /**
     * Get the key that selects a menu item. By default it is the Enter key but
     * by overriding this you can change the key to whatever you want.
     * 
     * @return
     */
    protected int getNavigationSelectKey() {
        return KeyCodes.KEY_ENTER;
    }

    /**
     * Get the key that closes the menu. By default it is the escape key but by
     * overriding this yoy can change the key to whatever you want.
     * 
     * @return
     */
    protected int getCloseMenuKey() {
        return KeyCodes.KEY_ESCAPE;
    }

    /**
     * Handles the keyboard events handled by the MenuBar
     * 
     * @param event
     *            The keyboard event received
     * @return true iff the navigation event was handled
     */
    public boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {
        if (keycode == KeyCodes.KEY_TAB || ctrl || shift || !isEnabled()) {
            // Do not handle tab key, nor ctrl keys
            return false;
        }

        if (keycode == getNavigationLeftKey()) {
            if (getSelected() == null) {
                // If nothing is selected then select the last item
                setSelected(items.get(items.size() - 1));
                if (getSelected().isSeparator() || !getSelected().isEnabled()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            } else if (visibleChildMenu == null && getParentMenu() == null) {
                // If this is the root menu then move to the right
                int idx = items.indexOf(getSelected());
                if (idx > 0) {
                    setSelected(items.get(idx - 1));
                } else {
                    setSelected(items.get(items.size() - 1));
                }

                if (getSelected().isSeparator() || !getSelected().isEnabled()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            } else if (visibleChildMenu != null) {
                // Redirect all navigation to the submenu
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);

            } else if (getParentMenu().getParentMenu() == null) {

                // Get the root menu
                VMenuBar root = getParentMenu();

                root.getSelected().getSubMenu().setSelected(null);
                root.hideChildren();

                // Get the root menus items and select the previous one
                int idx = root.getItems().indexOf(root.getSelected());
                idx = idx > 0 ? idx : root.getItems().size();
                CustomMenuItem selected = root.getItems().get(--idx);

                while (selected.isSeparator() || !selected.isEnabled()) {
                    idx = idx > 0 ? idx : root.getItems().size();
                    selected = root.getItems().get(--idx);
                }

                root.setSelected(selected);
                root.showChildMenu(selected);
                VMenuBar submenu = selected.getSubMenu();

                // Select the first item in the newly open submenu
                submenu.setSelected(submenu.getItems().get(0));

            } else {
                getParentMenu().getSelected().getSubMenu().setSelected(null);
                getParentMenu().hideChildren();
            }
            
            return true;

        } else if (keycode == getNavigationRightKey()) {
            
            if (getSelected() == null) {
                // If nothing is selected then select the first item
                setSelected(items.get(0));
                if (getSelected().isSeparator() || !getSelected().isEnabled()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            } else if (visibleChildMenu == null && getParentMenu() == null) {
                // If this is the root menu then move to the right
                int idx = items.indexOf(getSelected());

                if (idx < items.size() - 1) {
                    setSelected(items.get(idx + 1));
                } else {
                    setSelected(items.get(0));
                }

                if (getSelected().isSeparator() || !getSelected().isEnabled()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            } else if (visibleChildMenu == null
                    && getSelected().getSubMenu() != null) {
                // If the item has a submenu then show it and move the selection
                // there
                showChildMenu(getSelected());
                menuVisible = true;
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);
            } else if (visibleChildMenu == null) {

                // Get the root menu
                VMenuBar root = getParentMenu();
                while(root.getParentMenu() != null){
                    root = root.getParentMenu();
                }

                // Hide the submenu
                root.hideChildren();

                // Get the root menus items and select the next one
                int idx = root.getItems().indexOf(root.getSelected());
                idx = idx < root.getItems().size()-1 ? idx : -1;
                CustomMenuItem selected = root.getItems().get(++idx);
                
                while (selected.isSeparator() || !selected.isEnabled()) {
                    idx = idx < root.getItems().size() - 1 ? idx : -1;
                    selected = root.getItems().get(++idx);
                }

                root.setSelected(selected);
                root.showChildMenu(selected);
                VMenuBar submenu = selected.getSubMenu();

                // Select the first item in the newly open submenu
                submenu.setSelected(submenu.getItems().get(0));               

            } else if (visibleChildMenu != null) {
                // Redirect all navigation to the submenu
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);
            }

            return true;

        } else if (keycode == getNavigationUpKey()) {

            if (getSelected() == null) {
                // If nothing is selected then select the last item
                setSelected(items.get(items.size() - 1));
                if (getSelected().isSeparator() || !getSelected().isEnabled()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            } else if (visibleChildMenu != null) {
                // Redirect all navigation to the submenu
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);
            } else {
                // Select the previous item if possible or loop to the last item
                int idx = items.indexOf(getSelected());
                if (idx > 0) {
                    setSelected(items.get(idx - 1));
                } else {
                    setSelected(items.get(items.size() - 1));
                }

                if (getSelected().isSeparator() || !getSelected().isEnabled()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            } 

            return true;

        } else if (keycode == getNavigationDownKey()) {

            if (getSelected() == null) {
                // If nothing is selected then select the first item
                setSelected(items.get(0));
                if (getSelected().isSeparator() || !getSelected().isEnabled()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            } else if (visibleChildMenu == null && getParentMenu() == null) {
                // If this is the root menu the show the child menu with arrow
                // down
                showChildMenu(getSelected());
                menuVisible = true;
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);
            } else if (visibleChildMenu != null) {
                // Redirect all navigation to the submenu
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);
            } else {
                // Select the next item if possible or loop to the first item
                int idx = items.indexOf(getSelected());
                if (idx < items.size() - 1) {
                    setSelected(items.get(idx + 1));
                } else {
                    setSelected(items.get(0));
                }

                if (getSelected().isSeparator() || !getSelected().isEnabled()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            }
            return true;

        } else if (keycode == getCloseMenuKey()) {
            setSelected(null);
            hideChildren();
            menuVisible = false;

        } else if (keycode == getNavigationSelectKey()) {
            if (visibleChildMenu != null) {
                // Redirect all navigation to the submenu
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);
                menuVisible = false;
            } else if (visibleChildMenu == null
                    && getSelected().getSubMenu() != null) {
                // If the item has a submenu then show it and move the selection
                // there
                showChildMenu(getSelected());
                menuVisible = true;
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);
            } else {
                Command command = getSelected().getCommand();
                if (command != null) {
                    command.execute();
                }

                hideParents(true);
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.BlurHandler#onBlur(com.google.gwt.event
     * .dom.client.BlurEvent)
     */
    public void onBlur(BlurEvent event) {
        setSelected(null);
        hideChildren();
        menuVisible = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event
     * .dom.client.FocusEvent)
     */
    public void onFocus(FocusEvent event) {
        if (getSelected() == null) {
            // If nothing is selected then select the first item
            setSelected(items.get(0));
        }
    }
}
