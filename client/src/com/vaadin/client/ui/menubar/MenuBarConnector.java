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
package com.vaadin.client.ui.menubar;

import java.util.Iterator;
import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ImageIcon;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.VMenuBar;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.menubar.MenuBarConstants;
import com.vaadin.shared.ui.menubar.MenuBarState;

@Connect(com.vaadin.ui.MenuBar.class)
public class MenuBarConnector extends AbstractComponentConnector implements
        Paintable, SimpleManagedLayout {

    /**
     * This method must be implemented to update the client-side component from
     * UIDL data received from server.
     * 
     * This method is called when the page is loaded for the first time, and
     * every time UI changes in the component are received from the server.
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().htmlContentAllowed = uidl
                .hasAttribute(MenuBarConstants.HTML_CONTENT_ALLOWED);

        getWidget().openRootOnHover = uidl
                .getBooleanAttribute(MenuBarConstants.OPEN_ROOT_MENU_ON_HOWER);

        getWidget().enabled = isEnabled();

        // For future connections
        getWidget().client = client;
        getWidget().uidlId = uidl.getId();

        // Empty the menu every time it receives new information
        if (!getWidget().getItems().isEmpty()) {
            getWidget().clearItems();
        }

        UIDL options = uidl.getChildUIDL(0);

        if (null != getState()
                && !ComponentStateUtil.isUndefinedWidth(getState())) {
            UIDL moreItemUIDL = options.getChildUIDL(0);
            StringBuffer itemHTML = new StringBuffer();

            if (moreItemUIDL.hasAttribute("icon")) {
                itemHTML.append("<img src=\""
                        + Util.escapeAttribute(client
                                .translateVaadinUri(moreItemUIDL
                                        .getStringAttribute("icon")))
                        + "\" class=\"" + ImageIcon.CLASSNAME
                        + "\" alt=\"\" />");
            }

            String moreItemText = moreItemUIDL.getStringAttribute("text");
            if ("".equals(moreItemText)) {
                moreItemText = "&#x25BA;";
            }
            itemHTML.append(moreItemText);

            getWidget().moreItem = GWT.create(VMenuBar.CustomMenuItem.class);
            getWidget().moreItem.setHTML(itemHTML.toString());
            getWidget().moreItem.setCommand(VMenuBar.emptyCommand);

            getWidget().collapsedRootItems = new VMenuBar(true, getWidget());
            getWidget().moreItem.setSubMenu(getWidget().collapsedRootItems);
            getWidget().moreItem.addStyleName(getWidget().getStylePrimaryName()
                    + "-more-menuitem");
        }

        UIDL uidlItems = uidl.getChildUIDL(1);
        Iterator<Object> itr = uidlItems.getChildIterator();
        Stack<Iterator<Object>> iteratorStack = new Stack<Iterator<Object>>();
        Stack<VMenuBar> menuStack = new Stack<VMenuBar>();
        VMenuBar currentMenu = getWidget();

        while (itr.hasNext()) {
            UIDL item = (UIDL) itr.next();
            VMenuBar.CustomMenuItem currentItem = null;

            final int itemId = item.getIntAttribute("id");

            boolean itemHasCommand = item.hasAttribute("command");
            boolean itemIsCheckable = item
                    .hasAttribute(MenuBarConstants.ATTRIBUTE_CHECKED);

            String itemHTML = getWidget().buildItemHTML(item);

            Command cmd = null;
            if (!item.hasAttribute("separator")) {
                if (itemHasCommand || itemIsCheckable) {
                    // Construct a command that fires onMenuClick(int) with the
                    // item's id-number
                    cmd = new Command() {
                        @Override
                        public void execute() {
                            getWidget().hostReference.onMenuClick(itemId);
                        }
                    };
                }
            }

            currentItem = currentMenu.addItem(itemHTML.toString(), cmd);
            currentItem.updateFromUIDL(item, client);

            if (item.getChildCount() > 0) {
                menuStack.push(currentMenu);
                iteratorStack.push(itr);
                itr = item.getChildIterator();
                currentMenu = new VMenuBar(true, currentMenu);
                client.getVTooltip().connectHandlersToWidget(currentMenu);
                // this is the top-level style that also propagates to items -
                // any item specific styles are set above in
                // currentItem.updateFromUIDL(item, client)
                if (ComponentStateUtil.hasStyles(getState())) {
                    for (String style : getState().styles) {
                        currentMenu.addStyleDependentName(style);
                    }
                }
                currentItem.setSubMenu(currentMenu);
            }

            while (!itr.hasNext() && !iteratorStack.empty()) {
                boolean hasCheckableItem = false;
                for (VMenuBar.CustomMenuItem menuItem : currentMenu.getItems()) {
                    hasCheckableItem = hasCheckableItem
                            || menuItem.isCheckable();
                }
                if (hasCheckableItem) {
                    currentMenu.addStyleDependentName("check-column");
                } else {
                    currentMenu.removeStyleDependentName("check-column");
                }

                itr = iteratorStack.pop();
                currentMenu = menuStack.pop();
            }
        }// while

        getLayoutManager().setNeedsHorizontalLayout(this);

    }// updateFromUIDL

    @Override
    public VMenuBar getWidget() {
        return (VMenuBar) super.getWidget();
    }

    @Override
    public MenuBarState getState() {
        return (MenuBarState) super.getState();
    }

    @Override
    public void layout() {
        getWidget().iLayout();
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        TooltipInfo info = null;

        // Check content of widget to find tooltip for element
        if (element != getWidget().getElement()) {

            VMenuBar.CustomMenuItem item = getWidget().getMenuItemWithElement(
                    element);
            if (item != null) {
                info = item.getTooltip();
            }
        }

        // Use default tooltip if nothing found from DOM three
        if (info == null) {
            info = super.getTooltipInfo(element);
        }

        return info;
    }

    @Override
    public boolean hasTooltip() {
        /*
         * Item tooltips are not processed until updateFromUIDL, so we can't be
         * sure that there are no tooltips during onStateChange when this method
         * is used.
         */
        return true;
    }
}
