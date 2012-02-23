/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public abstract class VTabsheetBasePaintable extends
        VAbstractPaintableWidgetContainer {

    public static final String ATTRIBUTE_TAB_DISABLED = "disabled";
    public static final String ATTRIBUTE_TAB_DESCRIPTION = "description";
    public static final String ATTRIBUTE_TAB_CAPTION = "caption";

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().client = client;

        // Ensure correct implementation
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        // Update member references
        getWidgetForPaintable().id = uidl.getId();
        getWidgetForPaintable().disabled = getState().isDisabled();

        // Render content
        final UIDL tabs = uidl.getChildUIDL(0);

        // Paintables in the TabSheet before update
        ArrayList<Widget> oldWidgets = new ArrayList<Widget>();
        for (Iterator<Widget> iterator = getWidgetForPaintable()
                .getWidgetIterator(); iterator.hasNext();) {
            oldWidgets.add(iterator.next());
        }

        // Clear previous values
        getWidgetForPaintable().tabKeys.clear();
        getWidgetForPaintable().disabledTabKeys.clear();

        int index = 0;
        for (final Iterator<Object> it = tabs.getChildIterator(); it.hasNext();) {
            final UIDL tab = (UIDL) it.next();
            final String key = tab.getStringAttribute("key");
            final boolean selected = tab.getBooleanAttribute("selected");
            final boolean hidden = tab.getBooleanAttribute("hidden");

            if (tab.getBooleanAttribute(ATTRIBUTE_TAB_DISABLED)) {
                getWidgetForPaintable().disabledTabKeys.add(key);
            }

            getWidgetForPaintable().tabKeys.add(key);

            if (selected) {
                getWidgetForPaintable().activeTabIndex = index;
            }
            getWidgetForPaintable().renderTab(tab, index, selected, hidden);
            index++;
        }

        int tabCount = getWidgetForPaintable().getTabCount();
        while (tabCount-- > index) {
            getWidgetForPaintable().removeTab(index);
        }

        for (int i = 0; i < getWidgetForPaintable().getTabCount(); i++) {
            VPaintableWidget p = getWidgetForPaintable().getTab(i);
            // During the initial rendering the paintable might be null (this is
            // weird...)
            if (p != null) {
                oldWidgets.remove(p.getWidgetForPaintable());
            }
        }

        // Perform unregister for any paintables removed during update
        for (Iterator<Widget> iterator = oldWidgets.iterator(); iterator
                .hasNext();) {
            Widget oldWidget = iterator.next();
            VPaintableWidget oldPaintable = VPaintableMap.get(client)
                    .getPaintable(oldWidget);
            if (oldWidget.isAttached()) {
                oldWidget.removeFromParent();
            }
            VPaintableMap.get(client).unregisterPaintable(oldPaintable);
        }

    }

    @Override
    public VTabsheetBase getWidgetForPaintable() {
        return (VTabsheetBase) super.getWidgetForPaintable();
    }

}
