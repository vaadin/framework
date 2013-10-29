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
     * Escalator used internally by the grid to render the rows
     */
    private Escalator escalator = GWT.create(Escalator.class);

    /**
     * List of columns in the grid. Order defines the visible order.
     */
    private final List<GridColumn<T>> columns = new ArrayList<GridColumn<T>>();

    /**
     * Base class for grid columns internally used by the Grid. You should use
     * {@link GridColumn} when creating new columns.
     * 
     * @param <T>
     *            the row type
     */
    public static abstract class AbstractGridColumn<T> {

        /**
         * Grid associated with the column
         */
        private Grid<T> grid;

        /**
         * Text displayed in the column header
         */
        private String header;

        /**
         * Text displayed in the column footer
         */
        private String footer;

        /**
         * Is the column visible
         */
        private boolean visible;

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
            if (SharedUtil.equals(caption, this.header)) {
                return;
            }

            this.header = caption;

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
            if (SharedUtil.equals(caption, this.footer)) {
                return;
            }

            this.footer = caption;

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
         *            Set to <code>true</code> to show the column in the grid
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
         *            the row object that provides the cell content
         * @return The cell content of the row
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
     * Creates a new instance.
     */
    public Grid() {
        initWidget(escalator);

        escalator.getHeader().setEscalatorUpdater(createHeaderUpdater());
        escalator.getBody().setEscalatorUpdater(createBodyUpdater());
        escalator.getFooter().setEscalatorUpdater(createFooterUpdater());
    }

    /**
     * Creates the header updater that updates the escalator header rows from
     * the column and column group rows.
     * 
     * @return the updater that updates the data in the escalator.
     */
    private EscalatorUpdater createHeaderUpdater() {
        return new EscalatorUpdater() {

            @Override
            public void updateCells(Row row, List<Cell> cellsToUpdate) {
                if (isHeaderVisible()) {
                    for (Cell cell : cellsToUpdate) {
                        AbstractGridColumn<T> column = columns.get(cell
                                .getColumn());
                        cell.getElement().setInnerText(
                                column.getHeaderCaption());
                    }
                }
            }
        };
    }

    // TODO Should be implemented bu the data sources
    private EscalatorUpdater createBodyUpdater() {
        return new EscalatorUpdater() {

            @Override
            public void updateCells(Row row, List<Cell> cellsToUpdate) {
                for (Cell cell : cellsToUpdate) {
                    cell.getElement().setInnerHTML("-");
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
        return new EscalatorUpdater() {

            @Override
            public void updateCells(Row row, List<Cell> cellsToUpdate) {
                if (isFooterVisible()) {
                    for (Cell cell : cellsToUpdate) {
                        AbstractGridColumn<T> column = columns.get(cell
                                .getColumn());
                        cell.getElement().setInnerText(
                                column.getFooterCaption());
                    }
                }
            }
        };
    }

    /**
     * Refreshes all header rows.
     */
    private void refreshHeader() {
        RowContainer header = escalator.getHeader();
        if (isHeaderVisible() && header.getRowCount() > 0) {
            header.refreshRows(0, header.getRowCount());
        }
    }

    /**
     * Refreshes all footer rows.
     */
    private void refreshFooter() {
        RowContainer footer = escalator.getFooter();
        if (isFooterVisible() && footer.getRowCount() > 0) {
            footer.refreshRows(0, footer.getRowCount());
        }
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
        try {
            return columns.get(index);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            throw new IllegalStateException("Column not found.", aioobe);
        }
    }

    /**
     * Sets the header row visible.
     * 
     * @param visible
     *            true if header rows should be visible
     */
    public void setHeaderVisible(boolean visible) {
        if (visible == isHeaderVisible()) {
            return;
        }

        RowContainer header = escalator.getHeader();

        // TODO Should support multiple headers
        if (visible) {
            header.insertRows(0, 1);
        } else {
            header.removeRows(0, 1);
        }
    }

    /**
     * Are the header row(s) visible?
     * 
     * @return <code>true</code> if the header is visible
     */
    public boolean isHeaderVisible() {
        return escalator.getHeader().getRowCount() > 0;
    }

    /**
     * Sets the footer row(s) visible.
     * 
     * @param visible
     *            true if header rows should be visible
     */
    public void setFooterVisible(boolean visible) {
        if (visible == isFooterVisible()) {
            return;
        }

        RowContainer footer = escalator.getFooter();

        // TODO Should support multiple footers
        if (visible) {
            footer.insertRows(0, 1);
        } else {
            footer.removeRows(0, 1);
        }
    }

    /**
     * Are the footer row(s) visible?
     * 
     * @return <code>true</code> if the footer is visible
     */
    public boolean isFooterVisible() {
        return escalator.getFooter().getRowCount() > 0;
    }
}
