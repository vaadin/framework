/*
 * Copyright 2000-2021 Vaadin Ltd.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gwt.core.client.Scheduler;
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
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.connectors.grid.ColumnConnector.CustomColumn;
import com.vaadin.client.data.AbstractRemoteDataSource;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.DataAvailableEvent;
import com.vaadin.client.widget.grid.DataAvailableHandler;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.events.BodyClickHandler;
import com.vaadin.client.widget.grid.events.BodyDoubleClickHandler;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widget.grid.events.GridDoubleClickEvent;
import com.vaadin.client.widget.grid.sort.SortEvent;
import com.vaadin.client.widget.grid.sort.SortOrder;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.client.widgets.Grid.FooterRow;
import com.vaadin.client.widgets.Grid.HeaderRow;
import com.vaadin.client.widgets.Grid.SelectionColumn;
import com.vaadin.client.widgets.Grid.StaticSection.StaticCell;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridClientRpc;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.ui.grid.SectionState;
import com.vaadin.shared.ui.grid.SectionState.CellState;
import com.vaadin.shared.ui.grid.SectionState.RowState;

import elemental.json.JsonObject;

/**
 * A connector class for the typed Grid component.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@Connect(com.vaadin.ui.Grid.class)
public class GridConnector extends AbstractListingConnector
        implements HasComponentsConnector, SimpleManagedLayout, DeferredWorker {

    private Set<Runnable> refreshDetailsCallbacks = new HashSet<>();

    /**
     * Server-to-client RPC implementation for GridConnector.
     * <p>
     * The scrolling methods must trigger the scrolling only after any potential
     * resizing or other similar action triggered from the server side within
     * the same round trip has had a chance to happen, so there needs to be a
     * delay. The delay is done with <code>scheduleDeferred</code> rather than
     * <code>scheduleFinally</code> because otherwise the order of the
     * operations isn't guaranteed.
     *
     */
    private class GridConnectorClientRpc implements GridClientRpc {
        private final Grid<JsonObject> grid;
        private HandlerRegistration dataAvailableHandlerRegistration = null;
        private boolean recalculateScheduled = false;

        private GridConnectorClientRpc(Grid<JsonObject> grid) {
            this.grid = grid;
        }

        @Override
        public void scrollToRow(int row, ScrollDestination destination) {
            Scheduler.get().scheduleDeferred(() -> {
                grid.scrollToRow(row, destination);
                // Add details refresh listener and handle possible detail
                // for scrolled row.
                addDetailsRefreshCallback(() -> {
                    if (rowHasDetails(row)) {
                        grid.scrollToRow(row, destination);
                    }
                });
            });
        }

        @Override
        public void scrollToStart() {
            Scheduler.get().scheduleDeferred(() -> grid.scrollToStart());
        }

        @Override
        public void scrollToEnd() {
            Scheduler.get().scheduleDeferred(() -> {
                grid.scrollToEnd();
                addDetailsRefreshCallback(() -> {
                    if (rowHasDetails(grid.getDataSource().size() - 1)) {
                        grid.scrollToEnd();
                    }
                });
            });
        }

        @Override
        public void recalculateColumnWidths() {
            if (recalculateScheduled) {
                return;
            }

            // Must be scheduled so that possible refreshAll has time to clear
            // the cache.
            recalculateScheduled = true;
            Scheduler.get().scheduleFinally(() -> {
                // If cache has been cleared, wait for data to become available.
                // Don't trigger another attempt if there is already a handler
                // waiting, that one will trigger the call when calculations are
                // possible and clear out the registration afterwards.
                if (((AbstractRemoteDataSource<JsonObject>) getDataSource())
                        .getCachedRange().length() == 0
                        && getDataSource().size() > 0) {
                    if (dataAvailableHandlerRegistration == null) {
                        dataAvailableHandlerRegistration = grid
                                .addDataAvailableHandler(
                                        new DataAvailableHandler() {

                                            @Override
                                            public void onDataAvailable(
                                                    DataAvailableEvent event) {
                                                if (event.getAvailableRows()
                                                        .length() == 0
                                                        && getDataSource()
                                                                .size() > 0) {
                                                    // Cache not populated yet,
                                                    // wait for next call.
                                                    return;
                                                }
                                                grid.recalculateColumnWidths();
                                                if (dataAvailableHandlerRegistration != null) {
                                                    dataAvailableHandlerRegistration
                                                            .removeHandler();
                                                    dataAvailableHandlerRegistration = null;
                                                }
                                            }
                                        });
                    }
                } else if (dataAvailableHandlerRegistration == null) {
                    grid.recalculateColumnWidths();
                }
                recalculateScheduled = false;
            });
        }
    }

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
            int rowIndex = cell.getRowIndex();
            getRpcProxy(GridServerRpc.class).itemClick(rowKey, columnId,
                    MouseEventDetailsBuilder.buildMouseEventDetails(mouseEvent),
                    rowIndex);
        }
    }

    /* Map to keep track of all added columns */
    private Map<CustomColumn, String> columnToIdMap = new HashMap<>();
    private Map<String, CustomColumn> idToColumn = new HashMap<>();

    /* Child component list for HasComponentsConnector */
    private List<ComponentConnector> childComponents;
    private ItemClickHandler itemClickHandler = new ItemClickHandler();
    private boolean rowHeightScheduled = false;

    /**
     * Gets the string identifier of the given column in this grid.
     *
     * @param column
     *            the column whose id to get
     * @return the string id of the column
     */
    public String getColumnId(Column<?, ?> column) {
        return columnToIdMap.get(column);
    }

    /**
     * Gets the column corresponding to the given string identifier.
     *
     * @param columnId
     *            the id of the column to get
     * @return the column with the given id
     */
    public CustomColumn getColumn(String columnId) {
        return idToColumn.get(columnId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Grid<JsonObject> getWidget() {
        return (Grid<JsonObject>) super.getWidget();
    }

    /**
     * Method called for a row details refresh. Runs all callbacks if any
     * details were shown and clears the callbacks.
     *
     * @param detailsShown
     *            True if any details were set visible
     */
    protected void detailsRefreshed(boolean detailsShown) {
        if (detailsShown) {
            refreshDetailsCallbacks.forEach(Runnable::run);
        }
        refreshDetailsCallbacks.clear();
    }

    /**
     * Method target for when one single details has been updated and we might
     * need to scroll it into view.
     *
     * @param rowIndex
     *            index of updated row
     */
    protected void singleDetailsOpened(int rowIndex) {
        addDetailsRefreshCallback(() -> {
            if (rowHasDetails(rowIndex)) {
                getWidget().scrollToRow(rowIndex);
            }
        });
    }

    /**
     * Add a single use details runnable callback for when we get a call to
     * {@link #detailsRefreshed(boolean)}.
     *
     * @param refreshCallback
     *            Details refreshed callback
     */
    private void addDetailsRefreshCallback(Runnable refreshCallback) {
        refreshDetailsCallbacks.add(refreshCallback);
    }

    /**
     * Check if we have details for given row.
     *
     * @param rowIndex
     * @return
     */
    private boolean rowHasDetails(int rowIndex) {
        JsonObject row = getWidget().getDataSource().getRow(rowIndex);

        return row != null && row.hasKey(GridState.JSONKEY_DETAILS_VISIBLE)
                && !row.getString(GridState.JSONKEY_DETAILS_VISIBLE).isEmpty();
    }

    @Override
    protected void init() {
        super.init();

        updateWidgetStyleNames();

        Grid<JsonObject> grid = getWidget();

        // Trigger early redraw of both grid static sections.
        grid.setHeaderVisible(!grid.isHeaderVisible());
        grid.setFooterVisible(!grid.isFooterVisible());

        registerRpc(GridClientRpc.class, new GridConnectorClientRpc(grid));

        grid.addSortHandler(this::handleSortEvent);
        grid.setRowStyleGenerator(rowRef -> {
            JsonObject json = rowRef.getRow();
            return json.hasKey(GridState.JSONKEY_ROWSTYLE)
                    ? json.getString(GridState.JSONKEY_ROWSTYLE)
                    : null;
        });
        grid.setCellStyleGenerator(cellRef -> {
            JsonObject row = cellRef.getRow();
            if (!row.hasKey(GridState.JSONKEY_CELLSTYLES)) {
                return null;
            }

            Column<?, JsonObject> column = cellRef.getColumn();
            if (column instanceof CustomColumn) {
                String id = ((CustomColumn) column).getConnectorId();
                JsonObject cellStyles = row
                        .getObject(GridState.JSONKEY_CELLSTYLES);
                if (cellStyles.hasKey(id)) {
                    return cellStyles.getString(id);
                }
            }

            return null;
        });

        grid.addColumnVisibilityChangeHandler(event -> {
            if (event.isUserOriginated()) {
                getRpcProxy(GridServerRpc.class).columnVisibilityChanged(
                        getColumnId(event.getColumn()), event.isHidden());
            }
        });
        grid.addColumnReorderHandler(event -> {
            if (event.isUserOriginated()) {
                List<String> newColumnOrder = mapColumnsToIds(
                        event.getNewColumnOrder());
                List<String> oldColumnOrder = mapColumnsToIds(
                        event.getOldColumnOrder());
                getRpcProxy(GridServerRpc.class)
                        .columnsReordered(newColumnOrder, oldColumnOrder);
            }
        });
        grid.addColumnResizeHandler(event -> {
            Column<?, JsonObject> column = event.getColumn();
            getRpcProxy(GridServerRpc.class).columnResized(getColumnId(column),
                    column.getWidthActual());
        });

        // Handling row height changes
        grid.addRowHeightChangedHandler(event -> {
            getLayoutManager().setNeedsMeasureRecursively(GridConnector.this);
            getLayoutManager().layoutNow();
        });

        // Handling Escalator size changes
        grid.getEscalator().addEscalatorSizeChangeHandler(event -> {
            getLayoutManager().setNeedsMeasure(GridConnector.this);
            if (!getConnection().getMessageHandler().isUpdatingState()
                    && !getLayoutManager().isLayoutRunning()) {
                getLayoutManager().layoutNow();
            }
        });

        /* Item click events */
        grid.addBodyClickHandler(itemClickHandler);
        grid.addBodyDoubleClickHandler(itemClickHandler);

        layout();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        if (!getState().columnOrder.containsAll(idToColumn.keySet())) {
            updateColumns();
        } else if (stateChangeEvent.hasPropertyChanged("columnOrder")) {
            updateColumnOrder();
        }

        if (stateChangeEvent.hasPropertyChanged("header")) {
            updateHeader();
        }
        if (stateChangeEvent.hasPropertyChanged("footer")) {
            updateFooter();
        }
    }

    void updateColumnOrder() {
        getWidget().setColumnOrder(getState().columnOrder.stream()
                .map(this::getColumn).toArray(size -> new CustomColumn[size]));
    }

    @OnStateChange("columnResizeMode")
    void updateColumnResizeMode() {
        getWidget().setColumnResizeMode(getState().columnResizeMode);
    }

    /**
     * Updates the grid header section on state change.
     */
    void updateHeader() {
        final Grid<JsonObject> grid = getWidget();
        final SectionState state = getState().header;

        while (grid.getHeaderRowCount() > 0) {
            grid.removeHeaderRow(0);
        }

        for (RowState rowState : state.rows) {
            HeaderRow row = grid.appendHeaderRow();

            if (rowState.defaultHeader) {
                grid.setDefaultHeaderRow(row);
            }

            updateStaticRow(rowState, row);
        }

        grid.setHeaderVisible(state.visible);
    }

    @OnStateChange({ "bodyRowHeight", "headerRowHeight", "footerRowHeight" })
    void updateRowHeight() {
        if (rowHeightScheduled) {
            return;
        }

        Scheduler.get().scheduleFinally(() -> {
            GridState state = getState();
            Grid<JsonObject> grid = getWidget();
            if (grid.isAttached() && rowHeightNeedsReset()) {
                grid.resetSizesFromDom();
            }
            updateContainerRowHeigth(grid.getEscalator().getBody(),
                    state.bodyRowHeight);
            updateContainerRowHeigth(grid.getEscalator().getHeader(),
                    state.headerRowHeight);
            updateContainerRowHeigth(grid.getEscalator().getFooter(),
                    state.footerRowHeight);
            rowHeightScheduled = false;
        });

        rowHeightScheduled = true;
    }

    private boolean rowHeightNeedsReset() {
        GridState state = getState();
        // Body
        boolean bodyAutoCalc = state.bodyRowHeight < 0;

        // Header
        boolean headerAutoCalc = state.headerRowHeight < 0;
        boolean headerReset = headerAutoCalc && hasVisibleContent(state.header);

        // Footer
        boolean footerAutoCalc = state.footerRowHeight < 0;
        boolean footerReset = footerAutoCalc && hasVisibleContent(state.footer);

        return bodyAutoCalc || headerReset || footerReset;
    }

    private boolean hasVisibleContent(SectionState state) {
        return state.visible && !state.rows.isEmpty();
    }

    private void updateContainerRowHeigth(RowContainer container,
            double height) {
        if (height >= 0) {
            container.setDefaultRowHeight(height);
        }
    }

    private void updateStaticRow(RowState rowState,
            Grid.StaticSection.StaticRow row) {
        rowState.cells
                .forEach((columnId, cellState) -> updateStaticCellFromState(
                        row.getCell(getColumn(columnId)), cellState));
        for (Map.Entry<CellState, Set<String>> cellGroupEntry : rowState.cellGroups
                .entrySet()) {
            Set<String> group = cellGroupEntry.getValue();

            Grid.Column<?, ?>[] columns = group.stream().map(idToColumn::get)
                    .toArray(size -> new Grid.Column<?, ?>[size]);
            // Set state to be the same as first in group.
            updateStaticCellFromState(row.join(columns),
                    cellGroupEntry.getKey());
        }
        row.setStyleName(rowState.styleName);
    }

    private void updateStaticCellFromState(Grid.StaticSection.StaticCell cell,
            CellState cellState) {
        switch (cellState.type) {
        case TEXT:
            cell.setText(cellState.text);
            break;
        case HTML:
            cell.setHtml(cellState.html);
            break;
        case WIDGET:
            ComponentConnector connector = (ComponentConnector) cellState.connector;
            if (connector != null) {
                cell.setWidget(connector.getWidget());
            } else {
                // This happens if you do setVisible(false) on the component on
                // the server side
                cell.setWidget(null);
            }
            break;
        default:
            throw new IllegalStateException(
                    "unexpected cell type: " + cellState.type);
        }
        cell.setStyleName(cellState.styleName);
        cell.setDescription(cellState.description);
        cell.setDescriptionContentMode(cellState.descriptionContentMode);
    }

    /**
     * Updates the grid footer section on state change.
     */
    void updateFooter() {
        final Grid<JsonObject> grid = getWidget();
        final SectionState state = getState().footer;

        while (grid.getFooterRowCount() > 0) {
            grid.removeFooterRow(0);
        }

        for (RowState rowState : state.rows) {
            FooterRow row = grid.appendFooterRow();

            updateStaticRow(rowState, row);
        }

        grid.setFooterVisible(state.visible);
    }

    @OnStateChange({ "sortColumns", "sortDirs" })
    void updateSortOrder() {
        List<SortOrder> sortOrder = new ArrayList<SortOrder>();

        String[] sortColumns = getState().sortColumns;
        SortDirection[] sortDirs = getState().sortDirs;

        for (int i = 0; i < sortColumns.length; i++) {
            sortOrder
                    .add(new SortOrder(getColumn(sortColumns[i]), sortDirs[i]));
        }

        getWidget().setSortOrder(sortOrder);
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        super.setDataSource(dataSource);
        getWidget().setDataSource(dataSource);
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
    public void addColumn(CustomColumn column, String id) {
        assert !columnToIdMap.containsKey(column) && !columnToIdMap
                .containsValue(id) : "Column with given id already exists.";
        columnToIdMap.put(column, id);
        idToColumn.put(id, column);

        if (idToColumn.keySet().containsAll(getState().columnOrder)) {
            // All columns are available.
            updateColumns();
        }
    }

    /**
     * Updates the widgets columns to match the map in this connector.
     */
    protected void updateColumns() {
        List<Column<?, JsonObject>> currentColumns = getWidget().getColumns();

        List<CustomColumn> columnOrder = getState().columnOrder.stream()
                .map(this::getColumn).collect(Collectors.toList());

        if (isColumnOrderCorrect(currentColumns, columnOrder)) {
            // All up to date
            return;
        }

        Grid<JsonObject> grid = getWidget();

        // Remove old column
        currentColumns.stream()
                .filter(col -> !(columnOrder.contains(col)
                        || col instanceof SelectionColumn))
                .forEach(grid::removeColumn);

        // Add new columns
        grid.addColumns(columnOrder.stream()
                .filter(col -> !currentColumns.contains(col))
                .toArray(CustomColumn[]::new));

        // Make sure order is correct.
        grid.setColumnOrder(
                columnOrder.toArray(new CustomColumn[columnOrder.size()]));
    }

    private boolean isColumnOrderCorrect(List<Column<?, JsonObject>> current,
            List<CustomColumn> order) {
        List<Column<?, JsonObject>> columnsToCompare = current;
        if (current.size() > 0 && current.get(0) instanceof SelectionColumn) {
            // Remove selection column.
            columnsToCompare = current.subList(1, current.size());
        }
        return columnsToCompare.equals(order);
    }

    /**
     * Removes the given column from mappings in this Connector.
     *
     * @param column
     *            column to remove from the mapping
     */
    public void removeColumnMapping(CustomColumn column) {
        assert columnToIdMap
                .containsKey(column) : "Given Column does not exist.";

        // Remove mapping. Columns are removed from Grid when state changes.
        String id = columnToIdMap.remove(column);
        idToColumn.remove(id);
    }

    /**
     * Method called by {@code CustomColumn} when its renderer changes. This
     * method is used to maintain hierarchical renderer wrap in
     * {@code TreeGrid}.
     *
     * @param column
     *            the column which now has a new renderer
     *
     * @since 8.1
     */
    public void onColumnRendererChanged(CustomColumn column) {
        // NO-OP
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
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
        String[] colArray = columnIds.toArray(new String[0]);
        SortDirection[] dirArray = sortDirections.toArray(new SortDirection[0]);

        if (!Arrays.equals(colArray, getState().sortColumns)
                || !Arrays.equals(dirArray, getState().sortDirs)) {
            // State has actually changed, send to server
            getRpcProxy(GridServerRpc.class).sort(colArray, dirArray,
                    event.isUserOriginated());
        }
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
            TooltipInfo tooltip = getHeaderFooterTooltip(cell);
            if (tooltip != null) {
                return tooltip;
            }
            if (row != null && (row.hasKey(GridState.JSONKEY_ROWDESCRIPTION)
                    || row.hasKey(GridState.JSONKEY_CELLDESCRIPTION))) {

                Column<?, JsonObject> column = cell.getColumn();
                if (column instanceof CustomColumn) {
                    JsonObject cellDescriptions = row
                            .getObject(GridState.JSONKEY_CELLDESCRIPTION);

                    String id = ((CustomColumn) column).getConnectorId();

                    if (cellDescriptions != null
                            && cellDescriptions.hasKey(id)) {
                        return new TooltipInfo(cellDescriptions.getString(id),
                                ((CustomColumn) column)
                                        .getTooltipContentMode());
                    } else if (row.hasKey(GridState.JSONKEY_ROWDESCRIPTION)) {
                        return new TooltipInfo(
                                row.getString(GridState.JSONKEY_ROWDESCRIPTION),
                                getState().rowDescriptionContentMode);
                    }
                }
            }
        }

        if (super.hasTooltip()) {
            return super.getTooltipInfo(element);
        }
        return null;
    }

    private TooltipInfo getHeaderFooterTooltip(CellReference cell) {
        Section section = Section.BODY;
        if (cell instanceof EventCellReference) {
            // Header or footer
            section = ((EventCellReference) cell).getSection();
        }
        StaticCell staticCell = null;
        if (section == Section.HEADER) {
            staticCell = getWidget().getHeaderRow(cell.getRowIndex())
                    .getCell(cell.getColumn());
        } else if (section == Section.FOOTER) {
            staticCell = getWidget().getFooterRow(cell.getRowIndex())
                    .getCell(cell.getColumn());
        }
        if (staticCell != null && staticCell.getDescription() != null) {
            return new TooltipInfo(staticCell.getDescription(),
                    staticCell.getDescriptionContentMode());
        }

        return null;
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

    private List<String> mapColumnsToIds(List<Column<?, JsonObject>> columns) {
        return columns.stream().map(this::getColumnId).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
