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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.events.BodyClickHandler;
import com.vaadin.client.widget.grid.events.BodyDoubleClickHandler;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widget.grid.events.GridDoubleClickEvent;
import com.vaadin.client.widget.grid.selection.ClickSelectHandler;
import com.vaadin.client.widget.grid.selection.SpaceSelectHandler;
import com.vaadin.client.widget.grid.sort.SortEvent;
import com.vaadin.client.widget.grid.sort.SortOrder;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;

import elemental.json.JsonObject;

/**
 * A connector class for the typed Grid component.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@Connect(com.vaadin.ui.Grid.class)
public class GridConnector
        extends AbstractListingConnector<SelectionModel<JsonObject>>
        implements HasComponentsConnector, SimpleManagedLayout, DeferredWorker {

    private class ItemClickHandler
            implements BodyClickHandler, BodyDoubleClickHandler {

        @Override
        public void onClick(GridClickEvent event) {
            if (hasEventListener(GridConstants.ITEM_CLICK_EVENT_ID)) {
                fireItemClick(event.getTargetCell(), event.getNativeEvent());
            }
        }

        @Override
        public void onDoubleClick(GridDoubleClickEvent event) {
            if (hasEventListener(GridConstants.ITEM_CLICK_EVENT_ID)) {
                fireItemClick(event.getTargetCell(), event.getNativeEvent());
            }
        }

        private void fireItemClick(CellReference<?> cell,
                NativeEvent mouseEvent) {
            String rowKey = getRowKey((JsonObject) cell.getRow());
            String columnId = columnToIdMap.get(cell.getColumn());
            getRpcProxy(GridServerRpc.class).itemClick(rowKey, columnId,
                    MouseEventDetailsBuilder
                            .buildMouseEventDetails(mouseEvent));
        }
    }

    /* Map to keep track of all added columns */
    private Map<Column<?, JsonObject>, String> columnToIdMap = new HashMap<>();
    /* Child component list for HasComponentsConnector */
    private List<ComponentConnector> childComponents;
    private SpaceSelectHandler<JsonObject> spaceSelectHandler;
    private ClickSelectHandler<JsonObject> clickSelectHandler;
    private ItemClickHandler itemClickHandler = new ItemClickHandler();

    /**
     * Gets the string identifier of a {@link Column} in this grid.
     *
     * @param column
     *            the column for which the identifier is to be retrieved for
     * @return the string identifying the given column in this grid
     */
    public String getColumnId(Grid.Column<?, ?> column) {
        return columnToIdMap.get(column);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Grid<JsonObject> getWidget() {
        return (Grid<JsonObject>) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();

        // Default selection style is space key.
        spaceSelectHandler = new SpaceSelectHandler<>(getWidget());
        clickSelectHandler = new ClickSelectHandler<>(getWidget());
        getWidget().addSortHandler(this::handleSortEvent);
        getWidget().setRowStyleGenerator(rowRef -> {
            JsonObject json = rowRef.getRow();
            return json.hasKey(GridState.JSONKEY_ROWSTYLE)
                    ? json.getString(GridState.JSONKEY_ROWSTYLE) : null;
        });
        getWidget().setCellStyleGenerator(cellRef -> {
            JsonObject row = cellRef.getRow();
            if (!row.hasKey(GridState.JSONKEY_CELLSTYLES)) {
                return null;
            }

            Column<?, JsonObject> column = cellRef.getColumn();
            if (columnToIdMap.containsKey(column)) {
                String id = columnToIdMap.get(column);
                JsonObject cellStyles = row
                        .getObject(GridState.JSONKEY_CELLSTYLES);
                if (cellStyles.hasKey(id)) {
                    return cellStyles.getString(id);
                }
            }

            return null;
        });

        getWidget().addColumnVisibilityChangeHandler(event -> {
            if (event.isUserOriginated()) {
                getRpcProxy(GridServerRpc.class).columnVisibilityChanged(
                        getColumnId(event.getColumn()), event.isHidden());
            }
        });
        getWidget().addColumnResizeHandler(event -> {
            Column<?, JsonObject> column = event.getColumn();
            getRpcProxy(GridServerRpc.class).columnResized(getColumnId(column),
                    column.getWidthActual());
        });

        /* Item click events */
        getWidget().addBodyClickHandler(itemClickHandler);
        getWidget().addBodyDoubleClickHandler(itemClickHandler);
        getWidget().setSelectionModel(new SelectionModel.Single<JsonObject>() {

            @Override
            public void select(JsonObject item) {
                getRpcProxy(SelectionServerRpc.class)
                        .select(item.getString(DataCommunicatorConstants.KEY));
            }

            @Override
            public void deselect(JsonObject item) {
                getRpcProxy(SelectionServerRpc.class).deselect(
                        item.getString(DataCommunicatorConstants.KEY));
            }

            @Override
            public Optional<JsonObject> getSelectedItem() {
                throw new UnsupportedOperationException(
                        "Selected item not known on the client side");
            }

            @Override
            public boolean isSelected(JsonObject item) {
                return item.hasKey(DataCommunicatorConstants.SELECTED)
                        && item.getBoolean(DataCommunicatorConstants.SELECTED);
            }
        });

        layout();
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        super.setDataSource(dataSource);
        getWidget().setDataSource(dataSource);
    }

    @Override
    public void setSelectionModel(SelectionModel<JsonObject> selectionModel) {
        throw new UnsupportedOperationException(
                "Cannot set a selection model for GridConnector");
    }

    /**
     * Adds a column to the Grid widget. For each column a communication id
     * stored for client to server communication.
     *
     * @param column
     *            column to add
     * @param id
     *            communication id
     */
    public void addColumn(Column<?, JsonObject> column, String id) {
        assert !columnToIdMap.containsKey(column) && !columnToIdMap
                .containsValue(id) : "Column with given id already exists.";
        getWidget().addColumn(column);
        columnToIdMap.put(column, id);
    }

    /**
     * Removes a column from Grid widget. This method also removes communication
     * id mapping for the column.
     *
     * @param column
     *            column to remove
     */
    public void removeColumn(Column<?, JsonObject> column) {
        assert columnToIdMap
                .containsKey(column) : "Given Column does not exist.";
        getWidget().removeColumn(column);
        columnToIdMap.remove(column);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        columnToIdMap.clear();
        removeClickHandler();

        if (spaceSelectHandler != null) {
            spaceSelectHandler.removeHandler();
            spaceSelectHandler = null;
        }
    }

    @Override
    public boolean isWorkPending() {
        return getWidget().isWorkPending();
    }

    @Override
    public void layout() {
        getWidget().onResize();
    }

    /**
     * Sends sort information from an event to the server-side of the Grid.
     *
     * @param event
     *            the sort event
     */
    private void handleSortEvent(SortEvent<JsonObject> event) {
        List<String> columnIds = new ArrayList<>();
        List<SortDirection> sortDirections = new ArrayList<>();
        for (SortOrder so : event.getOrder()) {
            if (columnToIdMap.containsKey(so.getColumn())) {
                columnIds.add(columnToIdMap.get(so.getColumn()));
                sortDirections.add(so.getDirection());
            }
        }
        getRpcProxy(GridServerRpc.class).sort(columnIds.toArray(new String[0]),
                sortDirections.toArray(new SortDirection[0]),
                event.isUserOriginated());
    }
    /* HasComponentsConnector */

    @Override
    public void updateCaption(ComponentConnector connector) {
        // Details components don't support captions.
    }

    @Override
    public List<ComponentConnector> getChildComponents() {
        if (childComponents == null) {
            return Collections.emptyList();
        }

        return childComponents;
    }

    @Override
    public void setChildComponents(List<ComponentConnector> children) {
        childComponents = children;

    }

    @Override
    public HandlerRegistration addConnectorHierarchyChangeHandler(
            ConnectorHierarchyChangeHandler handler) {
        return ensureHandlerManager()
                .addHandler(ConnectorHierarchyChangeEvent.TYPE, handler);
    }

    @Override
    public GridState getState() {
        return (GridState) super.getState();
    }

    private void removeClickHandler() {
        if (clickSelectHandler != null) {
            clickSelectHandler.removeHandler();
            clickSelectHandler = null;
        }
    }

    @Override
    public boolean hasTooltip() {
        // Always check for generated descriptions.
        return true;
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        CellReference<JsonObject> cell = getWidget().getCellReference(element);

        if (cell != null) {
            JsonObject row = cell.getRow();

            if (row != null && (row.hasKey(GridState.JSONKEY_ROWDESCRIPTION)
                    || row.hasKey(GridState.JSONKEY_CELLDESCRIPTION))) {

                Column<?, JsonObject> column = cell.getColumn();
                if (columnToIdMap.containsKey(column)) {
                    JsonObject cellDescriptions = row
                            .getObject(GridState.JSONKEY_CELLDESCRIPTION);

                    String id = columnToIdMap.get(column);
                    if (cellDescriptions != null
                            && cellDescriptions.hasKey(id)) {
                        return new TooltipInfo(cellDescriptions.getString(id));
                    } else if (row.hasKey(GridState.JSONKEY_ROWDESCRIPTION)) {
                        return new TooltipInfo(row
                                .getString(GridState.JSONKEY_ROWDESCRIPTION));
                    }
                }
            }
        }

        if (super.hasTooltip()) {
            return super.getTooltipInfo(element);
        } else {
            return null;
        }
    }

    @Override
    protected void sendContextClickEvent(MouseEventDetails details,
            EventTarget eventTarget) {

        // if element is the resize indicator, ignore the event
        if (isResizeHandle(eventTarget)) {
            WidgetUtil.clearTextSelection();
            return;
        }

        EventCellReference<JsonObject> eventCell = getWidget().getEventCell();

        Section section = eventCell.getSection();
        String rowKey = null;
        if (eventCell.isBody() && eventCell.getRow() != null) {
            rowKey = getRowKey(eventCell.getRow());
        }

        String columnId = getColumnId(eventCell.getColumn());

        getRpcProxy(GridServerRpc.class).contextClick(eventCell.getRowIndex(),
                rowKey, columnId, section, details);

        WidgetUtil.clearTextSelection();
    }

    private boolean isResizeHandle(EventTarget eventTarget) {
        if (Element.is(eventTarget)) {
            Element e = Element.as(eventTarget);
            if (e.getClassName().contains("-column-resize-handle")) {
                return true;
            }
        }
        return false;
    }
}
