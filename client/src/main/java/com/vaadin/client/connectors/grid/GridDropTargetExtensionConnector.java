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
package com.vaadin.client.connectors.grid;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.DropTargetExtensionConnector;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.grid.GridDropTargetExtensionRpc;
import com.vaadin.shared.ui.grid.GridDropTargetExtensionState;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.GridDropTargetExtension;

import elemental.events.Event;
import elemental.json.JsonObject;

/**
 * Makes Grid an HTML5 drop target. This is the client side counterpart of
 * {@link GridDropTargetExtension}.
 *
 * @author Vaadin Ltd
 * @since
 */
@Connect(GridDropTargetExtension.class)
public class GridDropTargetExtensionConnector extends
        DropTargetExtensionConnector {

    private GridConnector gridConnector;

    @Override
    protected void extend(ServerConnector target) {
        gridConnector = (GridConnector) target;

        super.extend(target);
    }

    @Override
    protected void sendDropEventToServer(List<String> types,
            Map<String, String> data, Event dropEvent) {

        String rowKey = null;
        Optional<TableRowElement> targetRow = getTargetRow((Element) dropEvent.getTarget());
        if (targetRow.isPresent()) {
            rowKey = getRowData(targetRow.get()).getString(GridState.JSONKEY_ROWKEY);
        }

        getRpcProxy(GridDropTargetExtensionRpc.class)
                .drop(types, data, getState().dropEffect,
                        data.get(DragSourceState.DATA_TYPE_DRAG_SOURCE_ID),
                        rowKey);
    }

    private JsonObject getRowData(TableRowElement row) {
        int rowIndex = ((Escalator.AbstractRowContainer) getGridBody())
                .getLogicalRowIndex(row);
        return gridConnector.getDataSource().getRow(rowIndex);
    }

    @Override
    protected void addTargetIndicator(Event event) {
        getTargetRow(((Element) event.getTarget()))
                .ifPresent(e -> e.addClassName(CLASS_DRAG_OVER));
    }

    @Override
    protected void removeTargetIndicator(Event event) {
        getTargetRow(((Element) event.getTarget()))
                .ifPresent(e -> e.removeClassName(CLASS_DRAG_OVER));
    }

    private Optional<TableRowElement> getTargetRow(Element source) {
        while (!Objects.equals(source, getGridBody().getElement())) {
            if (TableRowElement.is(source)) {
                return Optional.of(source.cast());
            }
            source = source.getParentElement();
        }
        return Optional.empty();
    }

    @Override
    protected Element getDropTargetElement() {
        return getGridBody().getElement();
    }

    private Escalator getEscalator() {
        return gridConnector.getWidget().getEscalator();
    }

    private RowContainer.BodyRowContainer getGridBody() {
        return getEscalator().getBody();
    }

    @Override
    public GridDropTargetExtensionState getState() {
        return (GridDropTargetExtensionState) super.getState();
    }
}
