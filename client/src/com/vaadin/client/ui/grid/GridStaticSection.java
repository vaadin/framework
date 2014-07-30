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

import com.google.gwt.user.client.ui.Widget;

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
     */
    static class StaticCell {

        public enum Type {
            TEXT, HTML, WIDGET;
        }

        private Object content = null;

        private int colspan = 1;

        private GridStaticSection<?> section;

        private Type type = Type.TEXT;

        /**
         * Sets the text displayed in this cell.
         * 
         * @param text
         *            a plain text caption
         */
        public void setText(String text) {
            this.content = text;
            this.type = Type.TEXT;
            section.requestSectionRefresh();
        }

        /**
         * Returns the text displayed in this cell.
         * 
         * @return the plain text caption
         */
        public String getText() {
            if (type != Type.TEXT) {
                throw new IllegalStateException(
                        "Cannot fetch Text from a cell with type " + type);
            }
            return (String) content;
        }

        protected GridStaticSection<?> getSection() {
            assert section != null;
            return section;
        }

        protected void setSection(GridStaticSection<?> section) {
            this.section = section;
        }

        /**
         * Returns the amount of columns the cell spans. By default is 1.
         * 
         * @return The amount of columns the cell spans.
         */
        public int getColspan() {
            return colspan;
        }

        /**
         * Sets the amount of columns the cell spans. Must be more or equal to
         * 1. By default is 1.
         * 
         * @param colspan
         *            the colspan to set
         */
        public void setColspan(int colspan) {
            if (colspan < 1) {
                throw new IllegalArgumentException(
                        "Colspan cannot be less than 1");
            }

            this.colspan = colspan;
            section.requestSectionRefresh();
        }

        /**
         * Returns the html inside the cell.
         * 
         * @throws IllegalStateException
         *             if trying to retrive HTML from a cell with a type other
         *             than {@link Type#HTML}.
         * @return the html content of the cell.
         */
        public String getHtml() {
            if (type != Type.HTML) {
                throw new IllegalStateException(
                        "Cannot fetch HTML from a cell with type " + type);
            }
            return (String) content;
        }

        /**
         * Sets the content of the cell to the provided html. All previous
         * content is discarded and the cell type is set to {@link Type#HTML}.
         * 
         * @param html
         *            The html content of the cell
         */
        public void setHtml(String html) {
            this.content = html;
            this.type = Type.HTML;
            section.requestSectionRefresh();
        }

        /**
         * Returns the widget in the cell.
         * 
         * @throws IllegalStateException
         *             if the cell is not {@link Type#WIDGET}
         * 
         * @return the widget in the cell
         */
        public Widget getWidget() {
            if (type != Type.WIDGET) {
                throw new IllegalStateException(
                        "Cannot fetch Widget from a cell with type " + type);
            }
            return (Widget) content;
        }

        /**
         * Set widget as the content of the cell. The type of the cell becomes
         * {@link Type#WIDGET}. All previous content is discarded.
         * 
         * @param widget
         *            The widget to add to the cell. Should not be previously
         *            attached anywhere (widget.getParent == null).
         */
        public void setWidget(Widget widget) {
            this.content = widget;
            this.type = Type.WIDGET;
            section.requestSectionRefresh();
        }

        /**
         * Returns the type of the cell.
         * 
         * @return the type of content the cell contains.
         */
        public Type getType() {
            return type;
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

        private Renderer<String> renderer = new Renderer<String>() {

            @Override
            public void render(FlyweightCell cell, String data) {
                /*
                 * The rendering into the cell is done directly from the updater
                 * since it needs to handle multiple types of data.
                 */
            }
        };

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

            // Calculates colspans, triggers refresh on section implicitly
            calculateColspans();

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

        void calculateColspans() {

            // Reset all cells
            for (CELLTYPE cell : cells) {
                cell.setColspan(1);
            }

            // Set colspan for grouped cells
            for (List<CELLTYPE> group : cellGroups) {

                int firstVisibleColumnInGroup = -1;
                int lastVisibleColumnInGroup = -1;
                int hiddenInsideGroup = 0;

                /*
                 * To be able to calculate the colspan correctly we need to two
                 * things; find the first visible cell in the group which will
                 * get the colspan assigned to and find the amount of columns
                 * which should be spanned.
                 * 
                 * To do that we iterate through all cells, marking into memory
                 * when we find the first visible cell, when we find the last
                 * visible cell and how many cells are hidden in between.
                 */
                for (int i = 0; i < group.size(); i++) {
                    CELLTYPE cell = group.get(i);
                    int cellIndex = this.cells.indexOf(cell);
                    boolean columnVisible = getSection().getGrid()
                            .getColumn(cellIndex).isVisible();
                    if (columnVisible) {
                        lastVisibleColumnInGroup = i;
                        if (firstVisibleColumnInGroup == -1) {
                            firstVisibleColumnInGroup = i;
                        }
                    } else if (firstVisibleColumnInGroup != -1) {
                        hiddenInsideGroup++;
                    }
                }

                if (firstVisibleColumnInGroup == -1
                        || lastVisibleColumnInGroup == -1
                        || firstVisibleColumnInGroup == lastVisibleColumnInGroup) {
                    // No cells in group
                    continue;
                }

                /*
                 * Assign colspan to first cell in group.
                 */
                CELLTYPE firstVisibleCell = group
                        .get(firstVisibleColumnInGroup);
                firstVisibleCell.setColspan(lastVisibleColumnInGroup
                        - firstVisibleColumnInGroup - hiddenInsideGroup + 1);
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
     * <p>
     * <b>Note</b> that re-render means calling update() on each cell,
     * preAttach()/postAttach()/preDetach()/postDetach() is not called as the
     * cells are not removed from the DOM.
     */
    protected abstract void requestSectionRefresh();

    /**
     * Sets the visibility of the whole section.
     * 
     * @param visible
     *            true to show this section, false to hide
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        requestSectionRefresh();
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

        requestSectionRefresh();
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
        requestSectionRefresh();
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
        try {
            return rows.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Row with index " + index
                    + " does not exist");
        }
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
