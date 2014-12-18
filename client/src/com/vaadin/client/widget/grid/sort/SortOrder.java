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
package com.vaadin.client.widget.grid.sort;

import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.data.sort.SortDirection;

/**
 * Sort order descriptor. Contains column and direction references.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class SortOrder {

    private final Grid.Column<?, ?> column;
    private final SortDirection direction;

    /**
     * Create a sort order descriptor with a default sorting direction value of
     * {@link SortDirection#ASCENDING}.
     * 
     * @param column
     *            a grid column descriptor object
     */
    public SortOrder(Grid.Column<?, ?> column) {
        this(column, SortDirection.ASCENDING);
    }

    /**
     * Create a sort order descriptor.
     * 
     * @param column
     *            a grid column descriptor object
     * @param direction
     *            a sorting direction value (ascending or descending)
     */
    public SortOrder(Grid.Column<?, ?> column, SortDirection direction) {
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
    public Grid.Column<?, ?> getColumn() {
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

    /**
     * Returns a new SortOrder object with the sort direction reversed.
     * 
     * @return a new sort order object
     */
    public SortOrder getOpposite() {
        return new SortOrder(column, direction.getOpposite());
    }
}
