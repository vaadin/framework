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
package com.vaadin.client.ui.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.SubPartAware;
import com.vaadin.client.ui.grid.renderers.ComplexRenderer;
import com.vaadin.client.ui.grid.renderers.TextRenderer;
import com.vaadin.client.ui.grid.selection.MultiSelectionRenderer;
import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.util.SharedUtil;

/**
 * A data grid view that supports columns and lazy loading of data rows from a
 * data source.
 * 
 * <h3>Columns</h3>
 * <p>
 * The {@link GridColumn} class defines the renderer used to render a cell in
 * the grid. Implement {@link GridColumn#getValue(Object)} to retrieve the cell
 * value from the row object and return the cell renderer to render that cell.
 * </p>
 * <p>
 * {@link GridColumn}s contain other properties like the width of the column and
 * the visiblity of the column. If you want to change a column's properties
 * after it has been added to the grid you can get a column object for a
 * specific column index using {@link Grid#getColumn(int)}.
 * </p>
 * <p>
 * 
 * TODO Explain about headers/footers once the multiple header/footer api has
 * been implemented
 * 
 * <h3>Data sources</h3>
 * <p>
 * TODO Explain about what a data source is and how it should be implemented.
 * </p>
 * 
 * @param <T>
 *            The row type of the grid. The row type is the POJO type from where
 *            the data is retrieved into the column cells.
 * @since 7.4
 * @author Vaadin Ltd
 */
public class Grid<T> extends Composite implements SubPartAware {

    private class SelectionColumn extends GridColumn<Boolean, T> {
        private boolean initDone = false;

        public SelectionColumn(final Renderer<Boolean> selectColumnRenderer) {
            super(selectColumnRenderer);

            setHeaderRenderer(new Renderer<String>() {
                @Override
                public void render(FlyweightCell cell, String data) {
                    if (cell.getRow() == escalator.getHeader().getRowCount() - 1) {
                        // TODO: header "select all / select none" logic
                        selectColumnRenderer.render(cell, Boolean.FALSE);
                    }
                }
            });
        }

        public void initDone() {
            initDone = true;
        }

        @Override
        public void setFooterCaption(String caption) {
            if (initDone) {
                throw new UnsupportedOperationException(
                        "The selection column is read only");
            } else {
                super.setFooterCaption(caption);
            }
        }

        @Override
        public void setFooterRenderer(Renderer<String> renderer) {
            if (initDone) {
                throw new UnsupportedOperationException(
                        "The selection column is read only");
            } else {
                super.setFooterRenderer(renderer);
            }
        }

        @Override
        public void setHeaderCaption(String caption) {
            if (initDone) {
                throw new UnsupportedOperationException(
                        "The selection column is read only");
            } else {
                super.setHeaderCaption(caption);
            }
        }

        @Override
        public void setHeaderRenderer(Renderer<String> renderer) {
            if (initDone) {
                throw new UnsupportedOperationException(
                        "The selection column is read only");
            } else {
                super.setHeaderRenderer(renderer);
            }
        }

        @Override
        public void setVisible(boolean visible) {
            if (initDone) {
                throw new UnsupportedOperationException(
                        "The selection column is read only");
            } else {
                super.setVisible(visible);
            }
        }

        @Override
        public void setWidth(int pixels) {
            if (initDone) {
                throw new UnsupportedOperationException(
                        "The selection column is read only");
            } else {
                super.setWidth(pixels);
            }
        }

        @Override
        public Boolean getValue(T row) {
            return Boolean.valueOf(isSelected(row));
        }
    }

    /**
     * Escalator used internally by grid to render the rows
     */
    private Escalator escalator = GWT.create(Escalator.class);

    /**
     * List of columns in the grid. Order defines the visible order.
     */
    private final List<GridColumn<?, T>> columns = new ArrayList<GridColumn<?, T>>();

    /**
     * The datasource currently in use. <em>Note:</em> it is <code>null</code>
     * on initialization, but not after that.
     */
    private DataSource<T> dataSource;

    /**
     * The column groups rows added to the grid
     */
    private final List<ColumnGroupRow<T>> columnGroupRows = new ArrayList<ColumnGroupRow<T>>();

    /**
     * Are the headers for the columns visible
     */
    private boolean columnHeadersVisible = true;

    /**
     * Are the footers for the columns visible
     */
    private boolean columnFootersVisible = false;

    /**
     * The last column frozen counter from the left
     */
    private GridColumn<?, T> lastFrozenColumn;

    private Renderer<Boolean> selectColumnRenderer = null;

    private SelectionColumn selectionColumn;

    /**
     * Base class for grid columns internally used by the Grid. The user should
     * use {@link GridColumn} when creating new columns.
     * 
     * @param <C>
     *            the column type
     * 
     * @param <T>
     *            the row type
     */
    static abstract class AbstractGridColumn<C, T> implements HasVisibility {

        /**
         * The grid the column is associated with
         */
        private Grid<T> grid;

        /**
         * Should the column be visible in the grid
         */
        private boolean visible = true;

        /**
         * The text displayed in the header of the column
         */
        private String header;

        /**
         * Text displayed in the column footer
         */
        private String footer;

        /**
         * Width of column in pixels
         */
        private int width = 100;

        /**
         * Renderer for rendering a value into the cell
         */
        private Renderer<? super C> bodyRenderer;

        /**
         * Renderer for rendering the header cell value into the cell
         */
        private Renderer<String> headerRenderer = new TextRenderer();

        /**
         * Renderer for rendering the footer cell value into the cell
         */
        private Renderer<String> footerRenderer = new TextRenderer();

        /**
         * Constructs a new column with a custom renderer.
         * 
         * @param renderer
         *            The renderer to use for rendering the cells
         */
        public AbstractGridColumn(Renderer<? super C> renderer) {
            if (renderer == null) {
                throw new IllegalArgumentException("Renderer cannot be null.");
            }
            this.bodyRenderer = renderer;
        }

        /**
         * Constructs a new column with custom renderers for rows, header and
         * footer cells.
         * 
         * @param bodyRenderer
         *            The renderer to use for rendering body cells
         * @param headerRenderer
         *            The renderer to use for rendering header cells
         * @param footerRenderer
         *            The renderer to use for rendering footer cells
         */
        public AbstractGridColumn(Renderer<C> bodyRenderer,
                Renderer<String> headerRenderer, Renderer<String> footerRenderer) {
            this(bodyRenderer);
            if (headerRenderer == null || footerRenderer == null) {
                throw new IllegalArgumentException("Renderer cannot be null.");
            }

            this.headerRenderer = headerRenderer;
            this.footerRenderer = footerRenderer;
        }

        /**
         * Internally used by the grid to set itself
         * 
         * @param grid
         */
        private void setGrid(Grid<T> grid) {
            if (this.grid != null && grid != null) {
                // Trying to replace grid
                throw new IllegalStateException(
                        "Column already is attached to grid. Remove the column first from the grid and then add it.");
            }

            this.grid = grid;

            if (grid != null) {
                setVisible(this.visible);
                setWidth(this.width);
                setHeaderCaption(this.header);
                setFooterCaption(this.footer);
            }
        }

        /**
         * Gets text in the header of the column. By default the header caption
         * is empty.
         * 
         * @return the text displayed in the column caption
         */
        public String getHeaderCaption() {
            return header;
        }

        /**
         * Returns the renderer used for rendering the header cells
         * 
         * @return a renderer that renders header cells
         */
        public Renderer<String> getHeaderRenderer() {
            return headerRenderer;
        }

        /**
         * Sets the renderer that renders header cells. Should not be null.
         * 
         * @param renderer
         *            The renderer to use for rendering header cells.
         */
        public void setHeaderRenderer(Renderer<String> renderer) {
            if (renderer == null) {
                throw new IllegalArgumentException("Renderer cannot be null.");
            }
            headerRenderer = renderer;
            if (grid != null) {
                grid.refreshHeader();
            }
        }

        /**
         * Returns the renderer used for rendering the footer cells
         * 
         * @return a renderer that renders footer cells
         */
        public Renderer<String> getFooterRenderer() {
            return footerRenderer;
        }

        /**
         * Sets the renderer that renders footer cells. Should not be null.
         * 
         * @param renderer
         *            The renderer to use for rendering footer cells.
         */
        public void setFooterRenderer(Renderer<String> renderer) {
            if (renderer == null) {
                throw new IllegalArgumentException("Renderer cannot be null.");
            }
            footerRenderer = renderer;
            if (grid != null) {
                grid.refreshFooter();
            }
        }

        /**
         * Sets the text in the header of the column.
         * 
         * @param caption
         *            the text displayed in the column header
         */
        public void setHeaderCaption(String caption) {
            if (SharedUtil.equals(caption, header)) {
                return;
            }

            header = caption;

            if (grid != null) {
                grid.refreshHeader();
            }
        }

        /**
         * Gets text in the footer of the column. By default the footer caption
         * is empty.
         * 
         * @return The text displayed in the footer of the column
         */
        public String getFooterCaption() {
            return footer;
        }

        /**
         * Sets text in the footer of the column.
         * 
         * @param caption
         *            the text displayed in the footer of the column
         */
        public void setFooterCaption(String caption) {
            if (SharedUtil.equals(caption, footer)) {
                return;
            }

            footer = caption;

            if (grid != null) {
                grid.refreshFooter();
            }
        }

        /**
         * Is the column visible. By default all columns are visible.
         * 
         * @return <code>true</code> if the column is visible
         */
        @Override
        public boolean isVisible() {
            return visible;
        }

        /**
         * Sets a column as visible in the grid.
         * 
         * @param visible
         *            <code>true</code> if the column should be displayed in the
         *            grid
         */
        @Override
        public void setVisible(boolean visible) {
            if (this.visible == visible) {
                return;
            }

            this.visible = visible;

            // Remove column
            if (grid != null) {
                int index = findIndexOfColumn();
                ColumnConfiguration conf = grid.escalator
                        .getColumnConfiguration();

                if (visible) {
                    conf.insertColumns(index, 1);
                } else {
                    conf.removeColumns(index, 1);
                }
            }

        }

        /**
         * Returns the data that should be rendered into the cell. By default
         * returning Strings and Widgets are supported. If the return type is a
         * String then it will be treated as preformatted text.
         * <p>
         * To support other types you will need to pass a custom renderer to the
         * column via the column constructor.
         * 
         * @param row
         *            The row object that provides the cell content.
         * 
         * @return The cell content
         */
        public abstract C getValue(T row);

        /**
         * The renderer to render the cell width. By default renders the data as
         * a String or adds the widget into the cell if the column type is of
         * widget type.
         * 
         * @return The renderer to render the cell content with
         */
        public Renderer<? super C> getRenderer() {
            return bodyRenderer;
        }

        /**
         * Finds the index of this column instance
         * 
         */
        private int findIndexOfColumn() {
            return grid.findVisibleColumnIndex((GridColumn<?, T>) this);
        }

        /**
         * Sets the pixel width of the column. Use a negative value for the grid
         * to autosize column based on content and available space
         * 
         * @param pixels
         *            the width in pixels or negative for auto sizing
         */
        public void setWidth(int pixels) {
            this.width = pixels;

            if (grid != null && isVisible()) {
                int index = findIndexOfColumn();
                ColumnConfiguration conf = grid.escalator
                        .getColumnConfiguration();
                conf.setColumnWidth(index, pixels);
            }
        }

        /**
         * Returns the pixel width of the column
         * 
         * @return pixel width of the column
         */
        public int getWidth() {
            if (grid == null) {
                return this.width;
            } else {
                int index = findIndexOfColumn();
                ColumnConfiguration conf = grid.escalator
                        .getColumnConfiguration();
                return conf.getColumnWidth(index);
            }
        }
    }

    /**
     * Base class for header / footer escalator updater
     */
    protected abstract class HeaderFooterEscalatorUpdater implements
            EscalatorUpdater {

        /**
         * The row container which contains the header or footer rows
         */
        private RowContainer rows;

        /**
         * Should the index be counted from 0-> or 0<-
         */
        private boolean inverted;

        /**
         * Constructs an updater for updating a header / footer
         * 
         * @param rows
         *            The row container
         * @param inverted
         *            Should index counting be inverted
         */
        public HeaderFooterEscalatorUpdater(RowContainer rows, boolean inverted) {
            this.rows = rows;
            this.inverted = inverted;
        }

        /**
         * Gets the header/footer caption value
         * 
         * @param column
         *            The column to get the value for.
         * 
         * @return The value that should be rendered for the column caption
         */
        public abstract String getColumnValue(GridColumn<?, T> column);

        /**
         * Gets the group caption value
         * 
         * @param group
         *            The group for with the caption value should be returned
         * @return The value that should be rendered for the column caption
         */
        public abstract String getGroupValue(ColumnGroup<T> group);

        /**
         * Is the row visible in the header/footer
         * 
         * @param row
         *            the row to check
         * 
         * @return <code>true</code> if the row should be visible
         */
        public abstract boolean isRowVisible(ColumnGroupRow<T> row);

        /**
         * Should the first row be visible
         * 
         * @return <code>true</code> if the first row should be visible
         */
        public abstract boolean firstRowIsVisible();

        /**
         * The renderer that renders the cell
         * 
         * @param column
         *            The column for which the cell should be rendered
         * 
         * @return renderer used for rendering
         */
        public abstract Renderer<String> getRenderer(GridColumn<?, T> column);

        /**
         * The renderer that renders the cell for column groups
         * 
         * @param group
         *            The group that should be rendered
         * @return renderer used for rendering
         */
        public abstract Renderer<String> getGroupRenderer(ColumnGroup<T> group);

        @Override
        public void update(Row row, Iterable<FlyweightCell> cellsToUpdate) {

            int rowIndex;
            if (inverted) {
                rowIndex = rows.getRowCount() - row.getRow() - 1;
            } else {
                rowIndex = row.getRow();
            }

            if (firstRowIsVisible() && rowIndex == 0) {
                // column headers
                for (FlyweightCell cell : cellsToUpdate) {
                    GridColumn<?, T> column = getColumnFromVisibleIndex(cell
                            .getColumn());
                    if (column != null) {
                        getRenderer(column)
                                .render(cell, getColumnValue(column));
                    }
                }

            } else if (columnGroupRows.size() > 0) {
                // Adjust for headers
                if (firstRowIsVisible()) {
                    rowIndex--;
                }

                // Adjust for previous invisible header rows
                ColumnGroupRow<T> groupRow = null;
                for (int i = 0, realIndex = 0; i < columnGroupRows.size(); i++) {
                    groupRow = columnGroupRows.get(i);
                    if (isRowVisible(groupRow)) {
                        if (realIndex == rowIndex) {
                            rowIndex = realIndex;
                            break;
                        }
                        realIndex++;
                    }
                }

                assert groupRow != null;

                for (FlyweightCell cell : cellsToUpdate) {
                    GridColumn<?, T> column = getColumnFromVisibleIndex(cell
                            .getColumn());
                    ColumnGroup<T> group = getGroupForColumn(groupRow, column);
                    Element cellElement = cell.getElement();

                    if (group != null) {
                        getGroupRenderer(group).render(cell,
                                getGroupValue(group));
                        cell.setColSpan(group.getColumns().size());
                    } else {
                        // Cells are reused
                        cellElement.setInnerHTML(null);
                        cell.setColSpan(1);
                    }
                }
            }
        }

        @Override
        public void preAttach(Row row, Iterable<FlyweightCell> cellsToAttach) {
        }

        @Override
        public void postAttach(Row row, Iterable<FlyweightCell> attachedCells) {
        }

        @Override
        public void preDetach(Row row, Iterable<FlyweightCell> cellsToDetach) {
        }

        @Override
        public void postDetach(Row row, Iterable<FlyweightCell> detachedCells) {
        }
    }

    /**
     * Creates a new instance.
     */
    public Grid() {
        initWidget(escalator);

        setStylePrimaryName("v-grid");

        escalator.getHeader().setEscalatorUpdater(createHeaderUpdater());
        escalator.getBody().setEscalatorUpdater(createBodyUpdater());
        escalator.getFooter().setEscalatorUpdater(createFooterUpdater());

        refreshHeader();
        refreshFooter();

        escalator
                .addRowVisibilityChangeHandler(new RowVisibilityChangeHandler() {
                    @Override
                    public void onRowVisibilityChange(
                            RowVisibilityChangeEvent event) {
                        if (dataSource != null) {
                            dataSource.ensureAvailability(
                                    event.getFirstVisibleRow(),
                                    event.getVisibleRowCount());
                        }
                    }
                });

    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        escalator.setStylePrimaryName(style);

    }

    /**
     * Creates the header updater that updates the escalator header rows from
     * the column and column group rows.
     * 
     * @return the updater that updates the data in the escalator.
     */
    private EscalatorUpdater createHeaderUpdater() {
        return new HeaderFooterEscalatorUpdater(escalator.getHeader(), true) {

            @Override
            public boolean isRowVisible(ColumnGroupRow<T> row) {
                return row.isHeaderVisible();
            }

            @Override
            public String getGroupValue(ColumnGroup<T> group) {
                return group.getHeaderCaption();
            }

            @Override
            public String getColumnValue(GridColumn<?, T> column) {
                return column.getHeaderCaption();
            }

            @Override
            public boolean firstRowIsVisible() {
                return isColumnHeadersVisible();
            }

            @Override
            public Renderer<String> getRenderer(GridColumn<?, T> column) {
                return column.getHeaderRenderer();
            }

            @Override
            public Renderer<String> getGroupRenderer(ColumnGroup<T> group) {
                return group.getHeaderRenderer();
            }
        };
    }

    private EscalatorUpdater createBodyUpdater() {
        return new EscalatorUpdater() {

            @Override
            public void update(Row row, Iterable<FlyweightCell> cellsToUpdate) {
                int rowIndex = row.getRow();
                if (dataSource == null) {
                    setCellsLoading(cellsToUpdate);
                    return;
                }

                T rowData = dataSource.getRow(rowIndex);
                if (rowData == null) {
                    setCellsLoading(cellsToUpdate);
                    return;
                }

                for (FlyweightCell cell : cellsToUpdate) {
                    GridColumn column = getColumnFromVisibleIndex(cell
                            .getColumn());
                    if (column != null) {
                        Object value = column.getValue(rowData);
                        column.getRenderer().render(cell, value);
                    }
                }
            }

            private void setCellsLoading(Iterable<FlyweightCell> cellsToUpdate) {
                for (FlyweightCell cell : cellsToUpdate) {
                    cell.getElement().setInnerText("...");
                }
            }

            @Override
            public void preAttach(Row row, Iterable<FlyweightCell> cellsToAttach) {
                // NOOP for now
            }

            @Override
            public void postAttach(Row row,
                    Iterable<FlyweightCell> attachedCells) {
                // NOOP for now
            }

            @Override
            public void preDetach(Row row, Iterable<FlyweightCell> cellsToDetach) {
                // NOOP for now
            }

            @Override
            public void postDetach(Row row,
                    Iterable<FlyweightCell> detachedCells) {
                // NOOP for now
            }
        };
    }

    /**
     * Creates the footer updater that updates the escalator footer rows from
     * the column and column group rows.
     * 
     * @return the updater that updates the data in the escalator.
     */
    private EscalatorUpdater createFooterUpdater() {
        return new HeaderFooterEscalatorUpdater(escalator.getFooter(), false) {

            @Override
            public boolean isRowVisible(ColumnGroupRow<T> row) {
                return row.isFooterVisible();
            }

            @Override
            public String getGroupValue(ColumnGroup<T> group) {
                return group.getFooterCaption();
            }

            @Override
            public String getColumnValue(GridColumn<?, T> column) {
                return column.getFooterCaption();
            }

            @Override
            public boolean firstRowIsVisible() {
                return isColumnFootersVisible();
            }

            @Override
            public Renderer<String> getRenderer(GridColumn<?, T> column) {
                return column.getFooterRenderer();
            }

            @Override
            public Renderer<String> getGroupRenderer(ColumnGroup<T> group) {
                return group.getFooterRenderer();
            }
        };
    }

    /**
     * Refreshes header or footer rows on demand
     * 
     * @param rows
     *            The row container
     * @param firstRowIsVisible
     *            is the first row visible
     * @param isHeader
     *            <code>true</code> if we refreshing the header, else assumed
     *            the footer
     */
    private void refreshRowContainer(RowContainer rows,
            boolean firstRowIsVisible, boolean isHeader) {

        // Count needed rows
        int totalRows = firstRowIsVisible ? 1 : 0;
        for (ColumnGroupRow<T> row : columnGroupRows) {
            if (isHeader ? row.isHeaderVisible() : row.isFooterVisible()) {
                totalRows++;
            }
        }

        // Add or Remove rows on demand
        int rowDiff = totalRows - rows.getRowCount();
        if (rowDiff > 0) {
            rows.insertRows(0, rowDiff);
        } else if (rowDiff < 0) {
            rows.removeRows(0, -rowDiff);
        }

        // Refresh all the rows
        if (rows.getRowCount() > 0) {
            rows.refreshRows(0, rows.getRowCount());
        }
    }

    /**
     * Refreshes all header rows
     */
    void refreshHeader() {
        refreshRowContainer(escalator.getHeader(), isColumnHeadersVisible(),
                true);
    }

    /**
     * Refreshes all footer rows
     */
    void refreshFooter() {
        refreshRowContainer(escalator.getFooter(), isColumnFootersVisible(),
                false);
    }

    /**
     * Adds a column as the last column in the grid.
     * 
     * @param column
     *            the column to add
     */
    public void addColumn(GridColumn<?, T> column) {
        addColumn(column, getColumnCount());
    }

    /**
     * Inserts a column into a specific position in the grid.
     * 
     * @param index
     *            the index where the column should be inserted into
     * @param column
     *            the column to add
     * @throws IllegalStateException
     *             if Grid's current selection model renders a selection column,
     *             and {@code index} is 0.
     */
    public void addColumn(GridColumn<?, T> column, int index) {
        if (column == selectionColumn) {
            throw new IllegalArgumentException("The selection column many "
                    + "not be added manually");
        } else if (selectionColumn != null && index == 0) {
            throw new IllegalStateException("A column cannot be inserted "
                    + "before the selection column");
        }

        addColumnSkipSelectionColumnCheck(column, index);
    }

    private void addColumnSkipSelectionColumnCheck(GridColumn<?, T> column,
            int index) {
        // Register column with grid
        columns.add(index, column);

        // Insert column into escalator
        if (column.isVisible()) {
            int visibleIndex = findVisibleColumnIndex(column);
            ColumnConfiguration conf = escalator.getColumnConfiguration();
            conf.insertColumns(visibleIndex, 1);
        }

        // Register this grid instance with the column
        ((AbstractGridColumn<?, T>) column).setGrid(this);

        if (lastFrozenColumn != null
                && ((AbstractGridColumn<?, T>) lastFrozenColumn)
                        .findIndexOfColumn() < index) {
            refreshFrozenColumns();
        }

        // Sink all renderer events
        Set<String> events = new HashSet<String>();
        events.addAll(getConsumedEventsForRenderer(column.getHeaderRenderer()));
        events.addAll(getConsumedEventsForRenderer(column.getRenderer()));
        events.addAll(getConsumedEventsForRenderer(column.getFooterRenderer()));

        sinkEvents(events);
    }

    private void sinkEvents(Collection<String> events) {
        assert events != null;

        int eventsToSink = 0;
        for (String typeName : events) {
            int typeInt = Event.getTypeInt(typeName);
            if (typeInt < 0) {
                // Type not recognized by typeInt
                sinkBitlessEvent(typeName);
            } else {
                eventsToSink |= typeInt;
            }
        }

        if (eventsToSink > 0) {
            sinkEvents(eventsToSink);
        }
    }

    private int findVisibleColumnIndex(GridColumn<?, T> column) {
        int idx = 0;
        for (GridColumn<?, T> c : columns) {
            if (c == column) {
                return idx;
            } else if (c.isVisible()) {
                idx++;
            }
        }
        return -1;
    }

    private GridColumn<?, T> getColumnFromVisibleIndex(int index) {
        int idx = -1;
        for (GridColumn<?, T> c : columns) {
            if (c.isVisible()) {
                idx++;
            }
            if (index == idx) {
                return c;
            }
        }
        return null;
    }

    /**
     * Removes a column from the grid.
     * 
     * @param column
     *            the column to remove
     */
    public void removeColumn(GridColumn<?, T> column) {
        if (column != null && column.equals(selectionColumn)) {
            throw new IllegalArgumentException(
                    "The selection column may not be removed manually.");
        }

        removeColumnSkipSelectionColumnCheck(column);
    }

    private void removeColumnSkipSelectionColumnCheck(GridColumn<?, T> column) {
        int columnIndex = columns.indexOf(column);
        int visibleIndex = findVisibleColumnIndex(column);
        columns.remove(columnIndex);

        // de-register column with grid
        ((AbstractGridColumn<?, T>) column).setGrid(null);

        if (column.isVisible()) {
            ColumnConfiguration conf = escalator.getColumnConfiguration();
            conf.removeColumns(visibleIndex, 1);
        }

        if (column.equals(lastFrozenColumn)) {
            setLastFrozenColumn(null);
        } else {
            refreshFrozenColumns();
        }
    }

    /**
     * Returns the amount of columns in the grid.
     * 
     * @return The number of columns in the grid
     */
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * Returns a list of columns in the grid.
     * 
     * @return A unmodifiable list of the columns in the grid
     */
    public List<GridColumn<?, T>> getColumns() {
        return Collections.unmodifiableList(new ArrayList<GridColumn<?, T>>(
                columns));
    }

    /**
     * Returns a column by its index in the grid.
     * 
     * @param index
     *            the index of the column
     * @return The column in the given index
     * @throws IllegalArgumentException
     *             if the column index does not exist in the grid
     */
    public GridColumn<?, T> getColumn(int index)
            throws IllegalArgumentException {
        if (index < 0 || index >= columns.size()) {
            throw new IllegalStateException("Column not found.");
        }
        return columns.get(index);
    }

    /**
     * Set the column headers visible.
     * 
     * <p>
     * A column header is a single cell header on top of each column reserved
     * for a specific header for that column. The column header can be set by
     * {@link GridColumn#setHeaderCaption(String)} and column headers cannot be
     * merged with other column headers.
     * </p>
     * 
     * <p>
     * All column headers occupy the first header row of the grid. If you do not
     * wish to show the column headers in the grid you should hide the row by
     * setting visibility of the header row to <code>false</code>.
     * </p>
     * 
     * <p>
     * If you want to merge the column headers into groups you can use
     * {@link ColumnGroupRow}s to group columns together and give them a common
     * header. See {@link #addColumnGroupRow()} for details.
     * </p>
     * 
     * <p>
     * The header row is by default visible.
     * </p>
     * 
     * @param visible
     *            <code>true</code> if header rows should be visible
     */
    public void setColumnHeadersVisible(boolean visible) {
        if (visible == isColumnHeadersVisible()) {
            return;
        }
        columnHeadersVisible = visible;
        refreshHeader();
    }

    /**
     * Are the column headers visible
     * 
     * @return <code>true</code> if they are visible
     */
    public boolean isColumnHeadersVisible() {
        return columnHeadersVisible;
    }

    /**
     * Set the column footers visible.
     * 
     * <p>
     * A column footer is a single cell footer below of each column reserved for
     * a specific footer for that column. The column footer can be set by
     * {@link GridColumn#setFooterCaption(String)} and column footers cannot be
     * merged with other column footers.
     * </p>
     * 
     * <p>
     * All column footers occupy the first footer row of the grid. If you do not
     * wish to show the column footers in the grid you should hide the row by
     * setting visibility of the footer row to <code>false</code>.
     * </p>
     * 
     * <p>
     * If you want to merge the column footers into groups you can use
     * {@link ColumnGroupRow}s to group columns together and give them a common
     * footer. See {@link #addColumnGroupRow()} for details.
     * </p>
     * 
     * <p>
     * The footer row is by default hidden.
     * </p>
     * 
     * @param visible
     *            <code>true</code> if the footer row should be visible
     */
    public void setColumnFootersVisible(boolean visible) {
        if (visible == isColumnFootersVisible()) {
            return;
        }
        this.columnFootersVisible = visible;
        refreshFooter();
    }

    /**
     * Are the column footers visible
     * 
     * @return <code>true</code> if they are visible
     * 
     */
    public boolean isColumnFootersVisible() {
        return columnFootersVisible;
    }

    /**
     * Adds a new column group row to the grid.
     * 
     * <p>
     * Column group rows are rendered in the header and footer of the grid.
     * Column group rows are made up of column groups which groups together
     * columns for adding a common auxiliary header or footer for the columns.
     * </p>
     * 
     * Example usage:
     * 
     * <pre>
     * // Add a new column group row to the grid
     * ColumnGroupRow row = grid.addColumnGroupRow();
     * 
     * // Group &quot;Column1&quot; and &quot;Column2&quot; together to form a header in the row
     * ColumnGroup column12 = row.addGroup(&quot;Column1&quot;, &quot;Column2&quot;);
     * 
     * // Set a common header for &quot;Column1&quot; and &quot;Column2&quot;
     * column12.setHeader(&quot;Column 1&amp;2&quot;);
     * 
     * // Set a common footer for &quot;Column1&quot; and &quot;Column2&quot;
     * column12.setFooter(&quot;Column 1&amp;2&quot;);
     * </pre>
     * 
     * @return a column group row instance you can use to add column groups
     */
    public ColumnGroupRow<T> addColumnGroupRow() {
        ColumnGroupRow<T> row = new ColumnGroupRow<T>(this);
        columnGroupRows.add(row);
        refreshHeader();
        refreshFooter();
        return row;
    }

    /**
     * Adds a new column group row to the grid at a specific index.
     * 
     * @see #addColumnGroupRow() {@link Grid#addColumnGroupRow()} for example
     *      usage
     * 
     * @param rowIndex
     *            the index where the column group row should be added
     * @return a column group row instance you can use to add column groups
     */
    public ColumnGroupRow<T> addColumnGroupRow(int rowIndex) {
        ColumnGroupRow<T> row = new ColumnGroupRow<T>(this);
        columnGroupRows.add(rowIndex, row);
        refreshHeader();
        refreshFooter();
        return row;
    }

    /**
     * Removes a column group row
     * 
     * @param row
     *            The row to remove
     */
    public void removeColumnGroupRow(ColumnGroupRow<T> row) {
        columnGroupRows.remove(row);
        refreshHeader();
        refreshFooter();
    }

    /**
     * Get the column group rows
     * 
     * @return a unmodifiable list of column group rows
     * 
     */
    public List<ColumnGroupRow<T>> getColumnGroupRows() {
        return Collections.unmodifiableList(new ArrayList<ColumnGroupRow<T>>(
                columnGroupRows));
    }

    /**
     * Returns the column group for a row and column
     * 
     * @param row
     *            The row of the column
     * @param column
     *            the column to get the group for
     * @return A column group for the row and column or <code>null</code> if not
     *         found.
     */
    private ColumnGroup<T> getGroupForColumn(ColumnGroupRow<T> row,
            GridColumn<?, T> column) {
        for (ColumnGroup<T> group : row.getGroups()) {
            List<GridColumn<?, T>> columns = group.getColumns();
            if (columns.contains(column)) {
                return group;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <em>Note:</em> This method will change the widget's size in the browser
     * only if {@link #getHeightMode()} returns {@link HeightMode#CSS}.
     * 
     * @see #setHeightMode(HeightMode)
     */
    @Override
    public void setHeight(String height) {
        escalator.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
        escalator.setWidth(width);
    }

    /**
     * Sets the data source used by this grid.
     * 
     * @param dataSource
     *            the data source to use, not null
     * @throws IllegalArgumentException
     *             if <code>dataSource</code> is <code>null</code>
     */
    public void setDataSource(DataSource<T> dataSource)
            throws IllegalArgumentException {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource can't be null.");
        }

        if (this.dataSource != null) {
            this.dataSource.setDataChangeHandler(null);
        }

        this.dataSource = dataSource;
        dataSource.setDataChangeHandler(new DataChangeHandler() {
            @Override
            public void dataUpdated(int firstIndex, int numberOfItems) {
                escalator.getBody().refreshRows(firstIndex, numberOfItems);
            }

            @Override
            public void dataRemoved(int firstIndex, int numberOfItems) {
                escalator.getBody().removeRows(firstIndex, numberOfItems);
            }

            @Override
            public void dataAdded(int firstIndex, int numberOfItems) {
                escalator.getBody().insertRows(firstIndex, numberOfItems);
            }
        });

        int previousRowCount = escalator.getBody().getRowCount();
        if (previousRowCount != 0) {
            escalator.getBody().removeRows(0, previousRowCount);
        }

        int estimatedSize = dataSource.getEstimatedSize();
        if (estimatedSize > 0) {
            escalator.getBody().insertRows(0, estimatedSize);
        }
    }

    /**
     * Sets the rightmost frozen column in the grid.
     * <p>
     * All columns up to and including the given column will be frozen in place
     * when the grid is scrolled sideways.
     * 
     * @param lastFrozenColumn
     *            the rightmost column to freeze, or <code>null</code> to not
     *            have any columns frozen
     * @throws IllegalArgumentException
     *             if {@code lastFrozenColumn} is not a column from this grid
     */
    public void setLastFrozenColumn(GridColumn<?, T> lastFrozenColumn) {
        this.lastFrozenColumn = lastFrozenColumn;
        refreshFrozenColumns();
    }

    private void refreshFrozenColumns() {
        final int frozenCount;
        if (lastFrozenColumn != null) {
            frozenCount = columns.indexOf(lastFrozenColumn) + 1;
            if (frozenCount == 0) {
                throw new IllegalArgumentException(
                        "The given column isn't attached to this grid");
            }
        } else {
            frozenCount = 0;
        }

        escalator.getColumnConfiguration().setFrozenColumnCount(frozenCount);
    }

    /**
     * Gets the rightmost frozen column in the grid.
     * <p>
     * <em>Note:</em> Most usually, this method returns the very value set with
     * {@link #setLastFrozenColumn(GridColumn)}. This value, however, can be
     * reset to <code>null</code> if the column is removed from this grid.
     * 
     * @return the rightmost frozen column in the grid, or <code>null</code> if
     *         no columns are frozen.
     */
    public GridColumn<?, T> getLastFrozenColumn() {
        return lastFrozenColumn;
    }

    public HandlerRegistration addRowVisibilityChangeHandler(
            RowVisibilityChangeHandler handler) {
        /*
         * Reusing Escalator's RowVisibilityChangeHandler, since a scroll
         * concept is too abstract. e.g. the event needs to be re-sent when the
         * widget is resized.
         */
        return escalator.addRowVisibilityChangeHandler(handler);
    }

    /**
     * Scrolls to a certain row, using {@link ScrollDestination#ANY}.
     * 
     * @param rowIndex
     *            zero-based index of the row to scroll to.
     * @throws IllegalArgumentException
     *             if rowIndex is below zero, or above the maximum value
     *             supported by the data source.
     */
    public void scrollToRow(int rowIndex) throws IllegalArgumentException {
        scrollToRow(rowIndex, ScrollDestination.ANY,
                GridConstants.DEFAULT_PADDING);
    }

    /**
     * Scrolls to a certain row, using user-specified scroll destination.
     * 
     * @param rowIndex
     *            zero-based index of the row to scroll to.
     * @param destination
     *            desired destination placement of scrolled-to-row. See
     *            {@link ScrollDestination} for more information.
     * @throws IllegalArgumentException
     *             if rowIndex is below zero, or above the maximum value
     *             supported by the data source.
     */
    public void scrollToRow(int rowIndex, ScrollDestination destination)
            throws IllegalArgumentException {
        scrollToRow(rowIndex, destination,
                destination == ScrollDestination.MIDDLE ? 0
                        : GridConstants.DEFAULT_PADDING);
    }

    /**
     * Scrolls to a certain row using only user-specified parameters.
     * 
     * @param rowIndex
     *            zero-based index of the row to scroll to.
     * @param destination
     *            desired destination placement of scrolled-to-row. See
     *            {@link ScrollDestination} for more information.
     * @param paddingPx
     *            number of pixels to overscroll. Behavior depends on
     *            destination.
     * @throws IllegalArgumentException
     *             if {@code destination} is {@link ScrollDestination#MIDDLE}
     *             and padding is nonzero, because having a padding on a
     *             centered row is undefined behavior, or if rowIndex is below
     *             zero or above the row count of the data source.
     */
    private void scrollToRow(int rowIndex, ScrollDestination destination,
            int paddingPx) throws IllegalArgumentException {
        int maxsize = escalator.getBody().getRowCount() - 1;

        if (rowIndex < 0) {
            throw new IllegalArgumentException("Row index (" + rowIndex
                    + ") is below zero!");
        }

        if (rowIndex > maxsize) {
            throw new IllegalArgumentException("Row index (" + rowIndex
                    + ") is above maximum (" + maxsize + ")!");
        }

        escalator.scrollToRow(rowIndex, destination, paddingPx);
    }

    /**
     * Scrolls to the beginning of the very first row.
     */
    public void scrollToStart() {
        scrollToRow(0, ScrollDestination.START);
    }

    /**
     * Scrolls to the end of the very last row.
     */
    public void scrollToEnd() {
        scrollToRow(escalator.getBody().getRowCount() - 1,
                ScrollDestination.END);
    }

    private static final Logger getLogger() {
        return Logger.getLogger(Grid.class.getName());
    }

    /**
     * Sets the number of rows that should be visible in Grid's body, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * <p>
     * If Grid is currently not in {@link HeightMode#ROW}, the given value is
     * remembered, and applied once the mode is applied.
     * 
     * @param rows
     *            The height in terms of number of rows displayed in Grid's
     *            body. If Grid doesn't contain enough rows, white space is
     *            displayed instead.
     * @throws IllegalArgumentException
     *             if {@code rows} is zero or less
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isInifinite(double)
     *             infinite}
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isNaN(double) NaN}
     * 
     * @see #setHeightMode(HeightMode)
     */
    public void setHeightByRows(double rows) throws IllegalArgumentException {
        escalator.setHeightByRows(rows);
    }

    /**
     * Gets the amount of rows in Grid's body that are shown, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * <p>
     * By default, it is {@value Escalator#DEFAULT_HEIGHT_BY_ROWS}.
     * 
     * @return the amount of rows that should be shown in Grid's body, while in
     *         {@link HeightMode#ROW}.
     * @see #setHeightByRows(double)
     */
    public double getHeightByRows() {
        return escalator.getHeightByRows();
    }

    /**
     * Defines the mode in which the Grid widget's height is calculated.
     * <p>
     * If {@link HeightMode#CSS} is given, Grid will respect the values given
     * via {@link #setHeight(String)}, and behave as a traditional Widget.
     * <p>
     * If {@link HeightMode#ROW} is given, Grid will make sure that the body
     * will display as many rows as {@link #getHeightByRows()} defines.
     * <em>Note:</em> If headers/footers are inserted or removed, the widget
     * will resize itself to still display the required amount of rows in its
     * body. It also takes the horizontal scrollbar into account.
     * 
     * @param heightMode
     *            the mode in to which Grid should be set
     */
    public void setHeightMode(HeightMode heightMode) {
        /*
         * This method is a workaround for the fact that Vaadin re-applies
         * widget dimensions (height/width) on each state change event. The
         * original design was to have setHeight an setHeightByRow be equals,
         * and whichever was called the latest was considered in effect.
         * 
         * But, because of Vaadin always calling setHeight on the widget, this
         * approach doesn't work.
         */

        escalator.setHeightMode(heightMode);
    }

    /**
     * Returns the current {@link HeightMode} the Grid is in.
     * <p>
     * Defaults to {@link HeightMode#CSS}.
     * 
     * @return the current HeightMode
     */
    public HeightMode getHeightMode() {
        return escalator.getHeightMode();
    }

    private Set<String> getConsumedEventsForRenderer(Renderer<?> renderer) {
        Set<String> events = new HashSet<String>();
        if (renderer instanceof ComplexRenderer) {
            Collection<String> consumedEvents = ((ComplexRenderer<?>) renderer)
                    .getConsumedEvents();
            if (consumedEvents != null) {
                events.addAll(consumedEvents);
            }
        }
        return events;
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        EventTarget target = event.getEventTarget();
        if (Element.is(target)) {
            Element e = Element.as(target);
            RowContainer container = escalator.findRowContainer(e);
            if (container != null) {
                Cell cell = container.getCell(e);
                Renderer<?> renderer = columns.get(cell.getColumn())
                        .getRenderer();
                if (renderer instanceof ComplexRenderer) {
                    ((ComplexRenderer<?>) renderer).onBrowserEvent(cell, event);
                }
            }
        }
    }

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
        // Parse SubPart string to type and indices
        String[] splitArgs = subPart.split("\\[");

        String type = splitArgs[0];
        int[] indices = new int[splitArgs.length - 1];
        for (int i = 0; i < indices.length; ++i) {
            String tmp = splitArgs[i + 1];
            indices[i] = Integer.parseInt(tmp.substring(0, tmp.length() - 1));
        }

        // Get correct RowContainer for type from Escalator
        RowContainer container = null;
        if (type.equalsIgnoreCase("header")) {
            container = escalator.getHeader();
        } else if (type.equalsIgnoreCase("cell")) {
            // If wanted row is not visible, we need to scroll there.
            Range visibleRowRange = escalator.getVisibleRowRange();
            if (!visibleRowRange.contains(indices[0])) {
                try {
                    scrollToRow(indices[0]);
                } catch (IllegalArgumentException e) {
                    getLogger().log(Level.SEVERE, e.getMessage());
                }
                // Scrolling causes a lazy loading event. No element can
                // currently be retrieved.
                return null;
            }
            container = escalator.getBody();
        } else if (type.equalsIgnoreCase("footer")) {
            container = escalator.getFooter();
        }

        if (null != container) {
            if (indices.length == 0) {
                // No indexing. Just return the wanted container element
                return DOM.asOld(container.getElement());
            } else {
                try {
                    return DOM.asOld(getSubPart(container, indices));
                } catch (Exception e) {
                    getLogger().log(Level.SEVERE, e.getMessage());
                }
            }
        }
        return null;
    }

    private Element getSubPart(RowContainer container, int[] indices) {
        // Scroll wanted column to view if able
        if (indices.length > 1
                && escalator.getColumnConfiguration().getFrozenColumnCount() <= indices[1]) {
            escalator.scrollToColumn(indices[1], ScrollDestination.ANY, 0);
        }

        Element targetElement = container.getRowElement(indices[0]);
        for (int i = 1; i < indices.length && targetElement != null; ++i) {
            targetElement = (Element) targetElement.getChild(indices[i]);
        }
        return targetElement;
    }

    @Override
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {
        // Containers and matching SubPart types
        List<RowContainer> containers = Arrays.asList(escalator.getHeader(),
                escalator.getBody(), escalator.getFooter());
        List<String> containerType = Arrays.asList("header", "cell", "footer");

        for (int i = 0; i < containers.size(); ++i) {
            RowContainer container = containers.get(i);
            boolean containerRow = (subElement.getTagName().equalsIgnoreCase(
                    "tr") && subElement.getParentElement() == container
                    .getElement());
            if (containerRow) {
                // Wanted SubPart is row that is a child of containers root
                // To get indices, we use a cell that is a child of this row
                subElement = DOM.asOld(subElement.getFirstChildElement());
            }

            Cell cell = container.getCell(subElement);
            if (cell != null) {
                // Skip the column index if subElement was a child of root
                return containerType.get(i) + "[" + cell.getRow()
                        + (containerRow ? "]" : "][" + cell.getColumn() + "]");
            }
        }
        return null;
    }

    private void setSelectColumnRenderer(
            final Renderer<Boolean> selectColumnRenderer) {
        if (this.selectColumnRenderer == selectColumnRenderer) {
            return;
        }

        if (this.selectColumnRenderer != null) {
            removeColumnSkipSelectionColumnCheck(selectionColumn);
        }

        this.selectColumnRenderer = selectColumnRenderer;

        if (selectColumnRenderer != null) {
            selectionColumn = new SelectionColumn(selectColumnRenderer);

            // FIXME: this needs to be done elsewhere, requires design...
            selectionColumn.setWidth(25);
            addColumnSkipSelectionColumnCheck(selectionColumn, 0);
            selectionColumn.initDone();
        } else {
            selectionColumn = null;
        }
    }

    /* TODO remove before final */
    public void setSelectionCheckboxes(boolean set) {
        if (set) {
            setSelectColumnRenderer(new MultiSelectionRenderer(this));
        } else {
            setSelectColumnRenderer(null);
        }
    }

    /*
     * This is the same client-side Grid "isSelected" method as in the selection
     * model design.
     */
    private boolean isSelected(T row) {
        return false;
    }
}
