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
package com.vaadin.tests.widgetset.client;

import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.VGridLayout;
import com.vaadin.client.ui.gridlayout.GridLayoutConnector;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.ScrollableGridLayout;

@Connect(ScrollableGridLayout.class)
public class ScrollableGridLayoutConnector extends GridLayoutConnector
        implements MayScrollChildren {
    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        for (VGridLayout.Cell cell : getWidget().widgetToCell.values()) {
            cell.slot.getWrapperElement().addClassName("v-scrollable");
        }
    }
}
