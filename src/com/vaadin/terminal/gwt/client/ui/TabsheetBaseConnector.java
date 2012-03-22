/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.UIDL;

public abstract class TabsheetBaseConnector extends
        AbstractComponentContainerConnector {

    public static final String ATTRIBUTE_TAB_DISABLED = "disabled";
    public static final String ATTRIBUTE_TAB_DESCRIPTION = "description";
    public static final String ATTRIBUTE_TAB_ERROR_MESSAGE = "error";
    public static final String ATTRIBUTE_TAB_CAPTION = "caption";
    public static final String ATTRIBUTE_TAB_ICON = "icon";

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;

        // Ensure correct implementation
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        // Update member references
        getWidget().id = uidl.getId();
        getWidget().disabled = !isEnabled();

        // Render content
        final UIDL tabs = uidl.getChildUIDL(0);

        // Widgets in the TabSheet before update
        ArrayList<Widget> oldWidgets = new ArrayList<Widget>();
        for (Iterator<Widget> iterator = getWidget().getWidgetIterator(); iterator
                .hasNext();) {
            oldWidgets.add(iterator.next());
        }

        // Clear previous values
        getWidget().tabKeys.clear();
        getWidget().disabledTabKeys.clear();

        int index = 0;
        for (final Iterator<Object> it = tabs.getChildIterator(); it.hasNext();) {
            final UIDL tab = (UIDL) it.next();
            final String key = tab.getStringAttribute("key");
            final boolean selected = tab.getBooleanAttribute("selected");
            final boolean hidden = tab.getBooleanAttribute("hidden");

            if (tab.getBooleanAttribute(ATTRIBUTE_TAB_DISABLED)) {
                getWidget().disabledTabKeys.add(key);
            }

            getWidget().tabKeys.add(key);

            if (selected) {
                getWidget().activeTabIndex = index;
            }
            getWidget().renderTab(tab, index, selected, hidden);
            index++;
        }

        int tabCount = getWidget().getTabCount();
        while (tabCount-- > index) {
            getWidget().removeTab(index);
        }

        for (int i = 0; i < getWidget().getTabCount(); i++) {
            ComponentConnector p = getWidget().getTab(i);
            // null for PlaceHolder widgets
            if (p != null) {
                oldWidgets.remove(p.getWidget());
            }
        }

        // Detach any old tab widget, should be max 1
        for (Iterator<Widget> iterator = oldWidgets.iterator(); iterator
                .hasNext();) {
            Widget oldWidget = iterator.next();
            if (oldWidget.isAttached()) {
                oldWidget.removeFromParent();
            }
        }

    }

    @Override
    public VTabsheetBase getWidget() {
        return (VTabsheetBase) super.getWidget();
    }

}
