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
package com.vaadin.client.widget.grid.events;

import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;

/**
 * Base class for editor events.
 */
public abstract class EditorEvent extends GwtEvent<EditorEventHandler> {
    public static final Type<EditorEventHandler> TYPE = new Type<EditorEventHandler>();

    private CellReference<?> cell;

    protected EditorEvent(CellReference<?> cell) {
        this.cell = cell;
    }

    @Override
    public Type<EditorEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Get a reference to the Grid that fired this Event.
     * 
     * @return a Grid reference
     */
    @SuppressWarnings("unchecked")
    public <T> Grid<T> getGrid() {
        return (Grid<T>) cell.getGrid();
    }

    /**
     * Get a reference to the cell that was active when this Event was fired.
     * NOTE: do <i>NOT</i> rely on this information remaining accurate after
     * leaving the event handler.
     * 
     * @return a cell reference
     */
    @SuppressWarnings("unchecked")
    public <T> CellReference<T> getCell() {
        return (CellReference<T>) cell;
    }

    /**
     * Get a reference to the row that was active when this Event was fired.
     * NOTE: do <i>NOT</i> rely on this information remaining accurate after
     * leaving the event handler.
     * 
     * @return a row data object
     */
    @SuppressWarnings("unchecked")
    public <T> T getRow() {
        return (T) cell.getRow();
    }

    /**
     * Get the index of the row that was active when this Event was fired. NOTE:
     * do <i>NOT</i> rely on this information remaining accurate after leaving
     * the event handler.
     * 
     * @return an integer value
     */
    public int getRowIndex() {
        return cell.getRowIndex();
    }

    /**
     * Get a reference to the column that was active when this Event was fired.
     * NOTE: do <i>NOT</i> rely on this information remaining accurate after
     * leaving the event handler.
     * 
     * @return a column object
     */
    @SuppressWarnings("unchecked")
    public <C, T> Column<C, T> getColumn() {
        return (Column<C, T>) cell.getColumn();
    }

    /**
     * Get the index of the column that was active when this Event was fired.
     * NOTE: do <i>NOT</i> rely on this information remaining accurate after
     * leaving the event handler.
     * 
     * @return an integer value
     */
    public int getColumnIndex() {
        return cell.getColumnIndex();
    }

}