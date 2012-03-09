/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.VMenuBar.CustomMenuItem;

public class MenuBarConnector extends AbstractComponentConnector implements
        SimpleManagedLayout {
    /**
     * This method must be implemented to update the client-side component from
     * UIDL data received from server.
     * 
     * This method is called when the page is loaded for the first time, and
     * every time UI changes in the component are received from the server.
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and let the containing layout manage caption, etc.
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().htmlContentAllowed = uidl
                .hasAttribute(VMenuBar.HTML_CONTENT_ALLOWED);

        getWidget().openRootOnHover = uidl
                .getBooleanAttribute(VMenuBar.OPEN_ROOT_MENU_ON_HOWER);

        getWidget().enabled = isEnabled();

        // For future connections
        getWidget().client = client;
        getWidget().uidlId = uidl.getId();

        // Empty the menu every time it receives new information
        if (!getWidget().getItems().isEmpty()) {
            getWidget().clearItems();
        }

        UIDL options = uidl.getChildUIDL(0);

        if (null != getState() && !getState().isUndefinedWidth()) {
            UIDL moreItemUIDL = options.getChildUIDL(0);
            StringBuffer itemHTML = new StringBuffer();

            if (moreItemUIDL.hasAttribute("icon")) {
                itemHTML.append("<img src=\""
                        + Util.escapeAttribute(client
                                .translateVaadinUri(moreItemUIDL
                                        .getStringAttribute("icon")))
                        + "\" class=\"" + Icon.CLASSNAME + "\" alt=\"\" />");
            }

            String moreItemText = moreItemUIDL.getStringAttribute("text");
            if ("".equals(moreItemText)) {
                moreItemText = "&#x25BA;";
            }
            itemHTML.append(moreItemText);

            getWidget().moreItem = GWT.create(CustomMenuItem.class);
            getWidget().moreItem.setHTML(itemHTML.toString());
            getWidget().moreItem.setCommand(VMenuBar.emptyCommand);

            getWidget().collapsedRootItems = new VMenuBar(true, getWidget());
            getWidget().moreItem.setSubMenu(getWidget().collapsedRootItems);
            getWidget().moreItem.addStyleName(VMenuBar.CLASSNAME
                    + "-more-menuitem");
        }

        UIDL uidlItems = uidl.getChildUIDL(1);
        Iterator<Object> itr = uidlItems.getChildIterator();
        Stack<Iterator<Object>> iteratorStack = new Stack<Iterator<Object>>();
        Stack<VMenuBar> menuStack = new Stack<VMenuBar>();
        VMenuBar currentMenu = getWidget();

        while (itr.hasNext()) {
            UIDL item = (UIDL) itr.next();
            CustomMenuItem currentItem = null;

            final int itemId = item.getIntAttribute("id");

            boolean itemHasCommand = item.hasAttribute("command");
            boolean itemIsCheckable = item
                    .hasAttribute(VMenuBar.ATTRIBUTE_CHECKED);

            String itemHTML = getWidget().buildItemHTML(item);

            Command cmd = null;
            if (!item.hasAttribute("separator")) {
                if (itemHasCommand || itemIsCheckable) {
                    // Construct a command that fires onMenuClick(int) with the
                    // item's id-number
                    cmd = new Command() {
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
                // this is the top-level style that also propagates to items -
                // any item specific styles are set above in
                // currentItem.updateFromUIDL(item, client)
                if (getState().hasStyles()) {
                    for (String style : getState().getStyle().split(" ")) {
                        currentMenu.addStyleDependentName(style);
                    }
                }
                currentItem.setSubMenu(currentMenu);
            }

            while (!itr.hasNext() && !iteratorStack.empty()) {
                boolean hasCheckableItem = false;
                for (CustomMenuItem menuItem : currentMenu.getItems()) {
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

        getWidget().iLayout(false);

    }// updateFromUIDL

    @Override
    protected Widget createWidget() {
        return GWT.create(VMenuBar.class);
    }

    @Override
    public VMenuBar getWidget() {
        return (VMenuBar) super.getWidget();
    }

    public void layout() {
        getWidget().iLayout();
    }
}
