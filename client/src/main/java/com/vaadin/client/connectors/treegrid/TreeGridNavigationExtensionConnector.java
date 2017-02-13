/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.connectors.treegrid;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.renderers.HierarchyRenderer;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.GridEventHandler;
import com.vaadin.client.widget.treegrid.TreeGrid;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.treegrid.NodeCollapseRpc;
import com.vaadin.ui.components.treegrid.TreeGridNavigationExtension;

import elemental.json.JsonObject;

/**
 * 
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(TreeGridNavigationExtension.class)
public class TreeGridNavigationExtensionConnector
        extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        final TreeGrid grid = getParent().getWidget();

        grid.addBrowserEventHandler(5, new GridEventHandler<JsonObject>() {
            @Override
            public void onEvent(Grid.GridEvent<JsonObject> event) {
                if (event.isHandled()) {
                    return;
                }

                Event domEvent = event.getDomEvent();

                if (domEvent.getType().equals(BrowserEvents.KEYDOWN)) {

                    // Navigate within hierarchy with ALT/OPTION + ARROW KEY
                    // when hierarchy column is selected
                    if (isHierarchyColumn(event.getCell())
                            && domEvent.getAltKey()
                            && (domEvent.getKeyCode() == KeyCodes.KEY_LEFT
                                    || domEvent
                                            .getKeyCode() == KeyCodes.KEY_RIGHT)) {

                        // Hierarchy metadata
                        boolean collapsed, leaf;
                        int depth, parentIndex;
                        if (event.getCell().getRow()
                                .hasKey(GridState.JSONKEY_ROWDESCRIPTION)) {
                            JsonObject rowDescription = event.getCell().getRow()
                                    .getObject(
                                            GridState.JSONKEY_ROWDESCRIPTION);
                            collapsed = rowDescription.getBoolean("collapsed");
                            leaf = rowDescription.getBoolean("leaf");
                            depth = (int) rowDescription.getNumber("depth");
                            parentIndex = (int) rowDescription
                                    .getNumber("parentIndex");

                            switch (domEvent.getKeyCode()) {
                            case KeyCodes.KEY_RIGHT:
                                if (!leaf) {
                                    if (collapsed) {
                                        toggleCollapse(event.getCell().getRow()
                                                .getString(
                                                        DataCommunicatorConstants.KEY));
                                    } else {
                                        // Focus on next row
                                        grid.focusCell(
                                                event.getCell().getRowIndex()
                                                        + 1,
                                                event.getCell()
                                                        .getColumnIndex());
                                    }
                                }
                                break;
                            case KeyCodes.KEY_LEFT:
                                if (!collapsed) {
                                    // collapse node
                                    toggleCollapse(
                                            event.getCell().getRow().getString(
                                                    DataCommunicatorConstants.KEY));
                                } else if (depth > 0) {
                                    // jump to parent
                                    grid.focusCell(parentIndex,
                                            event.getCell().getColumnIndex());
                                }
                                break;
                            }
                        }
                        event.setHandled(true);
                        return;
                    }
                }
                event.setHandled(false);
            }
        });
    }

    private boolean isHierarchyColumn(EventCellReference<JsonObject> cell) {
        return cell.getColumn().getRenderer() instanceof HierarchyRenderer;
    }

    void toggleCollapse(String rowKey) {
        getRpcProxy(NodeCollapseRpc.class).toggleCollapse(rowKey);
    }

    @Override
    public TreeGridConnector getParent() {
        return (TreeGridConnector) super.getParent();
    }
}
