/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.widget.grid;

import com.google.gwt.dom.client.TableCellElement;
import com.vaadin.client.widget.escalator.Cell;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.shared.ui.grid.GridConstants.Section;

/**
 * A data class which contains information which identifies a cell being the
 * target of an event from {@link Grid}.
 * <p>
 * Since this class follows the <code>Flyweight</code>-pattern any instance of
 * this object is subject to change without the user knowing it and so should
 * not be stored anywhere outside of the method providing these instances.
 *
 * @param <T>
 *            The row type of the grid. The row type is the POJO type from where
 *            the data is retrieved into the column cells.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class EventCellReference<T> extends CellReference<T> {

    private Section section;
    private TableCellElement element;

    /**
     * Constructs a cell reference for an event targeting a grid cell. Needs to
     * be populated using {@link #set(Cell, Section)}.
     *
     * @param grid
     *            the grid the event originates from
     *
     * @see EventCellReference
     */
    public EventCellReference(Grid<T> grid) {
        super(new RowReference<>(grid));
    }

    /**
     * Configures this CellReference and its internal RowReference to point to
     * the given Cell.
     *
     * @param targetCell
     *            the cell to point to
     * @param section
     *            the section the cell belongs to
     */
    public void set(Cell targetCell, Section section) {
        Grid<T> grid = getGrid();

        int columnIndexDOM = targetCell.getColumn();
        Column<?, T> column = null;
        if (columnIndexDOM >= 0
                && columnIndexDOM < grid.getVisibleColumns().size()) {
            column = grid.getVisibleColumns().get(columnIndexDOM);
        }

        int row = targetCell.getRow();
        // Row objects only make sense for body section of Grid.
        T rowObject;
        if (section == Section.BODY && row >= 0
                && row < grid.getDataSource().size()) {
            rowObject = grid.getDataSource().getRow(row);
        } else {
            rowObject = null;
        }

        // At least for now we don't need to have the actual TableRowElement
        // available.
        getRowReference().set(row, rowObject, null);

        int columnIndex = grid.getColumns().indexOf(column);
        set(columnIndexDOM, columnIndex, column);

        this.element = targetCell.getElement();
        this.section = section;
    }

    @Override
    public TableCellElement getElement() {
        return element;
    }

    /**
     * Is the cell reference for a cell in the header of the Grid.
     *
     * @since 7.5
     * @return <code>true</code> if referenced cell is in the header,
     *         <code>false</code> if not
     */
    public boolean isHeader() {
        return section == Section.HEADER;
    }

    /**
     * Is the cell reference for a cell in the body of the Grid.
     *
     * @since 7.5
     * @return <code>true</code> if referenced cell is in the body,
     *         <code>false</code> if not
     */
    public boolean isBody() {
        return section == Section.BODY;
    }

    /**
     * Is the cell reference for a cell in the footer of the Grid.
     *
     * @since 7.5
     * @return <code>true</code> if referenced cell is in the footer,
     *         <code>false</code> if not
     */
    public boolean isFooter() {
        return section == Section.FOOTER;
    }

    /**
     * Gets the Grid section where the referenced cell is.
     *
     * @since 7.5
     * @return grid section
     */
    public Section getSection() {
        return section;
    }
}
