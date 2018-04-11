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
/*
 * Copyright 2007 Google Inc.
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
package com.vaadin.client.ui.menubar;

// COPIED HERE DUE package privates in GWT
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A widget that can be placed in a
 * {@link com.google.gwt.user.client.ui.MenuBar}. Menu items can either fire a
 * {@link com.google.gwt.user.client.Command} when they are clicked, or open a
 * cascading sub-menu.
 *
 * @deprecated
 */
@Deprecated
public class MenuItem extends UIObject implements HasHTML {

    private static final String DEPENDENT_STYLENAME_SELECTED_ITEM = "selected";

    private Command command;
    private MenuBar parentMenu, subMenu;

    /**
     * Constructs a new menu item that fires a command when it is selected.
     *
     * @param text
     *            the item's text
     * @param cmd
     *            the command to be fired when it is selected
     */
    public MenuItem(String text, Command cmd) {
        this(text, false);
        setCommand(cmd);
    }

    /**
     * Constructs a new menu item that fires a command when it is selected.
     *
     * @param text
     *            the item's text
     * @param asHTML
     *            <code>true</code> to treat the specified text as html
     * @param cmd
     *            the command to be fired when it is selected
     */
    public MenuItem(String text, boolean asHTML, Command cmd) {
        this(text, asHTML);
        setCommand(cmd);
    }

    /**
     * Constructs a new menu item that cascades to a sub-menu when it is
     * selected.
     *
     * @param text
     *            the item's text
     * @param subMenu
     *            the sub-menu to be displayed when it is selected
     */
    public MenuItem(String text, MenuBar subMenu) {
        this(text, false);
        setSubMenu(subMenu);
    }

    /**
     * Constructs a new menu item that cascades to a sub-menu when it is
     * selected.
     *
     * @param text
     *            the item's text
     * @param asHTML
     *            <code>true</code> to treat the specified text as html
     * @param subMenu
     *            the sub-menu to be displayed when it is selected
     */
    public MenuItem(String text, boolean asHTML, MenuBar subMenu) {
        this(text, asHTML);
        setSubMenu(subMenu);
    }

    MenuItem(String text, boolean asHTML) {
        setElement(DOM.createTD());
        setSelectionStyle(false);

        if (asHTML) {
            setHTML(text);
        } else {
            setText(text);
        }
        setStyleName("gwt-MenuItem");
    }

    /**
     * Gets the command associated with this item.
     *
     * @return this item's command, or <code>null</code> if none exists
     */
    public Command getCommand() {
        return command;
    }

    @Override
    public String getHTML() {
        return DOM.getInnerHTML(getElement());
    }

    /**
     * Gets the menu that contains this item.
     *
     * @return the parent menu, or <code>null</code> if none exists.
     */
    public MenuBar getParentMenu() {
        return parentMenu;
    }

    /**
     * Gets the sub-menu associated with this item.
     *
     * @return this item's sub-menu, or <code>null</code> if none exists
     */
    public MenuBar getSubMenu() {
        return subMenu;
    }

    @Override
    public String getText() {
        return DOM.getInnerText(getElement());
    }

    /**
     * Sets the command associated with this item.
     *
     * @param cmd
     *            the command to be associated with this item
     */
    public void setCommand(Command cmd) {
        command = cmd;
    }

    @Override
    public void setHTML(String html) {
        DOM.setInnerHTML(getElement(), html);
    }

    /**
     * Sets the sub-menu associated with this item.
     *
     * @param subMenu
     *            this item's new sub-menu
     */
    public void setSubMenu(MenuBar subMenu) {
        this.subMenu = subMenu;
    }

    @Override
    public void setText(String text) {
        DOM.setInnerText(getElement(), text);
    }

    void setParentMenu(MenuBar parentMenu) {
        this.parentMenu = parentMenu;
    }

    void setSelectionStyle(boolean selected) {
        if (selected) {
            addStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
        } else {
            removeStyleDependentName(DEPENDENT_STYLENAME_SELECTED_ITEM);
        }
    }
}
