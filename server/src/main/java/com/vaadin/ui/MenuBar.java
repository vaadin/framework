/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.ui;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.EventTrigger;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.menubar.MenuBarConstants;
import com.vaadin.shared.ui.menubar.MenuBarState;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * <p>
 * A class representing a horizontal menu bar. The menu can contain MenuItem
 * objects, which in turn can contain more MenuBars. These sub-level MenuBars
 * are represented as vertical menu.
 * </p>
 */
@SuppressWarnings("serial")
public class MenuBar extends AbstractComponent
        implements LegacyComponent, Focusable {

    // Items of the top-level menu
    private final List<MenuItem> menuItems;

    // Number of items in this menu
    private int numberOfItems = 0;

    private MenuItem moreItem;

    private boolean openRootOnHover;

    private boolean htmlContentAllowed;

    @Override
    protected MenuBarState getState() {
        return (MenuBarState) super.getState();
    }

    @Override
    protected MenuBarState getState(boolean markAsDirty) {
        return (MenuBarState) super.getState(markAsDirty);
    }

    /** Paint (serialize) the component for the client. */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute(MenuBarConstants.OPEN_ROOT_MENU_ON_HOWER,
                openRootOnHover);

        if (isHtmlContentAllowed()) {
            target.addAttribute(MenuBarConstants.HTML_CONTENT_ALLOWED, true);
        }

        target.startTag("options");

        if (getWidth() > -1) {
            target.startTag("moreItem");
            target.addAttribute("text", moreItem.getText());
            if (moreItem.getIcon() != null) {
                target.addAttribute("icon", moreItem.getIcon());
            }
            target.endTag("moreItem");
        }

        target.endTag("options");
        target.startTag("items");

        // This generates the tree from the contents of the menu
        for (MenuItem item : menuItems) {
            paintItem(target, item);
        }

        target.endTag("items");
    }

    private void paintItem(PaintTarget target, MenuItem item)
            throws PaintException {
        if (!item.isVisible()) {
            return;
        }

        target.startTag("item");

        target.addAttribute("id", item.getId());

        if (item.getStyleName() != null) {
            target.addAttribute(MenuBarConstants.ATTRIBUTE_ITEM_STYLE,
                    item.getStyleName());
        }

        if (item.isSeparator()) {
            target.addAttribute("separator", true);
        } else {
            target.addAttribute("text", item.getText());

            Command command = item.getCommand();
            if (command != null) {
                target.addAttribute("command", true);
            }

            Resource icon = item.getIcon();
            if (icon != null) {
                target.addAttribute(MenuBarConstants.ATTRIBUTE_ITEM_ICON, icon);
            }

            if (!item.isEnabled()) {
                target.addAttribute(MenuBarConstants.ATTRIBUTE_ITEM_DISABLED,
                        true);
            }

            String description = item.getDescription();
            if (description != null && !description.isEmpty()) {
                target.addAttribute(MenuBarConstants.ATTRIBUTE_ITEM_DESCRIPTION,
                        description);
            }

            ContentMode contentMode = item.getDescriptionContentMode();
            // If the contentMode is equal to ContentMode.PREFORMATTED, we don't
            // add any attribute.
            if (contentMode != null
                    && contentMode != ContentMode.PREFORMATTED) {
                target.addAttribute(
                        MenuBarConstants.ATTRIBUTE_ITEM_DESCRIPTION_CONTENT_MODE,
                        contentMode.name());
            }

            if (item.isCheckable()) {
                // if the "checked" attribute is present (either true or false),
                // the item is checkable
                target.addAttribute(MenuBarConstants.ATTRIBUTE_CHECKED,
                        item.isChecked());
            }
            if (item.hasChildren()) {
                for (MenuItem child : item.getChildren()) {
                    paintItem(target, child);
                }
            }

        }

        target.endTag("item");
    }

    /** De-serialize changes received from client. */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        final Deque<MenuItem> items = new ArrayDeque<>();
        boolean found = false;

        if (variables.containsKey("clickedId")) {

            Integer clickedId = (Integer) variables.get("clickedId");
            for (MenuItem i : getItems()) {
                items.push(i);
            }

            MenuItem tmpItem = null;

            // Go through all the items in the menu
            while (!found && !items.isEmpty()) {
                tmpItem = items.pop();
                found = (clickedId == tmpItem.getId());

                if (tmpItem.hasChildren()) {
                    for (MenuItem i : tmpItem.getChildren()) {
                        items.push(i);
                    }
                }

            } // while

            // If we got the clicked item, launch the command.
            if (found && tmpItem.isEnabled()) {
                if (tmpItem.isCheckable()) {
                    tmpItem.setChecked(!tmpItem.isChecked());
                }
                if (null != tmpItem.getCommand()) {
                    tmpItem.getCommand().menuSelected(tmpItem);
                }
            }
        }
    }

    /**
     * Constructs an empty, horizontal menu.
     */
    public MenuBar() {
        menuItems = new ArrayList<>();
        setMoreMenuItem(null);
    }

    /**
     * Adds a new menu item to the menu bar
     * <p>
     * Clicking on this menu item has no effect. Use
     * {@link #addItem(String, Command)} or {@link MenuItem#setCommand(Command)}
     * to assign an action to the menu item.
     *
     * @param caption
     *            the text for the menu item
     * @throws IllegalArgumentException
     *
     * @since 8.4
     */
    public MenuBar.MenuItem addItem(String caption) {
        return addItem(caption, null, null);
    }

    /**
     * Add a new item to the menu bar. Command can be null, but a caption must
     * be given.
     *
     * @param caption
     *            the text for the menu item
     * @param command
     *            the command for the menu item
     * @throws IllegalArgumentException
     */
    public MenuBar.MenuItem addItem(String caption, MenuBar.Command command) {
        return addItem(caption, null, command);
    }

    /**
     * Add a new item to the menu bar. Icon and command can be null, but a
     * caption must be given.
     *
     * @param caption
     *            the text for the menu item
     * @param icon
     *            the icon for the menu item
     * @param command
     *            the command for the menu item
     * @throws IllegalArgumentException
     */
    public MenuBar.MenuItem addItem(String caption, Resource icon,
            MenuBar.Command command) {
        if (caption == null) {
            throw new IllegalArgumentException("caption cannot be null");
        }
        MenuItem newItem = new MenuItem(caption, icon, command);
        menuItems.add(newItem);
        markAsDirty();

        return newItem;

    }

    /**
     * Add an item before some item. If the given item does not exist the item
     * is added at the end of the menu. Icon and command can be null, but a
     * caption must be given.
     *
     * @param caption
     *            the text for the menu item
     * @param icon
     *            the icon for the menu item
     * @param command
     *            the command for the menu item
     * @param itemToAddBefore
     *            the item that will be after the new item
     * @throws IllegalArgumentException
     */
    public MenuBar.MenuItem addItemBefore(String caption, Resource icon,
            MenuBar.Command command, MenuBar.MenuItem itemToAddBefore) {
        if (caption == null) {
            throw new IllegalArgumentException("caption cannot be null");
        }

        MenuItem newItem = new MenuItem(caption, icon, command);
        if (menuItems.contains(itemToAddBefore)) {
            int index = menuItems.indexOf(itemToAddBefore);
            menuItems.add(index, newItem);

        } else {
            menuItems.add(newItem);
        }

        markAsDirty();

        return newItem;
    }

    /**
     * Returns a list with all the MenuItem objects in the menu bar.
     *
     * @return a list containing the MenuItem objects in the menu bar
     */
    public List<MenuItem> getItems() {
        return menuItems;
    }

    /**
     * Remove first occurrence the specified item from the main menu.
     *
     * @param item
     *            The item to be removed
     */
    public void removeItem(MenuBar.MenuItem item) {
        if (item != null) {
            menuItems.remove(item);
        }
        markAsDirty();
    }

    /**
     * Empty the menu bar.
     */
    public void removeItems() {
        menuItems.clear();
        markAsDirty();
    }

    /**
     * Returns the size of the menu.
     *
     * @return The size of the menu
     */
    public int getSize() {
        return menuItems.size();
    }

    /**
     * Set the item that is used when collapsing the top level menu. All
     * "overflowing" items will be added below this. The item command will be
     * ignored. If set to null, the default item with a downwards arrow is used.
     *
     * The item command (if specified) is ignored.
     *
     * @param item
     */
    public void setMoreMenuItem(MenuItem item) {
        if (item != null) {
            moreItem = item;
        } else {
            moreItem = new MenuItem("", null, null);
        }
        markAsDirty();
    }

    /**
     * Get the MenuItem used as the collapse menu item.
     *
     * @return
     */
    public MenuItem getMoreMenuItem() {
        return moreItem;
    }

    /**
     * Using this method menubar can be put into a special mode where top level
     * menus opens without clicking on the menu, but automatically when mouse
     * cursor is moved over the menu. In this mode the menu also closes itself
     * if the mouse is moved out of the opened menu.
     * <p>
     * Note, that on touch devices the menu still opens on a click event.
     *
     * @param autoOpenTopLevelMenu
     *            true if menus should be opened without click, the default is
     *            false
     */
    public void setAutoOpen(boolean autoOpenTopLevelMenu) {
        if (autoOpenTopLevelMenu != openRootOnHover) {
            openRootOnHover = autoOpenTopLevelMenu;
            markAsDirty();
        }
    }

    /**
     * Detects whether the menubar is in a mode where top level menus are
     * automatically opened when the mouse cursor is moved over the menu.
     * Normally root menu opens only by clicking on the menu. Submenus always
     * open automatically.
     *
     * @return true if the root menus open without click, the default is false
     */
    public boolean isAutoOpen() {
        return openRootOnHover;
    }

    /**
     * Sets whether html is allowed in the item captions. If set to true, the
     * captions are passed to the browser as html and the developer is
     * responsible for ensuring no harmful html is used. If set to false, the
     * content is passed to the browser as plain text.
     *
     * @param htmlContentAllowed
     *            true if the captions are used as html, false if used as plain
     *            text
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        this.htmlContentAllowed = htmlContentAllowed;
        markAsDirty();
    }

    /**
     * Checks whether item captions are interpreted as html or plain text.
     *
     * @return true if the captions are used as html, false if used as plain
     *         text
     * @see #setHtmlContentAllowed(boolean)
     */
    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

    @Override
    public int getTabIndex() {
        return getState(false).tabIndex;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
     */
    @Override
    public void setTabIndex(int tabIndex) {
        getState().tabIndex = tabIndex;
    }

    @Override
    public void focus() {
        // Overridden only to make public
        super.focus();
    }

    /**
     * This interface contains the layer for menu commands of the
     * {@link com.vaadin.ui.MenuBar} class. It's method will fire when the user
     * clicks on the containing {@link com.vaadin.ui.MenuBar.MenuItem}. The
     * selected item is given as an argument.
     */
    @FunctionalInterface
    public interface Command extends Serializable {
        public void menuSelected(MenuBar.MenuItem selectedItem);
    }

    /**
     * A composite class for menu items and sub-menus. You can set commands to
     * be fired on user click by implementing the
     * {@link com.vaadin.ui.MenuBar.Command} interface. You can also add
     * multiple MenuItems to a MenuItem and create a sub-menu.
     *
     */
    public class MenuItem implements Serializable, EventTrigger {

        /** Private members * */
        private final int itsId;
        private Command itsCommand;
        private String itsText;
        private List<MenuItem> itsChildren;
        private Resource itsIcon;
        private MenuItem itsParent;
        private boolean enabled = true;
        private boolean visible = true;
        private boolean isSeparator = false;
        private String styleName;
        private String description;
        private ContentMode descriptionContentMode = ContentMode.PREFORMATTED;
        private boolean checkable = false;
        private boolean checked = false;

        /**
         * Constructs a new menu item that can optionally have an icon and a
         * command associated with it. Icon and command can be null, but a
         * caption must be given.
         *
         * @param caption
         *            The text associated with the command
         * @param command
         *            The command to be fired
         * @throws IllegalArgumentException
         */
        public MenuItem(String caption, Resource icon,
                MenuBar.Command command) {
            if (caption == null) {
                throw new IllegalArgumentException("caption cannot be null");
            }
            itsId = ++numberOfItems;
            itsText = caption;
            itsIcon = icon;
            itsCommand = command;
        }

        /**
         * Checks if the item has children (if it is a sub-menu).
         *
         * @return True if this item has children
         */
        public boolean hasChildren() {
            return !isSeparator() && itsChildren != null;
        }

        /**
         * Adds a separator to this menu. A separator is a way to visually group
         * items in a menu, to make it easier for users to find what they are
         * looking for in the menu.
         *
         * @author Jouni Koivuviita / Vaadin Ltd.
         * @since 6.2.0
         */
        public MenuBar.MenuItem addSeparator() {
            MenuItem item = addItem("", null, null);
            item.setSeparator(true);
            return item;
        }

        public MenuBar.MenuItem addSeparatorBefore(MenuItem itemToAddBefore) {
            MenuItem item = addItemBefore("", null, null, itemToAddBefore);
            item.setSeparator(true);
            return item;
        }

        /**
         * Add a new menu item inside this menu item, creating a sub-menu.
         * <p>
         * Clicking on the new item has no effect. Use
         * {@link #addItem(String, Command)} or {@link #setCommand(Command)} to
         * assign an action to the menu item.
         *
         * @param caption
         *            the text for the menu item
         *
         * @since 8.4
         */
        public MenuBar.MenuItem addItem(String caption) {
            return addItem(caption, null, null);
        }

        /**
         * Add a new item inside this item, thus creating a sub-menu. Command
         * can be null, but a caption must be given.
         *
         * @param caption
         *            the text for the menu item
         * @param command
         *            the command for the menu item
         */
        public MenuBar.MenuItem addItem(String caption,
                MenuBar.Command command) {
            return addItem(caption, null, command);
        }

        /**
         * Add a new item inside this item, thus creating a sub-menu. Icon and
         * command can be null, but a caption must be given.
         *
         * @param caption
         *            the text for the menu item
         * @param icon
         *            the icon for the menu item
         * @param command
         *            the command for the menu item
         * @throws IllegalStateException
         *             If the item is checkable and thus cannot have children.
         */
        public MenuBar.MenuItem addItem(String caption, Resource icon,
                MenuBar.Command command) throws IllegalStateException {
            if (isSeparator()) {
                throw new UnsupportedOperationException(
                        "Cannot add items to a separator");
            }
            if (isCheckable()) {
                throw new IllegalStateException(
                        "A checkable item cannot have children");
            }
            if (caption == null) {
                throw new IllegalArgumentException("Caption cannot be null");
            }

            if (itsChildren == null) {
                itsChildren = new ArrayList<>();
            }

            MenuItem newItem = new MenuItem(caption, icon, command);

            // The only place where the parent is set
            newItem.setParent(this);
            itsChildren.add(newItem);

            markAsDirty();

            return newItem;
        }

        /**
         * Add an item before some item. If the given item does not exist the
         * item is added at the end of the menu. Icon and command can be null,
         * but a caption must be given.
         *
         * @param caption
         *            the text for the menu item
         * @param icon
         *            the icon for the menu item
         * @param command
         *            the command for the menu item
         * @param itemToAddBefore
         *            the item that will be after the new item
         * @throws IllegalStateException
         *             If the item is checkable and thus cannot have children.
         */
        public MenuBar.MenuItem addItemBefore(String caption, Resource icon,
                MenuBar.Command command, MenuBar.MenuItem itemToAddBefore)
                throws IllegalStateException {
            if (isCheckable()) {
                throw new IllegalStateException(
                        "A checkable item cannot have children");
            }
            MenuItem newItem = null;

            if (hasChildren() && itsChildren.contains(itemToAddBefore)) {
                int index = itsChildren.indexOf(itemToAddBefore);
                newItem = new MenuItem(caption, icon, command);
                newItem.setParent(this);
                itsChildren.add(index, newItem);
            } else {
                newItem = addItem(caption, icon, command);
            }

            markAsDirty();

            return newItem;
        }

        /**
         * For the associated command.
         *
         * @return The associated command, or null if there is none
         */
        public Command getCommand() {
            return itsCommand;
        }

        /**
         * Gets the objects icon.
         *
         * @return The icon of the item, null if the item doesn't have an icon
         */
        public Resource getIcon() {
            return itsIcon;
        }

        /**
         * For the containing item. This will return null if the item is in the
         * top-level menu bar.
         *
         * @return The containing {@link com.vaadin.ui.MenuBar.MenuItem} , or
         *         null if there is none
         */
        public MenuBar.MenuItem getParent() {
            return itsParent;
        }

        /**
         * This will return the children of this item or null if there are none.
         *
         * @return List of children items, or null if there are none
         */
        public List<MenuItem> getChildren() {
            return itsChildren;
        }

        /**
         * Gets the objects text.
         *
         * @return The text
         */
        public java.lang.String getText() {
            return itsText;
        }

        /**
         * Returns the number of children.
         *
         * @return The number of child items
         */
        public int getSize() {
            if (itsChildren != null) {
                return itsChildren.size();
            }
            return -1;
        }

        /**
         * Get the unique identifier for this item.
         *
         * @return The id of this item
         */
        public int getId() {
            return itsId;
        }

        /**
         * Set the command for this item. Set null to remove.
         *
         * @param command
         *            The MenuCommand of this item
         */
        public void setCommand(MenuBar.Command command) {
            itsCommand = command;
        }

        /**
         * Sets the icon. Set null to remove.
         *
         * @param icon
         *            The icon for this item
         */
        public void setIcon(Resource icon) {
            itsIcon = icon;
            markAsDirty();
        }

        /**
         * Set the text of this object.
         *
         * @param text
         *            Text for this object
         */
        public void setText(java.lang.String text) {
            if (text != null) {
                itsText = text;
            }
            markAsDirty();
        }

        /**
         * Remove the first occurrence of the item.
         *
         * @param item
         *            The item to be removed
         */
        public void removeChild(MenuBar.MenuItem item) {
            if (item != null && itsChildren != null) {
                itsChildren.remove(item);
                if (itsChildren.isEmpty()) {
                    itsChildren = null;
                }
                markAsDirty();
            }
        }

        /**
         * Empty the list of children items.
         */
        public void removeChildren() {
            if (itsChildren != null) {
                itsChildren.clear();
                itsChildren = null;
                markAsDirty();
            }
        }

        /**
         * Set the parent of this item. This is called by the addItem method.
         *
         * @param parent
         *            The parent item
         */
        protected void setParent(MenuBar.MenuItem parent) {
            itsParent = parent;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            markAsDirty();
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
            markAsDirty();
        }

        public boolean isVisible() {
            return visible;
        }

        private void setSeparator(boolean isSeparator) {
            this.isSeparator = isSeparator;
            markAsDirty();
        }

        public boolean isSeparator() {
            return isSeparator;
        }

        public void setStyleName(String styleName) {
            this.styleName = styleName;
            markAsDirty();
        }

        public String getStyleName() {
            return styleName;
        }

        /**
         * Analogous method to {@link AbstractComponent#setDescription(String)}.
         * Sets the item's description. See {@link #getDescription()} for more
         * information on what the description is.
         *
         * @param description
         *            the new description string for the component.
         */
        public void setDescription(String description) {
            setDescription(description, ContentMode.PREFORMATTED);
        }

        /**
         * Analogous method to
         * {@link AbstractComponent#setDescription(String, ContentMode)}. Sets
         * the item's description using given content mode. See
         * {@link #getDescription()} for more information on what the
         * description is.
         * <p>
         * If the content {@code mode} is {@literal ContentMode.HTML} the
         * description is displayed as HTML in tooltips or directly in certain
         * components so care should be taken to avoid creating the possibility
         * for HTML injection and possibly XSS vulnerabilities.
         *
         * @see ContentMode
         *
         * @param description
         *            the new description string for the component.
         * @param mode
         *            the content mode for the description
         * @since 8.3
         */
        public void setDescription(String description, ContentMode mode) {
            this.description = description;
            this.descriptionContentMode = mode;
            markAsDirty();
        }

        /**
         * <p>
         * Gets the item's description. The description can be used to briefly
         * describe the state of the item to the user. The description string
         * may contain certain XML tags:
         * </p>
         *
         * <p>
         * <table border=1>
         * <tr>
         * <td width=120><b>Tag</b></td>
         * <td width=120><b>Description</b></td>
         * <td width=120><b>Example</b></td>
         * </tr>
         * <tr>
         * <td>&lt;b></td>
         * <td>bold</td>
         * <td><b>bold text</b></td>
         * </tr>
         * <tr>
         * <td>&lt;i></td>
         * <td>italic</td>
         * <td><i>italic text</i></td>
         * </tr>
         * <tr>
         * <td>&lt;u></td>
         * <td>underlined</td>
         * <td><u>underlined text</u></td>
         * </tr>
         * <tr>
         * <td>&lt;br></td>
         * <td>linebreak</td>
         * <td>N/A</td>
         * </tr>
         * <tr>
         * <td>&lt;ul><br>
         * &lt;li>item1<br>
         * &lt;li>item1<br>
         * &lt;/ul></td>
         * <td>item list</td>
         * <td>
         * <ul>
         * <li>item1
         * <li>item2
         * </ul>
         * </td>
         * </tr>
         * </table>
         * </p>
         *
         * <p>
         * These tags may be nested.
         * </p>
         *
         * @return item's description <code>String</code>
         */
        public String getDescription() {
            return description;
        }

        /**
         * Gets the content mode of the description of the menu item. The
         * description is displayed as the tooltip of the menu item in the UI.
         * <p>
         * If no content mode was explicitly set using the
         * {@link #setDescription(String, ContentMode)} method, the content mode
         * will be {@link ContentMode#PREFORMATTED}
         * </p>
         *
         * @return the {@link ContentMode} of the description of this menu item
         * @see ContentMode
         * @since 8.3
         */
        public ContentMode getDescriptionContentMode() {
            return descriptionContentMode;
        }

        /**
         * Gets the checkable state of the item - whether the item has checked
         * and unchecked states. If an item is checkable its checked state (as
         * returned by {@link #isChecked()}) is indicated in the UI.
         *
         * <p>
         * An item is not checkable by default.
         * </p>
         *
         * @return true if the item is checkable, false otherwise
         * @since 6.6.2
         */
        public boolean isCheckable() {
            return checkable;
        }

        /**
         * Sets the checkable state of the item. If an item is checkable its
         * checked state (as returned by {@link #isChecked()}) is indicated in
         * the UI.
         *
         * <p>
         * An item is not checkable by default.
         * </p>
         *
         * <p>
         * Items with sub items cannot be checkable.
         * </p>
         *
         * @param checkable
         *            true if the item should be checkable, false otherwise
         * @throws IllegalStateException
         *             If the item has children
         * @since 6.6.2
         */
        public void setCheckable(boolean checkable)
                throws IllegalStateException {
            if (hasChildren()) {
                throw new IllegalStateException(
                        "A menu item with children cannot be checkable");
            }
            this.checkable = checkable;
            markAsDirty();
        }

        /**
         * Gets the checked state of the item (checked or unchecked). Only used
         * if the item is checkable (as indicated by {@link #isCheckable()}).
         * The checked state is indicated in the UI with the item, if the item
         * is checkable.
         *
         * <p>
         * An item is not checked by default.
         * </p>
         *
         * <p>
         * The CSS style corresponding to the checked state is "-checked".
         * </p>
         *
         * @return true if the item is checked, false otherwise
         * @since 6.6.2
         */
        public boolean isChecked() {
            return checked;
        }

        /**
         * Sets the checked state of the item. Only used if the item is
         * checkable (indicated by {@link #isCheckable()}). The checked state is
         * indicated in the UI with the item, if the item is checkable.
         *
         * <p>
         * An item is not checked by default.
         * </p>
         *
         * <p>
         * The CSS style corresponding to the checked state is "-checked".
         * </p>
         *
         * @since 6.6.2
         */
        public void setChecked(boolean checked) {
            this.checked = checked;
            markAsDirty();
        }

        /**
         * Gets the menu bar this item is part of.
         *
         * @return the menu bar this item is attached to
         * @since 8.4
         */
        public MenuBar getMenuBar() {
            return MenuBar.this;
        }

        @Override
        public AbstractClientConnector getConnector() {
            return getMenuBar();
        }

        @Override
        public String getPartInformation() {
            return String.valueOf(getId());
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        for (MenuItem item : getItems()) {
            design.appendChild(createMenuElement(item, designContext));
        }

        // in many cases there seems to be an empty more menu item
        if (getMoreMenuItem() != null
                && !getMoreMenuItem().getText().isEmpty()) {
            Element moreMenu = createMenuElement(getMoreMenuItem(),
                    designContext);
            moreMenu.attr("more", true);
            design.appendChild(moreMenu);
        }

        if (!htmlContentAllowed) {
            design.attr(DESIGN_ATTR_PLAIN_TEXT, true);
        }
    }

    protected Element createMenuElement(MenuItem item, DesignContext context) {
        Element menuElement = new Element(Tag.valueOf("menu"), "");
        // Defaults
        MenuItem def = new MenuItem("", null, null);

        Attributes attr = menuElement.attributes();
        DesignAttributeHandler.writeAttribute("icon", attr, item.getIcon(),
                def.getIcon(), Resource.class, context);
        DesignAttributeHandler.writeAttribute("disabled", attr,
                !item.isEnabled(), !def.isEnabled(), boolean.class, context);
        DesignAttributeHandler.writeAttribute("visible", attr, item.isVisible(),
                def.isVisible(), boolean.class, context);
        DesignAttributeHandler.writeAttribute("separator", attr,
                item.isSeparator(), def.isSeparator(), boolean.class, context);
        DesignAttributeHandler.writeAttribute("checkable", attr,
                item.isCheckable(), def.isCheckable(), boolean.class, context);
        DesignAttributeHandler.writeAttribute("checked", attr, item.isChecked(),
                def.isChecked(), boolean.class, context);
        DesignAttributeHandler.writeAttribute("description", attr,
                item.getDescription(), def.getDescription(), String.class,
                context);
        DesignAttributeHandler.writeAttribute("descriptioncontentmode", attr,
                item.getDescriptionContentMode().name(),
                def.getDescriptionContentMode().name(), String.class, context);
        DesignAttributeHandler.writeAttribute("style-name", attr,
                item.getStyleName(), def.getStyleName(), String.class, context);

        menuElement.append(item.getText());

        if (item.hasChildren()) {
            for (MenuItem subMenu : item.getChildren()) {
                menuElement.appendChild(createMenuElement(subMenu, context));
            }
        }

        return menuElement;
    }

    protected MenuItem readMenuElement(Element menuElement) {
        Resource icon = null;
        if (menuElement.hasAttr("icon")) {
            icon = DesignAttributeHandler.getFormatter()
                    .parse(menuElement.attr("icon"), Resource.class);
        }

        String caption = "";
        List<Element> subMenus = new ArrayList<>();
        for (Node node : menuElement.childNodes()) {
            if (node instanceof Element
                    && ((Element) node).tagName().equals("menu")) {
                subMenus.add((Element) node);
            } else {
                caption += node.toString();
            }
        }
        MenuItem menu = new MenuItem(caption.trim(), icon, null);

        Attributes attr = menuElement.attributes();
        if (menuElement.hasAttr("icon")) {
            menu.setIcon(DesignAttributeHandler.readAttribute("icon", attr,
                    Resource.class));
        }
        if (menuElement.hasAttr("disabled")) {
            menu.setEnabled(!DesignAttributeHandler.readAttribute("disabled",
                    attr, boolean.class));
        }
        if (menuElement.hasAttr("visible")) {
            menu.setVisible(DesignAttributeHandler.readAttribute("visible",
                    attr, boolean.class));
        }
        if (menuElement.hasAttr("separator")) {
            menu.setSeparator(DesignAttributeHandler.readAttribute("separator",
                    attr, boolean.class));
        }
        if (menuElement.hasAttr("checkable")) {
            menu.setCheckable(DesignAttributeHandler.readAttribute("checkable",
                    attr, boolean.class));
        }
        if (menuElement.hasAttr("checked")) {
            menu.setChecked(DesignAttributeHandler.readAttribute("checked",
                    attr, boolean.class));
        }
        if (menuElement.hasAttr("description")) {
            String description = DesignAttributeHandler
                    .readAttribute("description", attr, String.class);
            if (menuElement.hasAttr("descriptioncontentmode")) {
                String contentModeString = DesignAttributeHandler.readAttribute(
                        "descriptioncontentmode", attr, String.class);
                menu.setDescription(description,
                        ContentMode.valueOf(contentModeString));
            } else {
                menu.setDescription(description);
            }
        }
        if (menuElement.hasAttr("style-name")) {
            menu.setStyleName(DesignAttributeHandler.readAttribute("style-name",
                    attr, String.class));
        }

        if (!subMenus.isEmpty()) {
            menu.itsChildren = new ArrayList<>();
        }

        for (Element subMenu : subMenus) {
            MenuItem newItem = readMenuElement(subMenu);

            newItem.setParent(menu);
            menu.itsChildren.add(newItem);
        }

        return menu;
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);

        for (Element itemElement : design.children()) {
            if (itemElement.tagName().equals("menu")) {
                MenuItem menuItem = readMenuElement(itemElement);
                if (itemElement.hasAttr("more")) {
                    setMoreMenuItem(menuItem);
                } else {
                    menuItems.add(menuItem);
                }
            }
        }

        setHtmlContentAllowed(!design.hasAttr(DESIGN_ATTR_PLAIN_TEXT));
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> result = super.getCustomAttributes();
        result.add(DESIGN_ATTR_PLAIN_TEXT);
        result.add("html-content-allowed");
        return result;
    }
}
