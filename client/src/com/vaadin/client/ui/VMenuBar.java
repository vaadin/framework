/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.WidgetUtil;
import com.vaadin.shared.ui.menubar.MenuBarConstants;

public class VMenuBar extends SimpleFocusablePanel implements
        CloseHandler<PopupPanel>, KeyPressHandler, KeyDownHandler,
        FocusHandler, SubPartAware {

    // The hierarchy of VMenuBar is a bit weird as VMenuBar is the Paintable,
    // used for the root menu but also used for the sub menus.

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "v-menubar";
    public static final String SUBMENU_CLASSNAME_PREFIX = "-submenu";

    /**
     * For server connections.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public String uidlId;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public final VMenuBar hostReference = this;

    /** For internal use only. May be removed or replaced in the future. */
    public CustomMenuItem moreItem = null;

    /** For internal use only. May be removed or replaced in the future. */
    public VMenuBar collapsedRootItems;

    /**
     * An empty command to be used when the item has no command associated
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public static final Command emptyCommand = null;

    /** Widget fields **/
    protected boolean subMenu;
    protected ArrayList<CustomMenuItem> items;
    protected Element containerElement;
    protected VOverlay popup;
    protected VMenuBar visibleChildMenu;
    protected boolean menuVisible = false;
    protected VMenuBar parentMenu;
    protected CustomMenuItem selected;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean enabled = true;

    private VLazyExecutor iconLoadedExecutioner = new VLazyExecutor(100,
            new ScheduledCommand() {

                @Override
                public void execute() {
                    iLayout(true);
                }
            });

    /** For internal use only. May be removed or replaced in the future. */
    public boolean openRootOnHover;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean htmlContentAllowed;

    public VMenuBar() {
        // Create an empty horizontal menubar
        this(false, null);

        // Navigation is only handled by the root bar
        addFocusHandler(this);

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

    public VMenuBar(boolean subMenu, VMenuBar parentMenu) {

        items = new ArrayList<CustomMenuItem>();
        popup = null;
        visibleChildMenu = null;
        this.subMenu = subMenu;

        containerElement = getElement();

        sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT
                | Event.ONLOAD);

        if (parentMenu == null) {
            // Root menu
            setStyleName(CLASSNAME);
        } else {
            // Child menus inherits style name
            setStyleName(parentMenu.getStyleName());
        }
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        updateStyleNames();
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        updateStyleNames();
    }

    protected void updateStyleNames() {
        String primaryStyleName = getParentMenu() != null ? getParentMenu()
                .getStylePrimaryName() : getStylePrimaryName();

        // Reset the style name for all the items
        for (CustomMenuItem item : items) {
            item.setStyleName(primaryStyleName + "-menuitem");
        }

        if (subMenu
                && !getStylePrimaryName().endsWith(SUBMENU_CLASSNAME_PREFIX)) {
            /*
             * Sub-menus should get the sub-menu prefix
             */
            super.setStylePrimaryName(primaryStyleName
                    + SUBMENU_CLASSNAME_PREFIX);
        }
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

    void updateSize() {
        // Take from setWidth
        if (!subMenu) {
            // Only needed for root level menu
            hideChildren();
            setSelected(null);
            menuVisible = false;
        }
    }

    /**
     * Build the HTML content for a menu item.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public String buildItemHTML(UIDL item) {
        // Construct html from the text and the optional icon
        StringBuffer itemHTML = new StringBuffer();
        if (item.hasAttribute("separator")) {
            itemHTML.append("<span>---</span>");
        } else {
            // Add submenu indicator
            if (item.getChildCount() > 0) {
                String bgStyle = "";
                itemHTML.append("<span class=\"" + getStylePrimaryName()
                        + "-submenu-indicator\"" + bgStyle + ">&#x25BA;</span>");
            }

            itemHTML.append("<span class=\"" + getStylePrimaryName()
                    + "-menuitem-caption\">");
            Icon icon = client.getIcon(item.getStringAttribute("icon"));
            if (icon != null) {
                itemHTML.append(icon.getElement().getString());
            }
            String itemText = item.getStringAttribute("text");
            if (!htmlContentAllowed) {
                itemText = WidgetUtil.escapeHTML(itemText);
            }
            itemHTML.append(itemText);
            itemHTML.append("</span>");
        }
        return itemHTML.toString();
    }

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
    @Override
    public com.google.gwt.user.client.Element getContainerElement() {
        return DOM.asOld(containerElement);
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
        CustomMenuItem item = GWT.create(CustomMenuItem.class);
        item.setHTML(html);
        item.setCommand(cmd);
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

            DOM.removeChild(getContainerElement(),
                    DOM.getChild(getContainerElement(), index));
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
            VMenuBar parent = getParentMenu();
            if (parent != null) {
                // The onload event for an image in a popup should be sent to
                // the parent, which owns the popup
                parent.iconLoaded();
            } else {
                // Onload events for images in the root menu are handled by the
                // root menu itself
                iconLoaded();
            }
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
                if (subMenu) {
                    // Prevent moving keyboard focus to child menus
                    VMenuBar parent = parentMenu;
                    while (parent.getParentMenu() != null) {
                        parent = parent.getParentMenu();
                    }
                    parent.setFocus(true);
                }

                break;

            case Event.ONMOUSEOVER:
                LazyCloser.cancelClosing();

                if (isEnabled() && targetItem.isEnabled()) {
                    itemOver(targetItem);
                }
                break;

            case Event.ONMOUSEOUT:
                itemOut(targetItem);
                LazyCloser.schedule();
                break;
            }
        } else if (subMenu && DOM.eventGetType(e) == Event.ONCLICK && subMenu) {
            // Prevent moving keyboard focus to child menus
            VMenuBar parent = parentMenu;
            while (parent.getParentMenu() != null) {
                parent = parent.getParentMenu();
            }
            parent.setFocus(true);
        }
    }

    private boolean isEnabled() {
        return enabled;
    }

    private void iconLoaded() {
        iconLoadedExecutioner.trigger();
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
            Scheduler.get().scheduleDeferred(item.getCommand());

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
        if ((openRootOnHover || subMenu || menuVisible) && !item.isSeparator()) {
            setSelected(item);
            if (!subMenu && openRootOnHover && !menuVisible) {
                menuVisible = true; // start opening menus
                LazyCloser.prepare(this);
            }
        }

        if (menuVisible && visibleChildMenu != item.getSubMenu()
                && popup != null) {
            // #15255 - disable animation-in/out when hide in this case
            popup.hide(false, false, false);
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
     * Used to autoclose submenus when they the menu is in a mode which opens
     * root menus on mouse hover.
     */
    private static class LazyCloser extends Timer {
        static LazyCloser INSTANCE;
        private VMenuBar activeRoot;

        @Override
        public void run() {
            activeRoot.hideChildren();
            activeRoot.setSelected(null);
            activeRoot.menuVisible = false;
            activeRoot = null;
        }

        public static void cancelClosing() {
            if (INSTANCE != null) {
                INSTANCE.cancel();
            }
        }

        public static void prepare(VMenuBar vMenuBar) {
            if (INSTANCE == null) {
                INSTANCE = new LazyCloser();
            }
            if (INSTANCE.activeRoot == vMenuBar) {
                INSTANCE.cancel();
            } else if (INSTANCE.activeRoot != null) {
                INSTANCE.cancel();
                INSTANCE.run();
            }
            INSTANCE.activeRoot = vMenuBar;
        }

        public static void schedule() {
            if (INSTANCE != null && INSTANCE.activeRoot != null) {
                INSTANCE.schedule(750);
            }
        }

    }

    /**
     * Shows the child menu of an item. The caller must ensure that the item has
     * a submenu.
     * 
     * @param item
     */
    public void showChildMenu(CustomMenuItem item) {

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
        showChildMenuAt(item, top, left);
    }

    protected void showChildMenuAt(CustomMenuItem item, int top, int left) {
        final int shadowSpace = 10;

        popup = new VOverlay(true, false, true);
        popup.setOwner(this);

        /*
         * Use parents primary style name if possible and remove the submenu
         * prefix if needed
         */
        String primaryStyleName = parentMenu != null ? parentMenu
                .getStylePrimaryName() : getStylePrimaryName();
        if (subMenu) {
            primaryStyleName = primaryStyleName.replace(
                    SUBMENU_CLASSNAME_PREFIX, "");
        }
        popup.setStyleName(primaryStyleName + "-popup");

        // Setting owner and handlers to support tooltips. Needed for tooltip
        // handling of overlay widgets (will direct queries to parent menu)
        if (parentMenu == null) {
            popup.setOwner(this);
        } else {
            VMenuBar parent = parentMenu;
            while (parent.getParentMenu() != null) {
                parent = parent.getParentMenu();
            }
            popup.setOwner(parent);
        }
        if (client != null) {
            client.getVTooltip().connectHandlersToWidget(popup);
        }

        popup.setWidget(item.getSubMenu());
        popup.addCloseHandler(this);
        popup.addAutoHidePartner(item.getElement());

        // at 0,0 because otherwise IE7 add extra scrollbars (#5547)
        popup.setPopupPosition(0, 0);

        item.getSubMenu().onShow();
        visibleChildMenu = item.getSubMenu();
        item.getSubMenu().setParentMenu(this);

        popup.show();

        if (left + popup.getOffsetWidth() >= RootPanel.getBodyElement()
                .getOffsetWidth() - shadowSpace) {
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
        }

        top = adjustPopupHeight(top, shadowSpace);

        popup.setPopupPosition(left, top);

    }

    private int adjustPopupHeight(int top, final int shadowSpace) {
        // Check that the popup will fit the screen
        int availableHeight = RootPanel.getBodyElement().getOffsetHeight()
                - top - shadowSpace;
        int missingHeight = popup.getOffsetHeight() - availableHeight;
        if (missingHeight > 0) {
            // First move the top of the popup to get more space
            // Don't move above top of screen, don't move more than needed
            int moveUpBy = Math.min(top - shadowSpace, missingHeight);

            // Update state
            top -= moveUpBy;
            missingHeight -= moveUpBy;
            availableHeight += moveUpBy;

            if (missingHeight > 0) {
                int contentWidth = visibleChildMenu.getOffsetWidth();

                // If there's still not enough room, limit height to fit and add
                // a scroll bar
                Style style = popup.getElement().getStyle();
                style.setHeight(availableHeight, Unit.PX);
                style.setOverflowY(Overflow.SCROLL);

                // Make room for the scroll bar by adjusting the width of the
                // popup
                style.setWidth(
                        contentWidth + WidgetUtil.getNativeScrollbarSize(),
                        Unit.PX);
                popup.positionOrSizeUpdated();
            }
        }
        return top;
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
    @Override
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
        hideChildren(true, true);
    }

    /**
     * 
     * Recursively hide all child menus
     * 
     * @param animateIn
     *            enable/disable animate-in animation when hide popup
     * @param animateOut
     *            enable/disable animate-out animation when hide popup
     * @since 7.3.7
     */
    public void hideChildren(boolean animateIn, boolean animateOut) {
        if (visibleChildMenu != null) {
            visibleChildMenu.hideChildren(animateIn, animateOut);
            popup.hide(false, animateIn, animateOut);
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
    public static class CustomMenuItem extends Widget implements HasHTML {

        protected String html = null;
        protected Command command = null;
        protected VMenuBar subMenu = null;
        protected VMenuBar parentMenu = null;
        protected boolean enabled = true;
        protected boolean isSeparator = false;
        protected boolean checkable = false;
        protected boolean checked = false;
        protected boolean selected = false;
        protected String description = null;

        private String styleName;

        /**
         * Default menu item {@link Widget} constructor for GWT.create().
         * 
         * Use {@link #setHTML(String)} and {@link #setCommand(Command)} after
         * constructing a menu item.
         */
        public CustomMenuItem() {
            this("", null);
        }

        /**
         * Creates a menu item {@link Widget}.
         * 
         * @param html
         * @param cmd
         * @deprecated use the default constructor and {@link #setHTML(String)}
         *             and {@link #setCommand(Command)} instead
         */
        @Deprecated
        public CustomMenuItem(String html, Command cmd) {
            // We need spans to allow inline-block in IE
            setElement(DOM.createSpan());

            setHTML(html);
            setCommand(cmd);
            setSelected(false);
        }

        @Override
        public void setStyleName(String style) {
            super.setStyleName(style);
            updateStyleNames();

            // Pass stylename down to submenus
            if (getSubMenu() != null) {
                getSubMenu().setStyleName(style);
            }
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            updateStyleNames();
        }

        public void setChecked(boolean checked) {
            if (checkable && !isSeparator) {
                this.checked = checked;
            } else {
                this.checked = false;
            }
            updateStyleNames();
        }

        public boolean isChecked() {
            return checked;
        }

        public void setCheckable(boolean checkable) {
            if (checkable && !isSeparator) {
                this.checkable = true;
            } else {
                setChecked(false);
                this.checkable = false;
            }
        }

        public boolean isCheckable() {
            return checkable;
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
            updateStyleNames();
        }

        protected void updateStyleNames() {
            if (parentMenu == null) {
                // Style names depend on the parent menu's primary style name so
                // don't do updates until the item has a parent
                return;
            }

            String primaryStyleName = parentMenu.getStylePrimaryName();
            if (parentMenu.subMenu) {
                primaryStyleName = primaryStyleName.replace(
                        SUBMENU_CLASSNAME_PREFIX, "");
            }

            String currentStyles = super.getStyleName();
            List<String> customStyles = new ArrayList<String>();
            for (String style : currentStyles.split(" ")) {
                if (!style.isEmpty() && !style.startsWith(primaryStyleName)) {
                    customStyles.add(style);
                }
            }

            if (isSeparator) {
                super.setStyleName(primaryStyleName + "-separator");
            } else {
                super.setStyleName(primaryStyleName + "-menuitem");
            }

            for (String customStyle : customStyles) {
                super.addStyleName(customStyle);
            }

            if (styleName != null) {
                addStyleDependentName(styleName);
            }

            if (enabled) {
                removeStyleDependentName("disabled");
            } else {
                addStyleDependentName("disabled");
            }

            if (selected && isSelectable()) {
                addStyleDependentName("selected");
                // needed for IE6 to have a single style name to match for an
                // element
                // TODO Can be optimized now that IE6 is not supported any more
                if (checkable) {
                    if (checked) {
                        removeStyleDependentName("selected-unchecked");
                        addStyleDependentName("selected-checked");
                    } else {
                        removeStyleDependentName("selected-checked");
                        addStyleDependentName("selected-unchecked");
                    }
                }
            } else {
                removeStyleDependentName("selected");
                // needed for IE6 to have a single style name to match for an
                // element
                removeStyleDependentName("selected-checked");
                removeStyleDependentName("selected-unchecked");
            }

            if (checkable && !isSeparator) {
                if (checked) {
                    addStyleDependentName("checked");
                    removeStyleDependentName("unchecked");
                } else {
                    addStyleDependentName("unchecked");
                    removeStyleDependentName("checked");
                }
            }
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

        @Override
        public String getHTML() {
            return html;
        }

        @Override
        public void setHTML(String html) {
            this.html = html;
            DOM.setInnerHTML(getElement(), html);

            // Sink the onload event for any icons. The onload
            // events are handled by the parent VMenuBar.
            WidgetUtil.sinkOnloadForImages(getElement());
        }

        @Override
        public String getText() {
            return html;
        }

        @Override
        public void setText(String text) {
            setHTML(WidgetUtil.escapeHTML(text));
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            updateStyleNames();
        }

        public boolean isEnabled() {
            return enabled;
        }

        private void setSeparator(boolean separator) {
            isSeparator = separator;
            updateStyleNames();
            if (!separator) {
                setEnabled(enabled);
            }
        }

        public boolean isSeparator() {
            return isSeparator;
        }

        public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
            setSeparator(uidl.hasAttribute("separator"));
            setEnabled(!uidl
                    .hasAttribute(MenuBarConstants.ATTRIBUTE_ITEM_DISABLED));

            if (!isSeparator()
                    && uidl.hasAttribute(MenuBarConstants.ATTRIBUTE_CHECKED)) {
                // if the selected attribute is present (either true or false),
                // the item is selectable
                setCheckable(true);
                setChecked(uidl
                        .getBooleanAttribute(MenuBarConstants.ATTRIBUTE_CHECKED));
            } else {
                setCheckable(false);
            }

            if (uidl.hasAttribute(MenuBarConstants.ATTRIBUTE_ITEM_STYLE)) {
                styleName = uidl
                        .getStringAttribute(MenuBarConstants.ATTRIBUTE_ITEM_STYLE);
            }

            if (uidl.hasAttribute(MenuBarConstants.ATTRIBUTE_ITEM_DESCRIPTION)) {
                description = uidl
                        .getStringAttribute(MenuBarConstants.ATTRIBUTE_ITEM_DESCRIPTION);
            }

            updateStyleNames();
        }

        public TooltipInfo getTooltip() {
            if (description == null) {
                return null;
            }

            return new TooltipInfo(description, null, this);
        }

        /**
         * Checks if the item can be selected.
         * 
         * @return true if it is possible to select this item, false otherwise
         */
        public boolean isSelectable() {
            return !isSeparator() && isEnabled();
        }

    }

    /**
     * @author Jouni Koivuviita / Vaadin Ltd.
     */
    public void iLayout() {
        iLayout(false);
        updateSize();
    }

    public void iLayout(boolean iconLoadEvent) {
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

            int availableWidth = LayoutManager.get(client).getInnerWidth(
                    getElement());

            // Used width includes the "more" item if present
            int usedWidth = getConsumedWidth();
            int diff = availableWidth - usedWidth;
            removeItem(moreItem);

            if (diff < 0) {
                // Too many items: collapse last items from root menu
                int widthNeeded = usedWidth - availableWidth;
                if (!morePresent) {
                    widthNeeded += moreItemWidth;
                }
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

                while (widthAvailable > widthGrowth
                        && collapsedRootItems.getItems().size() > 0) {
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
                        widthAvailable = diff + moreItemWidth;
                    }
                }
            }
            if (collapsedRootItems.getItems().size() > 0) {
                addItem(moreItem);
            }
        }

        // If a popup is open we might need to adjust the shadow as well if an
        // icon shown in that popup was loaded
        if (popup != null) {
            // Forces a recalculation of the shadow size
            popup.show();
        }
        if (iconLoadEvent) {
            // Size have changed if the width is undefined
            Util.notifyParentOfSizeChange(this, false);
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
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google
     * .gwt.event.dom.client.KeyPressEvent)
     */
    @Override
    public void onKeyPress(KeyPressEvent event) {
        // A bug fix for #14041
        // getKeyCode and getCharCode return different values for different
        // browsers
        int keyCode = event.getNativeEvent().getKeyCode();
        if (keyCode == 0) {
            keyCode = event.getNativeEvent().getCharCode();
        }
        if (handleNavigation(keyCode,
                event.isControlKeyDown() || event.isMetaKeyDown(),
                event.isShiftKeyDown())) {
            event.preventDefault();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt
     * .event.dom.client.KeyDownEvent)
     */
    @Override
    public void onKeyDown(KeyDownEvent event) {
        // A bug fix for #14041
        // getKeyCode and getCharCode return different values for different
        // browsers
        int keyCode = event.getNativeEvent().getKeyCode();
        if (keyCode == 0) {
            keyCode = event.getNativeEvent().getCharCode();
        }
        if (handleNavigation(keyCode,
                event.isControlKeyDown() || event.isMetaKeyDown(),
                event.isShiftKeyDown())) {
            event.preventDefault();
        }
    }

    /**
     * Get the key that moves the selection upwards. By default it is the up
     * arrow key but by overriding this you can change the key to whatever you
     * want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationUpKey() {
        return KeyCodes.KEY_UP;
    }

    /**
     * Get the key that moves the selection downwards. By default it is the down
     * arrow key but by overriding this you can change the key to whatever you
     * want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationDownKey() {
        return KeyCodes.KEY_DOWN;
    }

    /**
     * Get the key that moves the selection left. By default it is the left
     * arrow key but by overriding this you can change the key to whatever you
     * want.
     * 
     * @return The keycode of the key
     */
    protected int getNavigationLeftKey() {
        return KeyCodes.KEY_LEFT;
    }

    /**
     * Get the key that moves the selection right. By default it is the right
     * arrow key but by overriding this you can change the key to whatever you
     * want.
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
     * @deprecated use {@link #isNavigationSelectKey(int)} instead
     * @return
     */
    @Deprecated
    protected int getNavigationSelectKey() {
        return KeyCodes.KEY_ENTER;
    }

    /**
     * Checks whether key code selects a menu item. By default it is the Enter
     * and Space keys but by overriding this you can change the keys to whatever
     * you want.
     * 
     * @since 7.2
     * @param keycode
     * @return true if key selects menu item
     */
    protected boolean isNavigationSelectKey(int keycode) {
        return keycode == getNavigationSelectKey()
                || keycode == KeyCodes.KEY_SPACE;
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

        // If tab or shift+tab close menus
        if (keycode == KeyCodes.KEY_TAB) {
            setSelected(null);
            hideChildren();
            menuVisible = false;
            return false;
        }

        if (ctrl || shift || !isEnabled()) {
            // Do not handle tab key, nor ctrl keys
            return false;
        }

        if (keycode == getNavigationLeftKey()) {
            if (getSelected() == null) {
                // If nothing is selected then select the last item
                setSelected(items.get(items.size() - 1));
                if (!getSelected().isSelectable()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            } else if (visibleChildMenu == null && getParentMenu() == null) {
                // If this is the root menu then move to the left
                int idx = items.indexOf(getSelected());
                if (idx > 0) {
                    setSelected(items.get(idx - 1));
                } else {
                    setSelected(items.get(items.size() - 1));
                }

                if (!getSelected().isSelectable()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            } else if (visibleChildMenu != null) {
                // Redirect all navigation to the submenu
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);

            } else if (getParentMenu().getParentMenu() == null) {
                // Inside a sub menu, whose parent is a root menu item
                VMenuBar root = getParentMenu();

                root.getSelected().getSubMenu().setSelected(null);
                // #15255 - disable animate-in/out when hide popup
                root.hideChildren(false, false);

                // Get the root menus items and select the previous one
                int idx = root.getItems().indexOf(root.getSelected());
                idx = idx > 0 ? idx : root.getItems().size();
                CustomMenuItem selected = root.getItems().get(--idx);

                while (selected.isSeparator() || !selected.isEnabled()) {
                    idx = idx > 0 ? idx : root.getItems().size();
                    selected = root.getItems().get(--idx);
                }

                root.setSelected(selected);
                openMenuAndFocusFirstIfPossible(selected);
            } else {
                getParentMenu().getSelected().getSubMenu().setSelected(null);
                getParentMenu().hideChildren();
            }

            return true;

        } else if (keycode == getNavigationRightKey()) {

            if (getSelected() == null) {
                // If nothing is selected then select the first item
                setSelected(items.get(0));
                if (!getSelected().isSelectable()) {
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

                if (!getSelected().isSelectable()) {
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
                while (root.getParentMenu() != null) {
                    root = root.getParentMenu();
                }

                // Hide the submenu (#15255 - disable animate-in/out when hide
                // popup)
                root.hideChildren(false, false);

                // Get the root menus items and select the next one
                int idx = root.getItems().indexOf(root.getSelected());
                idx = idx < root.getItems().size() - 1 ? idx : -1;
                CustomMenuItem selected = root.getItems().get(++idx);

                while (selected.isSeparator() || !selected.isEnabled()) {
                    idx = idx < root.getItems().size() - 1 ? idx : -1;
                    selected = root.getItems().get(++idx);
                }

                root.setSelected(selected);
                openMenuAndFocusFirstIfPossible(selected);
            } else if (visibleChildMenu != null) {
                // Redirect all navigation to the submenu
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);
            }

            return true;

        } else if (keycode == getNavigationUpKey()) {

            if (getSelected() == null) {
                // If nothing is selected then select the last item
                setSelected(items.get(items.size() - 1));
                if (!getSelected().isSelectable()) {
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

                if (!getSelected().isSelectable()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            }

            return true;

        } else if (keycode == getNavigationDownKey()) {

            if (getSelected() == null) {
                // If nothing is selected then select the first item
                selectFirstItem();
            } else if (visibleChildMenu == null && getParentMenu() == null) {
                // If this is the root menu the show the child menu with arrow
                // down, if there is a child menu
                openMenuAndFocusFirstIfPossible(getSelected());
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

                if (!getSelected().isSelectable()) {
                    handleNavigation(keycode, ctrl, shift);
                }
            }
            return true;

        } else if (keycode == getCloseMenuKey()) {
            setSelected(null);
            hideChildren();
            menuVisible = false;

        } else if (isNavigationSelectKey(keycode)) {
            if (getSelected() == null) {
                // If nothing is selected then select the first item
                selectFirstItem();
            } else if (visibleChildMenu != null) {
                // Redirect all navigation to the submenu
                visibleChildMenu.handleNavigation(keycode, ctrl, shift);
                menuVisible = false;
            } else if (visibleChildMenu == null
                    && getSelected().getSubMenu() != null) {
                // If the item has a sub menu then show it and move the
                // selection there
                openMenuAndFocusFirstIfPossible(getSelected());
            } else {
                final Command command = getSelected().getCommand();

                setSelected(null);
                hideParents(true);

                // #17076 keyboard selected menuitem without children: do
                // not leave menu to visible ("hover open") mode
                menuVisible = false;

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (command != null) {
                            command.execute();
                        }
                    }
                });
            }
        }

        return false;
    }

    private void selectFirstItem() {
        for (int i = 0; i < items.size(); i++) {
            CustomMenuItem item = items.get(i);
            if (item.isSelectable()) {
                setSelected(item);
                break;
            }
        }
    }

    private void openMenuAndFocusFirstIfPossible(CustomMenuItem menuItem) {
        VMenuBar subMenu = menuItem.getSubMenu();
        if (subMenu == null) {
            // No child menu? Nothing to do
            return;
        }

        VMenuBar parentMenu = menuItem.getParentMenu();
        parentMenu.showChildMenu(menuItem);

        menuVisible = true;
        // Select the first item in the newly open submenu
        subMenu.selectFirstItem();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event
     * .dom.client.FocusEvent)
     */
    @Override
    public void onFocus(FocusEvent event) {

    }

    private final String SUBPART_PREFIX = "item";

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        if (subPart.startsWith(SUBPART_PREFIX)) {
            int index = Integer.parseInt(subPart.substring(SUBPART_PREFIX
                    .length()));
            CustomMenuItem item = getItems().get(index);

            return item.getElement();
        } else {
            Queue<CustomMenuItem> submenuItems = new LinkedList<CustomMenuItem>();
            for (CustomMenuItem item : getItems()) {
                if (isItemNamed(item, subPart)) {
                    return item.getElement();
                }
                if (item.getSubMenu() != null) {
                    submenuItems.addAll(item.getSubMenu().getItems());
                }
            }
            while (!submenuItems.isEmpty()) {
                CustomMenuItem item = submenuItems.poll();
                if (!item.isSeparator() && isItemNamed(item, subPart)) {
                    return item.getElement();
                }
                if (item.getSubMenu() != null && item.getSubMenu().menuVisible) {
                    submenuItems.addAll(item.getSubMenu().getItems());
                }

            }
            return null;
        }
    }

    private boolean isItemNamed(CustomMenuItem item, String name) {
        Element lastChildElement = getLastChildElement(item);
        if (getText(lastChildElement).equals(name)) {
            return true;
        }
        return false;
    }

    /*
     * Returns the text content of element without including the text of
     * possible nested elements. It is assumed that the last child of element
     * contains the text of interest and that the last child does not itself
     * have children with text content. This method is used by
     * getSubPartElement(String) so that possible text icons are not included in
     * the textual matching (#14879).
     */
    private native String getText(Element element)
    /*-{
        var n = element.childNodes.length;
        if(n > 0){
            return element.childNodes[n - 1].nodeValue;
        }
        else{
            return "";
        }
    }-*/;

    private Element getLastChildElement(CustomMenuItem item) {
        Element lastChildElement = item.getElement().getFirstChildElement();
        while (lastChildElement.getNextSiblingElement() != null) {
            lastChildElement = lastChildElement.getNextSiblingElement();
        }
        return lastChildElement;
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        if (!getElement().isOrHasChild(subElement)) {
            return null;
        }

        Element menuItemRoot = subElement;
        while (menuItemRoot != null && menuItemRoot.getParentElement() != null
                && menuItemRoot.getParentElement() != getElement()) {
            menuItemRoot = menuItemRoot.getParentElement().cast();
        }
        // "menuItemRoot" is now the root of the menu item

        final int itemCount = getItems().size();
        for (int i = 0; i < itemCount; i++) {
            if (getItems().get(i).getElement() == menuItemRoot) {
                String name = SUBPART_PREFIX + i;
                return name;
            }
        }
        return null;
    }

    /**
     * Get menu item with given DOM element
     * 
     * @param element
     *            Element used in search
     * @return Menu item or null if not found
     * @deprecated As of 7.2, call or override
     *             {@link #getMenuItemWithElement(Element)} instead
     */
    @Deprecated
    public CustomMenuItem getMenuItemWithElement(
            com.google.gwt.user.client.Element element) {
        for (int i = 0; i < items.size(); i++) {
            CustomMenuItem item = items.get(i);
            if (DOM.isOrHasChild(item.getElement(), element)) {
                return item;
            }

            if (item.getSubMenu() != null) {
                item = item.getSubMenu().getMenuItemWithElement(element);
                if (item != null) {
                    return item;
                }
            }
        }

        return null;
    }

    /**
     * Get menu item with given DOM element
     * 
     * @param element
     *            Element used in search
     * @return Menu item or null if not found
     * 
     * @since 7.2
     */
    public CustomMenuItem getMenuItemWithElement(Element element) {
        return getMenuItemWithElement(DOM.asOld(element));
    }
}
