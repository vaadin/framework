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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.shared.ui.grid.GridStaticCellType;

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

        private Object content = null;

        private int colspan = 1;

        private GridStaticSection<?> section;

        private GridStaticCellType type = GridStaticCellType.TEXT;

        private String styleName = null;

        /**
         * Sets the text displayed in this cell.
         * 
         * @param text
         *            a plain text caption
         */
        public void setText(String text) {
            this.content = text;
            this.type = GridStaticCellType.TEXT;
            section.requestSectionRefresh();
        }

        /**
         * Returns the text displayed in this cell.
         * 
         * @return the plain text caption
         */
        public String getText() {
            if (type != GridStaticCellType.TEXT) {
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
            if (type != GridStaticCellType.HTML) {
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
            this.type = GridStaticCellType.HTML;
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
            if (type != GridStaticCellType.WIDGET) {
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
            this.type = GridStaticCellType.WIDGET;
            section.requestSectionRefresh();
        }

        /**
         * Returns the type of the cell.
         * 
         * @return the type of content the cell contains.
         */
        public GridStaticCellType getType() {
            return type;
        }

        /**
         * Returns the custom style name for this cell.
         * 
         * @return the style name or null if no style name has been set
         */
        public String getStyleName() {
            return styleName;
        }

        /**
         * Sets a custom style name for this cell.
         * 
         * @param styleName
         *            the style name to set or null to not use any style name
         */
        public void setStyleName(String styleName) {
            this.styleName = styleName;
            section.requestSectionRefresh();

        }

    }

    /**
     * Abstract base class for Grid header and footer rows.
     * 
     * @param <CELLTYPE>
     *            the type of the cells in the row
     */
    abstract static class StaticRow<CELLTYPE extends StaticCell> {

        private Map<GridColumn<?, ?>, CELLTYPE> cells = new HashMap<GridColumn<?, ?>, CELLTYPE>();

        private GridStaticSection<?> section;

        /**
         * Map from set of spanned columns to cell meta data.
         */
        private Map<Set<GridColumn<?, ?>>, CELLTYPE> cellGroups = new HashMap<Set<GridColumn<?, ?>>, CELLTYPE>();

        /**
         * A custom style name for the row or null if none is set.
         */
        private String styleName = null;

        /**
         * Returns the cell on given GridColumn. If the column is merged
         * returned cell is the cell for the whole group.
         * 
         * @param column
         *            the column in grid
         * @return the cell on given column, merged cell for merged columns,
         *         null if not found
         */
        public CELLTYPE getCell(GridColumn<?, ?> column) {
            Set<GridColumn<?, ?>> cellGroup = getCellGroupForColumn(column);
            if (cellGroup != null) {
                return cellGroups.get(cellGroup);
            }
            return cells.get(column);
        }

        /**
         * Merges columns cells in a row
         * 
         * @param columns
         *            the columns which header should be merged
         * @return the remaining visible cell after the merge, or the cell on
         *         first column if all are hidden
         */
        public CELLTYPE join(GridColumn<?, ?>... columns) {
            if (columns.length <= 1) {
                throw new IllegalArgumentException(
                        "You can't merge less than 2 columns together.");
            }

            HashSet<GridColumn<?, ?>> columnGroup = new HashSet<GridColumn<?, ?>>();
            for (GridColumn<?, ?> column : columns) {
                if (!cells.containsKey(column)) {
                    throw new IllegalArgumentException(
                            "Given column does not exists on row " + column);
                } else if (getCellGroupForColumn(column) != null) {
                    throw new IllegalStateException(
                            "Column is already in a group.");
                }
                columnGroup.add(column);
            }

            CELLTYPE joinedCell = createCell();
            cellGroups.put(columnGroup, joinedCell);
            joinedCell.setSection(getSection());

            calculateColspans();

            return joinedCell;
        }

        /**
         * Merges columns cells in a row
         * 
         * @param cells
         *            The cells to merge. Must be from the same row.
         * @return The remaining visible cell after the merge, or the first cell
         *         if all columns are hidden
         */
        public CELLTYPE join(CELLTYPE... cells) {
            if (cells.length <= 1) {
                throw new IllegalArgumentException(
                        "You can't merge less than 2 cells together.");
            }

            GridColumn<?, ?>[] columns = new GridColumn<?, ?>[cells.length];

            int j = 0;
            for (GridColumn<?, ?> column : this.cells.keySet()) {
                CELLTYPE cell = this.cells.get(column);
                if (!this.cells.containsValue(cells[j])) {
                    throw new IllegalArgumentException(
                            "Given cell does not exists on row");
                } else if (cell.equals(cells[j])) {
                    columns[j++] = column;
                    if (j == cells.length) {
                        break;
                    }
                }
            }

            return join(columns);
        }

        private Set<GridColumn<?, ?>> getCellGroupForColumn(
                GridColumn<?, ?> column) {
            for (Set<GridColumn<?, ?>> group : cellGroups.keySet()) {
                if (group.contains(column)) {
                    return group;
                }
            }
            return null;
        }

        void calculateColspans() {

            // Reset all cells
            for (CELLTYPE cell : this.cells.values()) {
                cell.setColspan(1);
            }

            List<GridColumn<?, ?>> columnOrder = new ArrayList<GridColumn<?, ?>>(
                    section.grid.getColumns());
            // Set colspan for grouped cells
            for (Set<GridColumn<?, ?>> group : cellGroups.keySet()) {
                if (!checkCellGroupAndOrder(columnOrder, group)) {
                    cellGroups.get(group).setColspan(1);
                } else {
                    int colSpan = group.size();
                    for (GridColumn<?, ?> column : group) {
                        if (!column.isVisible()) {
                            --colSpan;
                        }
                    }
                    cellGroups.get(group).setColspan(colSpan);
                }
            }

        }

        private boolean checkCellGroupAndOrder(
                List<GridColumn<?, ?>> columnOrder,
                Set<GridColumn<?, ?>> cellGroup) {
            if (!columnOrder.containsAll(cellGroup)) {
                return false;
            }

            for (int i = 0; i < columnOrder.size(); ++i) {
                if (!cellGroup.contains(columnOrder.get(i))) {
                    continue;
                }

                for (int j = 1; j < cellGroup.size(); ++j) {
                    if (!cellGroup.contains(columnOrder.get(i + j))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        protected void addCell(GridColumn<?, ?> column) {
            CELLTYPE cell = createCell();
            cell.setSection(getSection());
            cells.put(column, cell);
        }

        protected void removeCell(GridColumn<?, ?> column) {
            cells.remove(column);
        }

        protected abstract CELLTYPE createCell();

        protected GridStaticSection<?> getSection() {
            return section;
        }

        protected void setSection(GridStaticSection<?> section) {
            this.section = section;
        }

        /**
         * Returns the custom style name for this row.
         * 
         * @return the style name or null if no style name has been set
         */
        public String getStyleName() {
            return styleName;
        }

        /**
         * Sets a custom style name for this row.
         * 
         * @param styleName
         *            the style name to set or null to not use any style name
         */
        public void setStyleName(String styleName) {
            this.styleName = styleName;
            section.requestSectionRefresh();
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
     * Inserts a new row at the given position. Shifts the row currently at that
     * position and any subsequent rows down (adds one to their indices).
     * 
     * @param index
     *            the position at which to insert the row
     * @return the new row
     * 
     * @throws IndexOutOfBoundsException
     *             if the index is out of bounds
     * @see #appendRow()
     * @see #prependRow()
     * @see #removeRow(int)
     * @see #removeRow(StaticRow)
     */
    public ROWTYPE addRow(int index) {
        ROWTYPE row = createRow();
        row.setSection(this);
        for (int i = 0; i < getGrid().getColumnCount(); ++i) {
            row.addCell(grid.getColumn(i));
        }
        rows.add(index, row);

        requestSectionRefresh();
        return row;
    }

    /**
     * Adds a new row at the top of this section.
     * 
     * @return the new row
     * @see #appendRow()
     * @see #addRow(int)
     * @see #removeRow(int)
     * @see #removeRow(StaticRow)
     */
    public ROWTYPE prependRow() {
        return addRow(0);
    }

    /**
     * Adds a new row at the bottom of this section.
     * 
     * @return the new row
     * @see #prependRow()
     * @see #addRow(int)
     * @see #removeRow(int)
     * @see #removeRow(StaticRow)
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
     * @see #addRow(int)
     * @see #appendRow()
     * @see #prependRow()
     * @see #removeRow(StaticRow)
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
     * @see #addRow(int)
     * @see #appendRow()
     * @see #prependRow()
     * @see #removeRow(int)
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

    protected void addColumn(GridColumn<?, ?> column) {
        for (ROWTYPE row : rows) {
            row.addCell(column);
        }
    }

    protected void removeColumn(GridColumn<?, ?> column) {
        for (ROWTYPE row : rows) {
            row.removeCell(column);
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
