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
package com.vaadin.tests.widgetset.client.grid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Panel;
import com.vaadin.client.ui.SubPartAware;

/**
 * Pure GWT Test Application base for testing features of a single widget;
 * provides a menu system and convenience method for adding items to it.
 * 
 * @since
 * @author Vaadin Ltd
 */
public abstract class PureGWTTestApplication<T> extends DockLayoutPanel
        implements SubPartAware {

    /**
     * Class describing a menu item with an associated action
     */
    public static class Command {
        private final String title;
        private final ScheduledCommand command;

        /**
         * Creates a Command object, which is used as an action entry in the
         * Menu
         * 
         * @param t
         *            a title string
         * @param cmd
         *            a scheduled command that is executed when this item is
         *            selected
         */
        public Command(String t, ScheduledCommand cmd) {
            title = t;
            command = cmd;
        }

        /**
         * Returns the title of this command item
         * 
         * @return a title string
         */
        public final String getTitle() {
            return title;
        }

        /**
         * Returns the actual scheduled command of this command item
         * 
         * @return a scheduled command
         */
        public final ScheduledCommand getCommand() {
            return command;
        }
    }

    /**
     * A menu object, providing a complete system for building a hierarchical
     * menu bar system.
     */
    public static class Menu {

        private final String title;
        private final MenuBar menubar;
        private final List<Menu> children;
        private final List<Command> items;

        /**
         * Create base-level menu, without a title. This is the root menu bar,
         * which can be attached to a client application window. All other Menus
         * should be added as child menus to this Menu, in order to maintain a
         * nice hierarchy.
         */
        private Menu() {
            title = "";
            menubar = new MenuBar();
            menubar.getElement().setId("menu");
            children = new ArrayList<Menu>();
            items = new ArrayList<Command>();
        }

        /**
         * Create a sub-menu, with a title.
         * 
         * @param title
         */
        public Menu(String title) {
            this.title = title;
            menubar = new MenuBar(true);
            children = new ArrayList<Menu>();
            items = new ArrayList<Command>();
        }

        /**
         * Return the GWT {@link MenuBar} object that provides the widget for
         * this Menu
         * 
         * @return a menubar object
         */
        public MenuBar getMenuBar() {
            return menubar;
        }

        /**
         * Returns the title of this menu entry
         * 
         * @return a title string
         */
        public String getTitle() {
            return title;
        }

        /**
         * Adds a child menu entry to this menu. The title for this entry is
         * taken from the Menu object argument.
         * 
         * @param m
         *            another Menu object
         */
        public void addChildMenu(Menu m) {
            menubar.addItem(m.title, m.menubar);
            children.add(m);
        }

        /**
         * Tests for the existence of a child menu by title at this level of the
         * menu hierarchy
         * 
         * @param title
         *            a title string
         * @return true, if this menu has a direct child menu with the specified
         *         title, otherwise false
         */
        public boolean hasChildMenu(String title) {
            return getChildMenu(title) != null;
        }

        /**
         * Gets a reference to a child menu with a certain title, that is a
         * direct child of this menu level.
         * 
         * @param title
         *            a title string
         * @return a Menu object with the specified title string, or null, if
         *         this menu doesn't have a direct child with the specified
         *         title.
         */
        public Menu getChildMenu(String title) {
            for (Menu m : children) {
                if (m.title.equals(title)) {
                    return m;
                }
            }
            return null;
        }

        /**
         * Adds a command item to the menu. When the entry is clicked, the
         * command is executed.
         * 
         * @param cmd
         *            a command object.
         */
        public void addCommand(Command cmd) {
            menubar.addItem(cmd.title, cmd.command);
            items.add(cmd);
        }

        /**
         * Tests for the existence of a {@link Command} that is the direct child
         * of this level of menu.
         * 
         * @param title
         *            the command's title
         * @return true, if this menu level includes a command item with the
         *         specified title. Otherwise false.
         */
        public boolean hasCommand(String title) {
            return getCommand(title) != null;
        }

        /**
         * Gets a reference to a {@link Command} item that is the direct child
         * of this level of menu.
         * 
         * @param title
         *            the command's title
         * @return a command, if found in this menu level, otherwise null.
         */
        public Command getCommand(String title) {
            for (Command c : items) {
                if (c.title.equals(title)) {
                    return c;
                }
            }
            return null;
        }
    }

    /**
     * Base level menu object, provides visible menu bar
     */
    private final Menu menu;
    private final T testedWidget;

    /**
     * This constructor creates the basic menu bar and adds it to the top of the
     * parent {@link DockLayoutPanel}
     */
    protected PureGWTTestApplication(T widget) {
        super(Unit.PX);
        Panel menuPanel = new LayoutPanel();
        menu = new Menu();
        menuPanel.add(menu.getMenuBar());
        addNorth(menuPanel, 25);
        testedWidget = widget;
    }

    /**
     * Connect an item to the menu structure
     * 
     * @param cmd
     *            a scheduled command; see google's docs
     * @param menupath
     *            path to the item
     */
    public void addMenuCommand(String title, ScheduledCommand cmd,
            String... menupath) {
        Menu m = createMenuPath(menupath);

        m.addCommand(new Command(title, cmd));
    }

    /**
     * Create a menu path, if one doesn't already exist, and return the last
     * menu in the series.
     * 
     * @param path
     *            a varargs list or array of strings describing a menu path,
     *            e.g. "File", "Recent", "User Files", which would result in the
     *            File menu having a submenu called "Recent" which would have a
     *            submenu called "User Files".
     * @return the last Menu object specified by the path
     */
    private Menu createMenuPath(String... path) {
        Menu m = menu;

        for (String p : path) {
            Menu sub = m.getChildMenu(p);

            if (sub == null) {
                sub = new Menu(p);
                m.addChildMenu(sub);
            }
            m = sub;
        }

        return m;
    }

    @Override
    public Element getSubPartElement(String subPart) {
        if (testedWidget instanceof SubPartAware) {
            return ((SubPartAware) testedWidget).getSubPartElement(subPart);
        }
        return null;
    }

    @Override
    public String getSubPartName(Element subElement) {
        if (testedWidget instanceof SubPartAware) {
            return ((SubPartAware) testedWidget).getSubPartName(subElement);
        }
        return null;
    }

    /**
     * Gets the tested widget.
     * 
     * @return tested widget
     */
    public T getTestedWidget() {
        return testedWidget;
    }
}
