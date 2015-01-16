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
package com.vaadin.client.widget.grid.datasources;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.widget.grid.sort.SortEvent;
import com.vaadin.client.widget.grid.sort.SortHandler;
import com.vaadin.client.widget.grid.sort.SortOrder;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.data.sort.SortDirection;

/**
 * Provides sorting facility from Grid for the {@link ListDataSource} in-memory
 * data source.
 * 
 * @author Vaadin Ltd
 * @param <T>
 *            Grid row data type
 * @since 7.4
 */
public class ListSorter<T> {

    private Grid<T> grid;
    private Map<Grid.Column<?, T>, Comparator<?>> comparators;
    private HandlerRegistration sortHandlerRegistration;

    public ListSorter(Grid<T> grid) {

        if (grid == null) {
            throw new IllegalArgumentException("Grid can not be null");
        }

        this.grid = grid;
        comparators = new HashMap<Grid.Column<?, T>, Comparator<?>>();

        sortHandlerRegistration = grid.addSortHandler(new SortHandler<T>() {
            @Override
            public void sort(SortEvent<T> event) {
                ListSorter.this.sort(event.getOrder());
            }
        });
    }

    /**
     * Detach this Sorter from the Grid. This unregisters the sort event handler
     * which was used to apply sorting to the ListDataSource.
     */
    public void removeFromGrid() {
        sortHandlerRegistration.removeHandler();
    }

    /**
     * Assign or remove a comparator for a column. This comparator method, if
     * defined, is always used in favour of 'natural' comparison of objects
     * (i.e. the compareTo of objects implementing the Comparable interface,
     * which includes all standard data classes like String, Number derivatives
     * and Dates). Any existing comparator can be removed by passing in a
     * non-null GridColumn and a null Comparator.
     * 
     * @param column
     *            a grid column. May not be null.
     * @param comparator
     *            comparator method for the values returned by the grid column.
     *            If null, any existing comparator is removed.
     */
    public <C> void setComparator(Grid.Column<C, T> column,
            Comparator<C> comparator) {
        if (column == null) {
            throw new IllegalArgumentException(
                    "Column reference can not be null");
        }
        if (comparator == null) {
            comparators.remove(column);
        } else {
            comparators.put(column, comparator);
        }
    }

    /**
     * Retrieve the comparator assigned for a specific grid column.
     * 
     * @param column
     *            a grid column. May not be null.
     * @return a comparator, or null if no comparator for the specified grid
     *         column has been set.
     */
    @SuppressWarnings("unchecked")
    public <C> Comparator<C> getComparator(Grid.Column<C, T> column) {
        if (column == null) {
            throw new IllegalArgumentException(
                    "Column reference can not be null");
        }
        return (Comparator<C>) comparators.get(column);
    }

    /**
     * Remove all comparator mappings. Useful if the data source has changed but
     * this Sorter is being re-used.
     */
    public void clearComparators() {
        comparators.clear();
    }

    /**
     * Apply sorting to the current ListDataSource.
     * 
     * @param order
     *            the sort order list provided by the grid sort event
     */
    private void sort(final List<SortOrder> order) {
        DataSource<T> ds = grid.getDataSource();
        if (!(ds instanceof ListDataSource)) {
            throw new IllegalStateException("Grid " + grid
                    + " data source is not a ListDataSource!");
        }

        ((ListDataSource<T>) ds).sort(new Comparator<T>() {

            @Override
            @SuppressWarnings({ "rawtypes", "unchecked" })
            public int compare(T a, T b) {

                for (SortOrder o : order) {

                    Grid.Column column = o.getColumn();
                    Comparator cmp = ListSorter.this.comparators.get(column);
                    int result = 0;
                    Object value_a = column.getValue(a);
                    Object value_b = column.getValue(b);
                    if (cmp != null) {
                        result = cmp.compare(value_a, value_b);
                    } else {
                        if (!(value_a instanceof Comparable)) {
                            throw new IllegalStateException("Column " + column
                                    + " has no assigned comparator and value "
                                    + value_a + " isn't naturally comparable");
                        }
                        result = ((Comparable) value_a).compareTo(value_b);
                    }

                    if (result != 0) {
                        return o.getDirection() == SortDirection.ASCENDING ? result
                                : -result;
                    }
                }

                if (order.size() > 0) {
                    return order.get(0).getDirection() == SortDirection.ASCENDING ? a
                            .hashCode() - b.hashCode()
                            : b.hashCode() - a.hashCode();
                }
                return a.hashCode() - b.hashCode();
            }
        });
    }
}
