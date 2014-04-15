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
package com.vaadin.client.ui.tabsheet;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.VTabsheetBase;
import com.vaadin.shared.ui.tabsheet.TabsheetBaseConstants;

public abstract class TabsheetBaseConnector extends
        AbstractComponentContainerConnector implements Paintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;

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

            if (tab.getBooleanAttribute(TabsheetBaseConstants.ATTRIBUTE_TAB_DISABLED)) {
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
