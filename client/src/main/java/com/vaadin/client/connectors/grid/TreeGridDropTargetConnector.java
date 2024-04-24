/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.connectors.grid;

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableRowElement;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.HierarchicalDataCommunicatorConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.treegrid.TreeGridDropTargetRpc;
import com.vaadin.shared.ui.treegrid.TreeGridDropTargetState;
import com.vaadin.ui.components.grid.TreeGridDropTarget;

import elemental.json.JsonObject;

/**
 * Makes TreeGrid an HTML5 drop target. This is the client side counterpart of
 * {@link TreeGridDropTarget}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(TreeGridDropTarget.class)
public class TreeGridDropTargetConnector extends GridDropTargetConnector {

    @Override
    protected void sendDropEventToServer(List<String> types,
            Map<String, String> data, String dropEffect,
            NativeEvent dropEvent) {
        String rowKey = null;
        DropLocation dropLocation = null;
        Integer rowDepth = null;
        Boolean rowCollapsed = null;

        Element targetElement = getTargetElement(
                (Element) dropEvent.getEventTarget().cast());
        // the target element is either the tablewrapper or one of the body rows
        if (TableRowElement.is(targetElement)) {
            JsonObject rowData = getRowData(targetElement.cast());
            rowKey = rowData.getString(GridState.JSONKEY_ROWKEY);
            dropLocation = getDropLocation(targetElement, dropEvent);

            // Collect hierarchy information
            JsonObject hierarchyDescription = rowData.getObject(
                    HierarchicalDataCommunicatorConstants.ROW_HIERARCHY_DESCRIPTION);
            rowDepth = (int) hierarchyDescription
                    .getNumber(HierarchicalDataCommunicatorConstants.ROW_DEPTH);
            rowCollapsed = hierarchyDescription.getBoolean(
                    HierarchicalDataCommunicatorConstants.ROW_COLLAPSED);
        } else {
            dropLocation = DropLocation.EMPTY;
        }

        MouseEventDetails mouseEventDetails = MouseEventDetailsBuilder
                .buildMouseEventDetails(dropEvent, targetElement);

        getRpcProxy(TreeGridDropTargetRpc.class).drop(types, data, dropEffect,
                rowKey, rowDepth, rowCollapsed, dropLocation,
                mouseEventDetails);
    }

    @Override
    public TreeGridDropTargetState getState() {
        return (TreeGridDropTargetState) super.getState();
    }
}
