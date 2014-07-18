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
import java.util.List;

import com.vaadin.client.ui.grid.renderers.TextRenderer;

/**
 * Abstract base class for Grid header and footer sections.
 * 
 * @since
 * @author Vaadin Ltd
 * @param <ROWTYPE>
 *            the type of the rows in the section
 */
abstract class GridStaticSection<ROWTYPE extends GridStaticSection.StaticRow<?>> {

    /**
     * A header or footer cell. Has a simple textual caption.
     * 
     * TODO HTML content
     * 
     * TODO Widget content
     */
    static class StaticCell {

        private String text = "";

        private GridStaticSection<?> section;

        /**
         * Sets the text displayed in this cell.
         *
         * @param text
         *            a plain text caption
         */
        public void setText(String text) {
            this.text = text;
            section.refreshGrid();
        }

        /**
         * Returns the text displayed in this cell.
         * 
         * @return the plain text caption
         */
        public String getText() {
            return text;
        }

        protected GridStaticSection<?> getSection() {
            assert section != null;
            return section;
        }

        protected void setSection(GridStaticSection<?> section) {
            this.section = section;
        }

    }

    /**
     * Abstract base class for Grid header and footer rows.
     * 
     * @param <CELLTYPE>
     *            the type of the cells in the row
     */
    abstract static class StaticRow<CELLTYPE extends StaticCell> {

        private List<CELLTYPE> cells = new ArrayList<CELLTYPE>();

        private Renderer<String> renderer = new TextRenderer();

        private GridStaticSection<?> section;

        /**
         * Returns the cell at the given position in this row.
         * 
         * @param index
         *            the position of the cell
         * @return the cell at the index
         * @throws IndexOutOfBoundsException
         *             if the index is out of bounds
         */
        public CELLTYPE getCell(int index) {
            return cells.get(index);
        }

        protected void addCell(int index) {
            CELLTYPE cell = createCell();
            cell.setSection(getSection());
            cells.add(index, cell);
        }

        protected void removeCell(int index) {
            cells.remove(index);
        }

        protected Renderer<String> getRenderer() {
            return renderer;
        }

        protected abstract CELLTYPE createCell();

        protected GridStaticSection<?> getSection() {
            return section;
        }

        protected void setSection(GridStaticSection<?> section) {
            this.section = section;
        }
    }

    private Grid<?> grid;

    private List<ROWTYPE> rows = new ArrayList<ROWTYPE>();

    private boolean visible = true;

    /**
     * Creates and returns a new instance of the row type.
     * 
     * @return the created row
     */
    protected abstract ROWTYPE createRow();

    /**
     * Informs the grid that this section should be re-rendered.
     */
    protected abstract void refreshGrid();

    /**
     * Sets the visibility of the whole section.
     * 
     * @param visible
     *            true to show this section, false to hide
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        refreshGrid();
    }

    /**
     * Returns the visibility of this section.
     * 
     * @return true if visible, false otherwise.
     */
    public boolean isVisible() {
        return visible;
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
    public ROWTYPE addRow(int index) {
        ROWTYPE row = createRow();
        row.setSection(this);
        for (int i = 0; i < getGrid().getColumnCount(); ++i) {
            row.addCell(i);
        }
        rows.add(index, row);
        refreshGrid();
        return row;
    }

    /**
     * Adds a new row at the top of this section.
     * 
     * @return the new row
     */
    public ROWTYPE prependRow() {
        return addRow(0);
    }

    /**
     * Adds a new row at the bottom of this section.
     * 
     * @return the new row
     */
    public ROWTYPE appendRow() {
        return addRow(rows.size());
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
    public void removeRow(int index) {
        rows.remove(index);
        refreshGrid();
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
        if (!rows.remove(row)) {
            throw new IllegalArgumentException(
                    "Section does not contain the given row");
        }
        refreshGrid();
    }

    /**
     * Returns the row at the given position.
     * 
     * @param index
     *            the position of the row
     * @return the row with the given index
     * 
     * @throws IndexOutOfBoundsException
     *             if the index is out of bounds
     */
    public ROWTYPE getRow(int index) {
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

    protected List<ROWTYPE> getRows() {
        return rows;
    }

    protected int getVisibleRowCount() {
        return isVisible() ? getRowCount() : 0;
    }

    protected void addColumn(GridColumn<?, ?> column, int index) {
        for (ROWTYPE row : rows) {
            row.addCell(index);
        }
    }

    protected void removeColumn(int index) {
        for (ROWTYPE row : rows) {
            row.removeCell(index);
        }
    }

    protected void setGrid(Grid<?> grid) {
        this.grid = grid;
    }

    protected Grid<?> getGrid() {
        assert grid != null;
        return grid;
    }
}
