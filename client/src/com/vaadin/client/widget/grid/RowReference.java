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

import com.vaadin.client.ui.grid.Grid;

/**
 * A data class which contains information which identifies a row in a
 * {@link Grid}.
 * <p>
 * Since this class follows the <code>Flyweight</code>-pattern any instance of
 * this object is subject to change without the user knowing it and so should
 * not be stored anywhere outside of the method providing these instances.
 * 
 * @param <T>
 *            the row object type
 */
public class RowReference<T> {
    private final Grid<T> grid;

    private int rowIndex;
    private T row;

    /**
     * Creates a new row reference for the given grid.
     * 
     * @param grid
     *            the grid that the row belongs to
     */
    public RowReference(Grid<T> grid) {
        this.grid = grid;
    }

    /**
     * Sets the identifying information for this row.
     * 
     * @param rowIndex
     *            the index of the row
     * @param row
     *            the row object
     */
    public void set(int rowIndex, T row) {
        this.rowIndex = rowIndex;
        this.row = row;
    }

    /**
     * Gets the grid that contains the referenced row.
     * 
     * @return the grid that contains referenced row
     */
    public Grid<T> getGrid() {
        return grid;
    }

    /**
     * Gets the row index of the row.
     * 
     * @return the index of the row
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Gets the row data object.
     * 
     * @return the row object
     */
    public T getRow() {
        return row;
    }

}