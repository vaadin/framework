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
import java.util.HashSet;
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

        private int colspan = 1;

        private GridStaticSection<?> section;

        /**
         * Sets the text displayed in this cell.
         * 
         * @param text
         *            a plain text caption
         */
        public void setText(String text) {
            this.text = text;
            section.refreshSection();
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

        /**
         * @return the colspan
         */
        public int getColspan() {
            return colspan;
        }

        /**
         * @param colspan
         *            the colspan to set
         */
        public void setColspan(int colspan) {
            this.colspan = colspan;
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

        private Collection<List<CELLTYPE>> cellGroups = new HashSet<List<CELLTYPE>>();

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

        /**
         * Merges cells in a row
         * 
         * @param cells
         *            The cells to be merged
         * @return The first cell of the merged cells
         */
        protected CELLTYPE join(List<CELLTYPE> cells) {
            assert cells.size() > 1 : "You cannot merge less than 2 cells together";

            // Ensure no cell is already grouped
            for (CELLTYPE cell : cells) {
                if (getCellGroupForCell(cell) != null) {
                    throw new IllegalStateException("Cell " + cell.getText()
                            + " is already grouped.");
                }
            }

            // Ensure continuous range
            int firstCellIndex = this.cells.indexOf(cells.get(0));
            for (int i = 0; i < cells.size(); i++) {
                if (this.cells.get(firstCellIndex + i) != cells.get(i)) {
                    throw new IllegalStateException(
                            "Cell range must be a continous range");
                }
            }

            // Create a new group
            cellGroups.add(new ArrayList<CELLTYPE>(cells));

            calculateColspans();

            getSection().refreshSection();

            // Returns first cell of group
            return cells.get(0);
        }

        /**
         * Merges columns cells in a row
         * 
         * @param columns
         *            The columns which header should be merged
         * @return The remaining visible cell after the merge
         */
        public CELLTYPE join(GridColumn<?, ?>... columns) {
            assert columns.length > 1 : "You cannot merge less than 2 columns together";

            // Convert columns to cells
            List<CELLTYPE> cells = new ArrayList<CELLTYPE>();
            for (GridColumn<?, ?> c : columns) {
                int index = getSection().getGrid().getColumns().indexOf(c);
                cells.add(this.cells.get(index));
            }

            return join(cells);
        }

        /**
         * Merges columns cells in a row
         * 
         * @param cells
         *            The cells to merge. Must be from the same row.
         * @return The remaining visible cell after the merge
         */
        public CELLTYPE join(CELLTYPE... cells) {
            return join(Arrays.asList(cells));
        }

        private List<CELLTYPE> getCellGroupForCell(CELLTYPE cell) {
            for (List<CELLTYPE> group : cellGroups) {
                if (group.contains(cell)) {
                    return group;
                }
            }
            return null;
        }

        private void calculateColspans() {
            // Reset all cells
            for (CELLTYPE cell : cells) {
                cell.setColspan(1);
            }

            // Set colspan for grouped cells
            for (List<CELLTYPE> group : cellGroups) {
                for (int i = 0; i < group.size(); i++) {
                    CELLTYPE cell = group.get(i);
                    if (i == 0) {
                        // Assign full colspan to first cell
                        cell.setColspan(group.size());
                    } else {
                        // Hide other cells
                        cell.setColspan(0);
                    }
                }
            }
        }

        protected void addCell(int index) {
            CELLTYPE cell = createCell();
            cell.setSection(getSection());
            cells.add(index, cell);
        }

        protected void removeCell(int index) {
            cells.remove(index);
        }

        protected void setRenderer(Renderer<String> renderer) {
            this.renderer = renderer;
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
    protected abstract void refreshSection();

    /**
     * Sets the visibility of the whole section.
     * 
     * @param visible
     *            true to show this section, false to hide
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        refreshSection();
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
        refreshSection();
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
        refreshSection();
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
