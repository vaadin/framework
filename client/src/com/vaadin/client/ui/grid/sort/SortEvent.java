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

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.grid.Grid;

/**
 * A sort event, fired by the Grid when it needs its data source to provide data
 * sorted in a specific manner.
 *
 * @since
 * @author Vaadin Ltd
 */
public class SortEvent<T> extends GwtEvent<SortEventHandler<?>> {

    private static final Type<SortEventHandler<?>> TYPE = new Type<SortEventHandler<?>>();

    private final Grid<T> grid;
    private final List<SortOrder> order;

    /**
     * Creates a new Sort Event. All provided parameters are final, and passed
     * on as-is.
     *
     * @param grid
     *            a grid reference
     * @param datasource
     *            a reference to the grid's data source
     * @param order
     *            an array dictating the desired sort order of the data source
     */
    public SortEvent(Grid<T> grid, List<SortOrder> order) {
        this.grid = grid;
        this.order = order;
    }

    @Override
    public Type<SortEventHandler<?>> getAssociatedType() {
        return TYPE;
    }

    /**
     * Static access to the GWT event type identifier associated with this Event
     * class
     *
     * @return a type object, uniquely describing this event type.
     */
    public static Type<SortEventHandler<?>> getType() {
        return TYPE;
    }

    /**
     * Get access to the Grid that fired this event
     *
     * @return the grid instance
     */
    @Override
    public Grid<T> getSource() {
        return grid;
    }

    /**
     * Get access to the Grid that fired this event
     *
     * @return the grid instance
     */
    public Grid<T> getGrid() {
        return grid;
    }

    /**
     * Access the data source of the Grid that fired this event
     *
     * @return a data source instance
     */
    public DataSource<T> getDataSource() {
        return grid.getDataSource();
    }

    /**
     * Get the sort ordering that is to be applied to the Grid
     *
     * @return a list of sort order objects
     */
    public List<SortOrder> getOrder() {
        return order;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void dispatch(SortEventHandler<?> handler) {
        ((SortEventHandler<T>) handler).sort(this);
    }

}
