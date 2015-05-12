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
package com.vaadin.client.widget.grid;

import com.google.gwt.dom.client.TableCellElement;
import com.vaadin.client.widget.escalator.Cell;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;

/**
 * A data class which contains information which identifies a cell being the
 * target of an event from {@link Grid}.
 * <p>
 * Since this class follows the <code>Flyweight</code>-pattern any instance of
 * this object is subject to change without the user knowing it and so should
 * not be stored anywhere outside of the method providing these instances.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class EventCellReference<T> extends CellReference<T> {

    private Grid<T> grid;
    private TableCellElement element;

    public EventCellReference(Grid<T> grid) {
        super(new RowReference<T>(grid));
        this.grid = grid;
    }

    /**
     * Sets the RowReference and CellReference to point to given Cell.
     * 
     * @param targetCell
     *            cell to point to
     */
    public void set(Cell targetCell) {
        int row = targetCell.getRow();
        int columnIndexDOM = targetCell.getColumn();
        Column<?, T> column = grid.getVisibleColumns().get(columnIndexDOM);

        // At least for now we don't need to have the actual TableRowElement
        // available.
        getRowReference().set(row, grid.getDataSource().getRow(row), null);
        int columnIndex = grid.getColumns().indexOf(column);
        set(columnIndexDOM, columnIndex, column);

        this.element = targetCell.getElement();
    }

    @Override
    public TableCellElement getElement() {
        return element;
    }
}
