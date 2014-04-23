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
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.VTabsheetBase;
import com.vaadin.shared.ui.tabsheet.TabState;
import com.vaadin.shared.ui.tabsheet.TabsheetState;

public abstract class TabsheetBaseConnector extends
        AbstractComponentContainerConnector {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractConnector#init()
     */
    @Override
    protected void init() {
        super.init();

        getWidget().setClient(getConnection());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.client.ui.AbstractComponentConnector#onStateChanged(com.vaadin
     * .client.communication.StateChangeEvent)
     */
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // Update member references
        getWidget().setEnabled(isEnabled());

        // Widgets in the TabSheet before update
        ArrayList<Widget> oldWidgets = new ArrayList<Widget>();
        for (Iterator<Widget> iterator = getWidget().getWidgetIterator(); iterator
                .hasNext();) {
            oldWidgets.add(iterator.next());
        }

        // Clear previous values
        getWidget().clearTabKeys();

        int index = 0;
        for (TabState tab : getState().tabs) {
            final String key = tab.key;
            final boolean selected = key.equals(getState().selected);

            getWidget().addTabKey(key, !tab.enabled && tab.visible);

            if (selected) {
                getWidget().setActiveTabIndex(index);
            }
            getWidget().renderTab(tab, index);
            if (selected) {
                getWidget().selectTab(index);
            }
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

    @Override
    public TabsheetState getState() {
        return (TabsheetState) super.getState();
    }

}
