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
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.Window;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.extensions.DropTargetExtensionConnector;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.shared.ui.grid.GridDropTargetRpc;
import com.vaadin.shared.ui.grid.GridDropTargetState;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.components.grid.GridDropTarget;

import elemental.events.Event;
import elemental.json.JsonObject;

/**
 * Makes Grid an HTML5 drop target. This is the client side counterpart of
 * {@link GridDropTarget}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(GridDropTarget.class)
public class GridDropTargetConnector extends DropTargetExtensionConnector {

    /**
     * Current style name
     */
    private String currentStyleName;

    private GridConnector gridConnector;

    /**
     * Class name to apply when an element is dragged over a row and the
     * location is {@link DropLocation#ON_TOP}.
     */
    private String styleDragCenter;

    /**
     * Class name to apply when an element is dragged over a row and the
     * location is {@link DropLocation#ABOVE}.
     */
    private String styleDragTop;

    /**
     * Class name to apply when an element is dragged over a row and the
     * location is {@link DropLocation#BELOW}.
     */
    private String styleDragBottom;

    @Override
    protected void extend(ServerConnector target) {
        gridConnector = (GridConnector) target;

        super.extend(target);
    }

    @Override
    protected void sendDropEventToServer(List<String> types,
            Map<String, String> data, String dropEffect,
            NativeEvent dropEvent) {

        String rowKey = null;
        DropLocation dropLocation = null;

        Optional<TableRowElement> targetRow = getTargetRow(
                (Element) dropEvent.getEventTarget().cast());
        if (targetRow.isPresent()) {
            rowKey = getRowData(targetRow.get())
                    .getString(GridState.JSONKEY_ROWKEY);
            dropLocation = getDropLocation(targetRow.get(), dropEvent);
        }

        getRpcProxy(GridDropTargetRpc.class).drop(types, data, dropEffect,
                rowKey, dropLocation);
    }

    private JsonObject getRowData(TableRowElement row) {
        int rowIndex = ((Escalator.AbstractRowContainer) getGridBody())
                .getLogicalRowIndex(row);
        return gridConnector.getDataSource().getRow(rowIndex);
    }

    /**
     * Returns the location of the event within the row.
     */
    private DropLocation getDropLocation(Element target, NativeEvent event) {
        if (getState().dropMode == DropMode.BETWEEN) {
            if (getRelativeY(target, event) < (target.getOffsetHeight() / 2)) {
                return DropLocation.ABOVE;
            } else {
                return DropLocation.BELOW;
            }
        } else if (getState().dropMode == DropMode.ON_TOP_OR_BETWEEN) {
            if (getRelativeY(target, event) < getState().dropThreshold) {
                return DropLocation.ABOVE;
            } else if (target.getOffsetHeight()
                    - getRelativeY(target, event) < getState().dropThreshold) {
                return DropLocation.BELOW;
            } else {
                return DropLocation.ON_TOP;
            }
        }
        return DropLocation.ON_TOP;
    }

    private int getRelativeY(Element element, NativeEvent event) {
        int relativeTop = element.getAbsoluteTop() - Window.getScrollTop();
        return WidgetUtil.getTouchOrMouseClientY(event) - relativeTop;
    }

    @Override
    protected void onDragEnter(Event event) {
        // Generate style names for the drop target
        String styleRow = gridConnector.getWidget().getStylePrimaryName()
                + "-row";
        styleDragCenter = styleRow + STYLE_SUFFIX_DRAG_CENTER;
        styleDragTop = styleRow + STYLE_SUFFIX_DRAG_TOP;
        styleDragBottom = styleRow + STYLE_SUFFIX_DRAG_BOTTOM;

        super.onDragEnter(event);
    }

    @Override
    protected void addDragOverStyle(NativeEvent event) {
        getTargetRow(((Element) event.getEventTarget().cast()))
                .ifPresent(target -> {

                    // Get required class name
                    String className = getTargetClassName(target, event);

                    // Add or replace class name if changed
                    if (!target.hasClassName(className)) {
                        if (currentStyleName != null) {
                            target.removeClassName(currentStyleName);
                        }
                        target.addClassName(className);
                        currentStyleName = className;
                    }
                });
    }

    private String getTargetClassName(Element target, NativeEvent event) {
        String className;

        switch (getDropLocation(target, event)) {
        case ABOVE:
            className = styleDragTop;
            break;
        case BELOW:
            className = styleDragBottom;
            break;
        case ON_TOP:
        default:
            className = styleDragCenter;
            break;
        }

        return className;
    }

    @Override
    protected void removeDragOverStyle(NativeEvent event) {

        // Remove all possible style names
        getTargetRow((Element) event.getEventTarget().cast()).ifPresent(e -> {
            e.removeClassName(styleDragCenter);
            e.removeClassName(styleDragTop);
            e.removeClassName(styleDragBottom);
        });
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
    public GridDropTargetState getState() {
        return (GridDropTargetState) super.getState();
    }
}
