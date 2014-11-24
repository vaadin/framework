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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container.Indexed;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.GridStaticSectionState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.CellState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.RowState;
import com.vaadin.ui.Component;

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
    abstract static class StaticRow<CELLTYPE extends StaticCell> implements
            Serializable {

        private RowState rowState = new RowState();
        protected GridStaticSection<?> section;
        private Map<Object, CELLTYPE> cells = new LinkedHashMap<Object, CELLTYPE>();
        private Map<Set<CELLTYPE>, CELLTYPE> cellGroups = new HashMap<Set<CELLTYPE>, CELLTYPE>();

        protected StaticRow(GridStaticSection<?> section) {
            this.section = section;
        }

        protected void addCell(Object propertyId) {
            CELLTYPE cell = createCell();
            cell.setColumnId(section.grid.getColumn(propertyId).getState().id);
            cells.put(propertyId, cell);
            rowState.cells.add(cell.getCellState());
        }

        protected void removeCell(Object propertyId) {
            CELLTYPE cell = cells.remove(propertyId);
            if (cell != null) {
                Set<CELLTYPE> cellGroupForCell = getCellGroupForCell(cell);
                if (cellGroupForCell != null) {
                    removeCellFromGroup(cell, cellGroupForCell);
                }
                rowState.cells.remove(cell.getCellState());
            }
        }

        private void removeCellFromGroup(CELLTYPE cell, Set<CELLTYPE> cellGroup) {
            String columnId = cell.getColumnId();
            for (Set<String> group : rowState.cellGroups.keySet()) {
                if (group.contains(columnId)) {
                    if (group.size() > 2) {
                        cellGroup.remove(cell);
                        group.remove(columnId);
                    } else {
                        rowState.cellGroups.remove(group);
                        cellGroups.remove(cellGroup);
                    }
                    return;
                }
            }
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
         * Returns the cell for the given property id on this row. If the column
         * is merged returned cell is the cell for the whole group.
         * 
         * @param propertyId
         *            the property id of the column
         * @return the cell for the given property, merged cell for merged
         *         properties, null if not found
         */
        public CELLTYPE getCell(Object propertyId) {
            CELLTYPE cell = cells.get(propertyId);
            Set<CELLTYPE> cellGroup = getCellGroupForCell(cell);
            if (cellGroup != null) {
                return cellGroups.get(cellGroup);
            }
            return cell;
        }

        /**
         * Merges columns cells in a row
         * 
         * @param propertyIds
         *            The property ids of columns to merge
         * @return The remaining visible cell after the merge
         */
        public CELLTYPE join(Object... propertyIds) {
            assert propertyIds.length > 1 : "You need to merge at least 2 properties";

            Set<CELLTYPE> cells = new HashSet<CELLTYPE>();
            for (int i = 0; i < propertyIds.length; ++i) {
                cells.add(getCell(propertyIds[i]));
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
            assert cells.length > 1 : "You need to merge at least 2 cells";

            return join(new HashSet<CELLTYPE>(Arrays.asList(cells)));
        }

        protected CELLTYPE join(Set<CELLTYPE> cells) {
            for (CELLTYPE cell : cells) {
                if (getCellGroupForCell(cell) != null) {
                    throw new IllegalArgumentException("Cell already merged");
                } else if (!this.cells.containsValue(cell)) {
                    throw new IllegalArgumentException(
                            "Cell does not exist on this row");
                }
            }

            // Create new cell data for the group
            CELLTYPE newCell = createCell();

            Set<String> columnGroup = new HashSet<String>();
            for (CELLTYPE cell : cells) {
                columnGroup.add(cell.getColumnId());
            }
            rowState.cellGroups.put(columnGroup, newCell.getCellState());
            cellGroups.put(cells, newCell);
            return newCell;
        }

        private Set<CELLTYPE> getCellGroupForCell(CELLTYPE cell) {
            for (Set<CELLTYPE> group : cellGroups.keySet()) {
                if (group.contains(cell)) {
                    return group;
                }
            }
            return null;
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

        private void setColumnId(String id) {
            cellState.columnId = id;
        }

        private String getColumnId() {
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

        protected CellState getCellState() {
            return cellState;
        }

        /**
         * Sets the text displayed in this cell.
         * 
         * @param text
         *            a plain text caption
         */
        public void setText(String text) {
            cellState.text = text;
            cellState.type = GridStaticCellType.TEXT;
            row.section.markAsDirty();
        }

        /**
         * Returns the text displayed in this cell.
         * 
         * @return the plain text caption
         */
        public String getText() {
            if (cellState.type != GridStaticCellType.TEXT) {
                throw new IllegalStateException(
                        "Cannot fetch Text from a cell with type "
                                + cellState.type);
            }
            return cellState.text;
        }

        /**
         * Returns the HTML content displayed in this cell.
         * 
         * @return the html
         * 
         */
        public String getHtml() {
            if (cellState.type != GridStaticCellType.HTML) {
                throw new IllegalStateException(
                        "Cannot fetch HTML from a cell with type "
                                + cellState.type);
            }
            return cellState.html;
        }

        /**
         * Sets the HTML content displayed in this cell.
         * 
         * @param html
         *            the html to set
         */
        public void setHtml(String html) {
            cellState.html = html;
            cellState.type = GridStaticCellType.HTML;
            row.section.markAsDirty();
        }

        /**
         * Returns the component displayed in this cell.
         * 
         * @return the component
         */
        public Component getComponent() {
            if (cellState.type != GridStaticCellType.WIDGET) {
                throw new IllegalStateException(
                        "Cannot fetch Component from a cell with type "
                                + cellState.type);
            }
            return (Component) cellState.connector;
        }

        /**
         * Sets the component displayed in this cell.
         * 
         * @param component
         *            the component to set
         */
        public void setComponent(Component component) {
            component.setParent(row.section.grid);
            cellState.connector = component;
            cellState.type = GridStaticCellType.WIDGET;
            row.section.markAsDirty();
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
        if (getSectionState().visible != visible) {
            getSectionState().visible = visible;
            markAsDirty();
        }
    }

    /**
     * Returns the visibility of this section.
     * 
     * @return true if visible, false otherwise.
     */
    public boolean isVisible() {
        return getSectionState().visible;
    }

    /**
     * Removes the row at the given position.
     * 
     * @param index
     *            the position of the row
     * 
     * @throws IndexOutOfBoundsException
     *             if the index is out of bounds
     * @see #removeRow(StaticRow)
     * @see #addRowAt(int)
     * @see #appendRow()
     * @see #prependRow()
     */
    public ROWTYPE removeRow(int rowIndex) {
        ROWTYPE row = rows.remove(rowIndex);
        getSectionState().rows.remove(rowIndex);

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
     * @see #removeRow(int)
     * @see #addRowAt(int)
     * @see #appendRow()
     * @see #prependRow()
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
     * @see #appendRow()
     * @see #addRowAt(int)
     * @see #removeRow(StaticRow)
     * @see #removeRow(int)
     */
    public ROWTYPE prependRow() {
        return addRowAt(0);
    }

    /**
     * Adds a new row at the bottom of this section.
     * 
     * @return the new row
     * @see #prependRow()
     * @see #addRowAt(int)
     * @see #removeRow(StaticRow)
     * @see #removeRow(int)
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
     * @see #appendRow()
     * @see #prependRow()
     * @see #removeRow(StaticRow)
     * @see #removeRow(int)
     */
    public ROWTYPE addRowAt(int index) {
        ROWTYPE row = createRow();
        rows.add(index, row);
        getSectionState().rows.add(index, row.getRowState());

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

    protected abstract GridStaticSectionState getSectionState();

    protected abstract ROWTYPE createRow();

    /**
     * Informs the grid that state has changed and it should be redrawn.
     */
    protected void markAsDirty() {
        grid.markAsDirty();
    }

    /**
     * Removes a column for given property id from the section.
     * 
     * @param propertyId
     *            property to be removed
     */
    protected void removeColumn(Object propertyId) {
        for (ROWTYPE row : rows) {
            row.removeCell(propertyId);
        }
    }

    /**
     * Adds a column for given property id to the section.
     * 
     * @param propertyId
     *            property to be added
     */
    protected void addColumn(Object propertyId) {
        for (ROWTYPE row : rows) {
            row.addCell(propertyId);
        }
    }

    /**
     * Performs a sanity check that section is in correct state.
     * 
     * @throws IllegalStateException
     *             if merged cells are not i n continuous range
     */
    protected void sanityCheck() throws IllegalStateException {
        List<String> columnOrder = grid.getState().columnOrder;
        for (ROWTYPE row : rows) {
            for (Set<String> cellGroup : row.getRowState().cellGroups.keySet()) {
                if (!checkCellGroupAndOrder(columnOrder, cellGroup)) {
                    throw new IllegalStateException(
                            "Not all merged cells were in a continuous range.");
                }
            }
        }
    }

    private boolean checkCellGroupAndOrder(List<String> columnOrder,
            Set<String> cellGroup) {
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
}
