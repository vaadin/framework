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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.Window;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.extensions.DropTargetExtensionConnector;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.escalator.RowContainer.BodyRowContainer;
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

    /**
     * Class name to apply when dragged over an empty grid.
     */
    private String styleDragEmpty;

    /**
     * The latest row that was dragged on top of, or the tablewrapper element
     * returned by {@link #getDropTargetElement()} if drop is not applicable for
     * any body rows. Need to store this so that can remove drop hint styling
     * when the target has changed since all browsers don't seem to always fire
     * the drag-enter drag-exit events in a consistent order.
     */
    private Element latestTargetElement;

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

        Element targetElement = getTargetElement(
                (Element) dropEvent.getEventTarget().cast());
        // the target element is either the tablewrapper or one of the body rows
        if (TableRowElement.is(targetElement)) {
            rowKey = getRowData(targetElement.cast())
                    .getString(GridState.JSONKEY_ROWKEY);
            dropLocation = getDropLocation(targetElement, dropEvent);
        } else {
            dropLocation = DropLocation.EMPTY;
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
        if (TableRowElement.is(target)) {
            if (getState().dropMode == DropMode.BETWEEN) {
                if (getRelativeY(target,
                        event) < (target.getOffsetHeight() / 2)) {
                    return DropLocation.ABOVE;
                } else {
                    return DropLocation.BELOW;
                }
            } else if (getState().dropMode == DropMode.ON_TOP_OR_BETWEEN) {
                if (getRelativeY(target, event) < getState().dropThreshold) {
                    return DropLocation.ABOVE;
                } else if (target.getOffsetHeight() - getRelativeY(target,
                        event) < getState().dropThreshold) {
                    return DropLocation.BELOW;
                } else {
                    return DropLocation.ON_TOP;
                }
            } else {
                return DropLocation.ON_TOP;
            }
        }
        return DropLocation.EMPTY;
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
        styleDragEmpty = gridConnector.getWidget().getStylePrimaryName()
                + "-body" + STYLE_SUFFIX_DRAG_TOP;

        super.onDragEnter(event);
    }

    @Override
    protected void addDragOverStyle(NativeEvent event) {
        Element targetElement = getTargetElement(
                ((Element) event.getEventTarget().cast()));
        // Get required class name
        String className = getTargetClassName(targetElement, event);

        // it seems that sometimes the events are not fired in a consistent
        // order, and this could cause that the correct styles are not removed
        // from the previous target element in removeDragOverStyle(event)
        if (latestTargetElement != null
                && targetElement != latestTargetElement) {
            removeStyles(latestTargetElement);
        }

        latestTargetElement = targetElement;

        // Add or replace class name if changed
        if (!targetElement.hasClassName(className)) {
            if (currentStyleName != null) {
                targetElement.removeClassName(currentStyleName);
            }
            targetElement.addClassName(className);
            currentStyleName = className;
        }
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
        case EMPTY:
            className = styleDragEmpty;
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
        Element targetElement = getTargetElement(
                (Element) event.getEventTarget().cast());
        removeStyles(targetElement);
    }

    private void removeStyles(Element element) {
        element.removeClassName(styleDragCenter);
        element.removeClassName(styleDragTop);
        element.removeClassName(styleDragBottom);
        element.removeClassName(styleDragEmpty);
    }

    private Element getTargetElement(Element source) {
        final Element tableWrapper = getDropTargetElement();
        final BodyRowContainer gridBody = getGridBody();
        final int rowCount = gridBody.getRowCount();

        while (!Objects.equals(source, tableWrapper)) {
            // the drop might happen on top of header, body or footer rows
            if (TableRowElement.is(source)) {
                String parentTagName = source.getParentElement().getTagName();
                if ("thead".equalsIgnoreCase(parentTagName)) {
                    // for empty grid or ON_TOP mode, drop as last row,
                    // otherwise as above first visible row
                    if (rowCount == 0
                            || getState().dropMode == DropMode.ON_TOP) {
                        return tableWrapper;
                    } else {
                        return gridBody.getRowElement(0);
                    }
                } else if ("tfoot".equalsIgnoreCase(parentTagName)) {
                    // for empty grid or ON_TOP mode, drop as last row,
                    // otherwise as below last visible row
                    if (rowCount == 0
                            || getState().dropMode == DropMode.ON_TOP) {
                        return tableWrapper;
                    } else {
                        return gridBody.getRowElement(rowCount - 1);
                    }
                } else { // parent is tbody
                    return source;
                }
            }
            source = source.getParentElement();
        }
        // the drag is on top of the tablewrapper
        // if no rows in grid, or if the drop mode is ON_TOP, then there is no
        // target row for the drop
        if (rowCount == 0 || getState().dropMode == DropMode.ON_TOP) {
            return tableWrapper;
        } else { // if dragged under the last row to empty space, drop target
                 // needs to be below the last row
            return gridBody.getRowElement(rowCount - 1);
        }
    }

    @Override
    protected Element getDropTargetElement() {
        /*
         * The drop target element, the <div class="v-grid-tablewrapper" />.
         * This is where the event listeners are added since then we can accept
         * drops on header, body and footer rows and the "empty area" outside
         * rows. Also it is used since then the drop hints for "empty" area can
         * be shown properly as the grid body would scroll.
         */
        return getEscalator().getTableWrapper();
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
