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
package com.vaadin.ui.components.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.vaadin.shared.ui.grid.SectionState;
import com.vaadin.shared.ui.grid.SectionState.CellState;
import com.vaadin.shared.ui.grid.SectionState.RowState;

/**
 * Represents the header or footer section of a Grid.
 *
 * @author Vaadin Ltd.
 * 
 * @param <ROW>
 *            the type of the rows in the section
 *
 * @since 8.0
 */
public abstract class StaticSection<ROW extends StaticSection.StaticRow<?>>
        implements Serializable {

    /**
     * Abstract base class for Grid header and footer rows.
     *
     * @param <CELL>
     *            the type of the cells in the row
     */
    public abstract static class StaticRow<CELL extends StaticCell>
            implements Serializable {

        private RowState rowState = new RowState();
        private StaticSection<?> section;
        private Map<Object, CELL> cells = new LinkedHashMap<>();

        /**
         * Creates a new row belonging to the given section.
         * 
         * @param section
         *            the section of the row
         */
        protected StaticRow(StaticSection<?> section) {
            this.section = section;
        }

        /**
         * Creates and returns a new instance of the cell type.
         *
         * @return the created cell
         */
        protected abstract CELL createCell();

        /**
         * Returns the declarative tag name used for the cells in this row.
         * 
         * @return the cell tag name
         */
        protected abstract String getCellTagName();

        /**
         * Adds a cell to this section, corresponding to the given column id.
         * 
         * @param columnId
         *            the id of the column for which to add a cell
         */
        protected void addCell(String columnId) {
            CELL cell = createCell();
            cell.setColumnId(columnId);
            cells.put(columnId, cell);
            rowState.cells.put(columnId, cell.getCellState());
        }

        /**
         * Removes the cell from this section that corresponds to the given
         * column id. If there is no such cell, does nothing.
         * 
         * @param columnId
         *            the id of the column from which to remove the cell
         */
        protected void removeCell(Object columnId) {
            CELL cell = cells.remove(columnId);
            if (cell != null) {
                rowState.cells.remove(cell.getCellState());
            }
        }

        /**
         * Returns the shared state of this row.
         * 
         * @return the row state
         */
        protected RowState getRowState() {
            return rowState;
        }

        /**
         * Returns the cell in this section that corresponds to the given column
         * id.
         *
         * @param columnId
         *            the id of the column
         * @return the cell for the given column or null if not found
         */
        public CELL getCell(String columnId) {
            CELL cell = cells.get(columnId);
            return cell;
        }
    }

    /**
     * A header or footer cell. Has a simple textual caption.
     */
    abstract static class StaticCell implements Serializable {

        private CellState cellState = new CellState();
        private StaticRow<?> row;

        protected StaticCell(StaticRow<?> row) {
            this.row = row;
        }

        void setColumnId(String id) {
            cellState.columnId = id;
        }

        String getColumnId() {
            return cellState.columnId;
        }

        /**
         * Gets the row where this cell is.
         *
         * @return row for this cell
         */
        public StaticRow<?> getRow() {
            return row;
        }

        /**
         * Returns the shared state of this cell.
         * 
         * @return the cell state
         */
        protected CellState getCellState() {
            return cellState;
        }

        /**
         * Sets the textual caption of this cell.
         *
         * @param text
         *            a plain text caption, not null
         */
        public void setText(String text) {
            Objects.requireNonNull(text, "text cannot be null");
            cellState.text = text;
            row.section.markAsDirty();
        }

        /**
         * Returns the textual caption of this cell.
         *
         * @return the plain text caption
         */
        public String getText() {
            return cellState.text;
        }
    }

    private List<ROW> rows = new ArrayList<>();

    /**
     * Creates a new row instance.
     * 
     * @return the new row
     */
    protected abstract ROW createRow();

    /**
     * Returns the shared state of this section.
     * 
     * @param markAsDirty
     *            {@code true} to mark the state as modified, {@code false}
     *            otherwise
     * @return the section state
     */
    protected abstract SectionState getState(boolean markAsDirty);

    /**
     * Marks the state of this section as modified.
     */
    protected void markAsDirty() {
        getState(true);
    }

    /**
     * Adds a new row at the given index.
     * 
     * @param index
     *            the index of the new row
     * @return the added row
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0 || index > getRowCount()}
     */
    public ROW addRowAt(int index) {
        ROW row = createRow();
        rows.add(index, row);
        getState(true).rows.add(index, row.getRowState());
        return row;
    }

    /**
     * Removes the row at the given index.
     * 
     * @param index
     *            the index of the row to remove
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0 || index >= getRowCount()}
     */
    public void removeRow(int index) {
        rows.remove(index);
        getState(true).rows.remove(index);
    }

    /**
     * Removes the given row from this section.
     * 
     * @param row
     *            the row to remove, not null
     * @throws IllegalArgumentException
     *             if this section does not contain the row
     */
    public void removeRow(Object row) {
        Objects.requireNonNull(row, "row cannot be null");
        int index = rows.indexOf(row);
        if (index < 0) {
            throw new IllegalArgumentException(
                    "Section does not contain the given row");
        }
        removeRow(index);
    }

    /**
     * Returns the row at the given index.
     * 
     * @param index
     *            the index of the row
     * @return the row at the index
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0 || index >= getRowCount()}
     */
    public ROW getRow(int index) {
        return rows.get(index);
    }

    /**
     * Returns the number of rows in this section.
     *
     * @return the number of rows
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Adds a cell corresponding to the given column id to this section.
     *
     * @param columnId
     *            the id of the column for which to add a cell
     */
    public void addColumn(String columnId) {
        for (ROW row : rows) {
            row.addCell(columnId);
        }
    }

    /**
     * Removes the cell corresponding to the given column id.
     *
     * @param columnId
     *            the id of the column whose cell to remove
     */
    public void removeColumn(String columnId) {
        for (ROW row : rows) {
            row.removeCell(columnId);
        }
    }
}
