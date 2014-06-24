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
package com.vaadin.client.ui.grid.sort;

import com.vaadin.client.ui.grid.GridColumn;
import com.vaadin.shared.ui.grid.SortDirection;

/**
 * Sort order descriptor. Contains column and direction references.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @param T
 *            grid data type
 */
public class SortOrder {

    private final GridColumn<?, ?> column;
    private final SortDirection direction;

    /**
     * Create a sort order descriptor.
     * 
     * @param column
     *            a grid column descriptor object
     * @param direction
     *            a sorting direction value (ascending or descending)
     */
    public SortOrder(GridColumn<?, ?> column, SortDirection direction) {
        if (column == null) {
            throw new IllegalArgumentException(
                    "Grid column reference can not be null!");
        }
        if (direction == null) {
            throw new IllegalArgumentException(
                    "Direction value can not be null!");
        }
        this.column = column;
        this.direction = direction;
    }

    /**
     * Returns the {@link GridColumn} reference given in the constructor.
     * 
     * @return a grid column reference
     */
    public GridColumn<?, ?> getColumn() {
        return column;
    }

    /**
     * Returns the {@link SortDirection} value given in the constructor.
     * 
     * @return a sort direction value
     */
    public SortDirection getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return column.getHeaderCaption() + " (" + direction + ")";
    }

}
