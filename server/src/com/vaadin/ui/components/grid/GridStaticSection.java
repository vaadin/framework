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
package com.vaadin.ui.components.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container.Indexed;
import com.vaadin.shared.ui.grid.GridStaticSectionState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.CellState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.RowState;

/**
 * Abstract base class for Grid header and footer sections.
 * 
 * @since
 * @author Vaadin Ltd
 * @param <ROWTYPE>
 *            the type of the rows in the section
 */
abstract class GridStaticSection<ROWTYPE extends GridStaticSection.StaticRow<?>>
        implements Serializable {

    /**
     * Abstract base class for Grid header and footer rows.
     * 
     * @param <CELLTYPE>
     *            the type of the cells in the row
     */
    abstract static class StaticRow<CELLTYPE extends StaticCell<?>> implements
            Serializable {

        private RowState rowState = new RowState();
        protected GridStaticSection<?> section;
        private Map<Object, CELLTYPE> cells = new LinkedHashMap<Object, CELLTYPE>();

        protected StaticRow(GridStaticSection<?> section) {
            this.section = section;
        }

        protected void addCell(Object propertyId) {
            CELLTYPE cell = createCell();
            cells.put(propertyId, cell);
            rowState.cells.add(cell.getCellState());
        }

        /**
         * Creates and returns a new instance of the cell type.
         * 
         * @return the created cell
         */
        protected abstract CELLTYPE createCell();

        protected RowState getRowState() {
            return rowState;
        }

        /**
         * Returns the cell at the given position in this row.
         * 
         * @param propertyId
         *            the itemId of column
         * @return the cell on given column
         * @throws IndexOutOfBoundsException
         *             if the index is out of bounds
         */
        public CELLTYPE getCell(Object propertyId) {
            return cells.get(propertyId);
        }
    }

    /**
     * A header or footer cell. Has a simple textual caption.
     * 
     * @param <ROWTYPE>
     *            the type of row this cells is in
     */
    abstract static class StaticCell<ROWTYPE extends StaticRow<?>> implements
            Serializable {

        private CellState cellState = new CellState();
        private ROWTYPE row;

        protected StaticCell(ROWTYPE row) {
            this.row = row;
        }

        /**
         * Gets the row where this cell is.
         * 
         * @return row for this cell
         */
        public ROWTYPE getRow() {
            return row;
        }

        protected CellState getCellState() {
            return cellState;
        }

        /**
         * Gets the current text content of this cell. Text is null if HTML or
         * Component content is used.
         * 
         * @return text content or null
         */
        public String getText() {
            return cellState.text;
        }

        /**
         * Sets the current text content of this cell.
         * 
         * @param text
         *            new text content
         */
        public void setText(String text) {
            if (text != null && !text.equals(getCellState().text)) {
                getCellState().text = text;
                row.section.markAsDirty();
            }
        }
    }

    protected Grid grid;
    protected List<ROWTYPE> rows = new ArrayList<ROWTYPE>();

    /**
     * Sets the visibility of the whole section.
     * 
     * @param visible
     *            true to show this section, false to hide
     */
    public void setVisible(boolean visible) {
        if (getState().visible != visible) {
            getState().visible = visible;
            markAsDirty();
        }
    }

    /**
     * Returns the visibility of this section.
     * 
     * @return true if visible, false otherwise.
     */
    public boolean isVisible() {
        return getState().visible;
    }

    /**
     * Removes the row at the given position.
     * 
     * @param index
     *            the position of the row
     * 
     * @throws IndexOutOfBoundsException
     *             if the index is out of bounds
     */
    public ROWTYPE removeRow(int rowIndex) {
        ROWTYPE row = rows.remove(rowIndex);
        getState().rows.remove(rowIndex);

        markAsDirty();
        return row;
    }

    /**
     * Removes the given row from the section.
     * 
     * @param row
     *            the row to be removed
     * 
     * @throws IllegalArgumentException
     *             if the row does not exist in this section
     */
    public void removeRow(ROWTYPE row) {
        try {
            removeRow(rows.indexOf(row));
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    "Section does not contain the given row");
        }
    }

    /**
     * Gets row at given index.
     * 
     * @param rowIndex
     *            0 based index for row. Counted from top to bottom
     * @return row at given index
     */
    public ROWTYPE getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    /**
     * Adds a new row at the top of this section.
     * 
     * @return the new row
     */
    public ROWTYPE prependRow() {
        return addRowAt(0);
    }

    /**
     * Adds a new row at the bottom of this section.
     * 
     * @return the new row
     */
    public ROWTYPE appendRow() {
        return addRowAt(rows.size());
    }

    /**
     * Inserts a new row at the given position.
     * 
     * @param index
     *            the position at which to insert the row
     * @return the new row
     * 
     * @throws IndexOutOfBoundsException
     *             if the index is out of bounds
     */
    public ROWTYPE addRowAt(int index) {
        ROWTYPE row = createRow();
        rows.add(index, row);
        getState().rows.add(index, row.getRowState());

        Indexed dataSource = grid.getContainerDatasource();
        for (Object id : dataSource.getContainerPropertyIds()) {
            row.addCell(id);
        }

        markAsDirty();
        return row;
    }

    /**
     * Gets the amount of rows in this section.
     * 
     * @return row count
     */
    public int getRowCount() {
        return rows.size();
    }

    protected abstract GridStaticSectionState getState();

    protected abstract ROWTYPE createRow();

    /**
     * Informs the grid that state has changed and it should be redrawn.
     */
    protected void markAsDirty() {
        grid.markAsDirty();
    }
}
