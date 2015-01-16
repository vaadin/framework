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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.data.sort.SortDirection;

/**
 * Fluid Sort descriptor object.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class Sort {

    private final Sort previous;
    private final SortOrder order;
    private final int count;

    /**
     * Basic constructor, used by the {@link #by(GridColumn)} and
     * {@link #by(GridColumn, SortDirection)} methods.
     * 
     * @param column
     *            a grid column
     * @param direction
     *            a sort direction
     */
    private Sort(Grid.Column<?, ?> column, SortDirection direction) {
        previous = null;
        count = 1;
        order = new SortOrder(column, direction);
    }

    /**
     * Extension constructor. Performs object equality checks on all previous
     * Sort objects in the chain to make sure that the column being passed in
     * isn't already used earlier (which would indicate a bug). If the column
     * has been used before, this constructor throws an
     * {@link IllegalStateException}.
     * 
     * @param previous
     *            the sort instance that the new sort instance is to extend
     * @param column
     *            a (previously unused) grid column reference
     * @param direction
     *            a sort direction
     */
    private Sort(Sort previous, Grid.Column<?, ?> column,
            SortDirection direction) {
        this.previous = previous;
        count = previous.count + 1;
        order = new SortOrder(column, direction);

        Sort s = previous;
        while (s != null) {
            if (s.order.getColumn() == column) {
                throw new IllegalStateException(
                        "Can not sort along the same column twice");
            }
            s = s.previous;
        }
    }

    /**
     * Start building a Sort order by sorting a provided column in ascending
     * order.
     * 
     * @param column
     *            a grid column object reference
     * @return a sort instance, typed to the grid data type
     */
    public static Sort by(Grid.Column<?, ?> column) {
        return by(column, SortDirection.ASCENDING);
    }

    /**
     * Start building a Sort order by sorting a provided column.
     * 
     * @param column
     *            a grid column object reference
     * @param direction
     *            indicator of sort direction - either ascending or descending
     * @return a sort instance, typed to the grid data type
     */
    public static Sort by(Grid.Column<?, ?> column, SortDirection direction) {
        return new Sort(column, direction);
    }

    /**
     * Continue building a Sort order. The provided column is sorted in
     * ascending order if the previously added columns have been evaluated as
     * equals.
     * 
     * @param column
     *            a grid column object reference
     * @return a sort instance, typed to the grid data type
     */
    public Sort then(Grid.Column<?, ?> column) {
        return then(column, SortDirection.ASCENDING);
    }

    /**
     * Continue building a Sort order. The provided column is sorted in
     * specified order if the previously added columns have been evaluated as
     * equals.
     * 
     * @param column
     *            a grid column object reference
     * @param direction
     *            indicator of sort direction - either ascending or descending
     * @return a sort instance, typed to the grid data type
     */
    public Sort then(Grid.Column<?, ?> column, SortDirection direction) {
        return new Sort(this, column, direction);
    }

    /**
     * Build a sort order list. This method is called internally by Grid when
     * calling {@link com.vaadin.client.ui.grid.Grid#sort(Sort)}, but can also
     * be called manually to create a SortOrder list, which can also be provided
     * directly to Grid.
     * 
     * @return a sort order list.
     */
    public List<SortOrder> build() {

        List<SortOrder> order = new ArrayList<SortOrder>(count);

        Sort s = this;
        for (int i = count - 1; i >= 0; --i) {
            order.add(0, s.order);
            s = s.previous;
        }

        return order;
    }
}
