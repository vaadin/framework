/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
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
 * @since 7.2
 * @author Vaadin Ltd
 */
public class Grid<T> extends Composite {

    /**
     * Escalator used internally by grid to render the rows
     */
    private Escalator escalator = GWT.create(Escalator.class);

    /**
     * List of columns in the grid. Order defines the visible order.
     */
    private final List<GridColumn<T>> columns = new ArrayList<GridColumn<T>>();

    private DataSource<T> dataSource;

    /**
     * The column groups rows added to the grid
     */
    private final List<ColumnGroupRow> columnGroupRows = new ArrayList<ColumnGroupRow>();

    /**
     * Are the headers for the columns visible
     */
    private boolean columnHeadersVisible = false;

    /**
     * Are the footers for the columns visible
     */
    private boolean columnFootersVisible = false;

    /**
     * Base class for grid columns internally used by the Grid. The user should
     * use {@link GridColumn} when creating new columns.
     * 
     * @param <T>
     *            the row type
     */
    public static abstract class AbstractGridColumn<T> {

        /**
         * The grid the column is associated with
         */
        private Grid<T> grid;

        /**
         * Should the column be visible in the grid
         */
        private boolean visible;

        /**
         * The text displayed in the header of the column
         */
        private String header;

        /**
         * Text displayed in the column footer
         */
        private String footer;

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
        public void setVisible(boolean visible) {
            if (this.visible == visible) {
                return;
            }

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

                // TODO should update body as well
            }

            this.visible = visible;
        }

        /**
         * Returns the text that should be displayed in the cell.
         * 
         * @param row
         *            The row object that provides the cell content.
         * 
         * @return The cell content
         */
        public abstract String getValue(T row);

        /**
         * Finds the index of this column instance
         * 
         */
        private int findIndexOfColumn() {
            return grid.columns.indexOf(this);
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
         * @return The value that should be rendered for the column caption
         */
        public abstract String getColumnValue(GridColumn column);

        /**
         * Gets the group caption value
         * 
         * @param group
         *            The group for with the caption value should be returned
         * @return The value that should be rendered for the column caption
         */
        public abstract String getGroupValue(ColumnGroup group);

        /**
         * Is the row visible in the header/footer
         * 
         * @return <code>true</code> if the row should be visible
         */
        public abstract boolean isRowVisible(ColumnGroupRow row);

        /**
         * Should the first row be visible
         * 
         * @return <code>true</code> if the first row should be visible
         */
        public abstract boolean firstRowIsVisible();

        @Override
        public void updateCells(Row row, List<Cell> cellsToUpdate) {

            int rowIndex;
            if (inverted) {
                rowIndex = rows.getRowCount() - row.getRow() - 1;
            } else {
                rowIndex = row.getRow();
            }

            if (firstRowIsVisible() && rowIndex == 0) {
                // column headers
                for (Cell cell : cellsToUpdate) {
                    int columnIndex = cell.getColumn();
                    GridColumn column = columns.get(columnIndex);
                    cell.getElement().setInnerText(getColumnValue(column));
                }

            } else if (columnGroupRows.size() > 0) {
                // Adjust for headers
                if (firstRowIsVisible()) {
                    rowIndex--;
                }

                // Adjust for previous invisible header rows
                ColumnGroupRow groupRow = null;
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

                for (Cell cell : cellsToUpdate) {
                    int columnIndex = cell.getColumn();
                    GridColumn column = columns.get(columnIndex);
                    ColumnGroup group = getGroupForColumn(groupRow, column);

                    if (group != null) {
                        // FIXME Should merge the group cells when escalator
                        // supports it
                        cell.getElement().setInnerText(getGroupValue(group));
                    } else {
                        // Cells are reused
                        cell.getElement().setInnerHTML(null);
                    }
                }
            }
        }
    }

    /**
     * Creates a new instance.
     */
    public Grid() {
        initWidget(escalator);

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

    /**
     * Creates the header updater that updates the escalator header rows from
     * the column and column group rows.
     * 
     * @return the updater that updates the data in the escalator.
     */
    private EscalatorUpdater createHeaderUpdater() {
        return new HeaderFooterEscalatorUpdater(escalator.getHeader(), true) {

            @Override
            public boolean isRowVisible(ColumnGroupRow row) {
                return row.isHeaderVisible();
            }

            @Override
            public String getGroupValue(ColumnGroup group) {
                return group.getHeaderCaption();
            }

            @Override
            public String getColumnValue(GridColumn column) {
                return column.getHeaderCaption();
            }

            @Override
            public boolean firstRowIsVisible() {
                return isColumnHeadersVisible();
            }
        };
    }

    private EscalatorUpdater createBodyUpdater() {
        return new EscalatorUpdater() {

            @Override
            public void updateCells(Row row, List<Cell> cellsToUpdate) {
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

                for (Cell cell : cellsToUpdate) {
                    String value = getColumn(cell.getColumn())
                            .getValue(rowData);
                    cell.getElement().setInnerText(value);
                }
            }

            private void setCellsLoading(List<Cell> cellsToUpdate) {
                for (Cell cell : cellsToUpdate) {
                    cell.getElement().setInnerText("...");
                }
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
            public boolean isRowVisible(ColumnGroupRow row) {
                return row.isFooterVisible();
            }

            @Override
            public String getGroupValue(ColumnGroup group) {
                return group.getFooterCaption();
            }

            @Override
            public String getColumnValue(GridColumn column) {
                return column.getFooterCaption();
            }

            @Override
            public boolean firstRowIsVisible() {
                return isColumnFootersVisible();
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
        for (ColumnGroupRow row : columnGroupRows) {
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
    public void addColumn(GridColumn<T> column) {
        ColumnConfiguration conf = escalator.getColumnConfiguration();
        addColumn(column, conf.getColumnCount());
    }

    /**
     * Inserts a column into a specific position in the grid.
     * 
     * @param index
     *            the index where the column should be inserted into
     * @param column
     *            the column to add
     */
    public void addColumn(GridColumn<T> column, int index) {

        // Register this grid instance with the column
        ((AbstractGridColumn<T>) column).setGrid(this);

        columns.add(index, column);

        ColumnConfiguration conf = escalator.getColumnConfiguration();
        conf.insertColumns(index, 1);
    }

    /**
     * Removes a column from the grid.
     * 
     * @param column
     *            the column to remove
     */
    public void removeColumn(GridColumn<T> column) {

        int columnIndex = columns.indexOf(column);
        columns.remove(columnIndex);

        // de-register column with grid
        ((AbstractGridColumn<T>) column).setGrid(null);

        ColumnConfiguration conf = escalator.getColumnConfiguration();
        conf.removeColumns(columnIndex, 1);
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
    public List<GridColumn<T>> getColumns() {
        return Collections.unmodifiableList(new ArrayList<GridColumn<T>>(
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
    public GridColumn<T> getColumn(int index) throws IllegalArgumentException {
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
    public ColumnGroupRow addColumnGroupRow() {
        ColumnGroupRow row = new ColumnGroupRow(this);
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
    public ColumnGroupRow addColumnGroupRow(int rowIndex) {
        ColumnGroupRow row = new ColumnGroupRow(this);
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
    public void removeColumnGroupRow(ColumnGroupRow row) {
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
    public List<ColumnGroupRow> getColumnGroupRows() {
        return Collections.unmodifiableList(new ArrayList<ColumnGroupRow>(
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
    private static ColumnGroup getGroupForColumn(ColumnGroupRow row,
            GridColumn column) {
        for (ColumnGroup group : row.getGroups()) {
            List<GridColumn> columns = group.getColumns();
            if (columns.contains(column)) {
                return group;
            }
        }
        return null;
    }

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
}
