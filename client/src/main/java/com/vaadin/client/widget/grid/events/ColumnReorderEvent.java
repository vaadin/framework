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
package com.vaadin.client.widget.grid.events;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.client.widgets.Grid.Column;

/**
 * An event for notifying that the columns in the Grid have been reordered.
 *
 * @param <T>
 *            The row type of the grid. The row type is the POJO type from where
 *            the data is retrieved into the column cells.
 * @since 7.5.0
 * @author Vaadin Ltd
 */
public class ColumnReorderEvent<T> extends GwtEvent<ColumnReorderHandler<T>> {

    /**
     * Handler type.
     */
    private static final Type<ColumnReorderHandler<?>> TYPE = new Type<>();

    /**
     * Returns the associated handler type.
     *
     * @return the handler type
     */
    public static final Type<ColumnReorderHandler<?>> getType() {
        return TYPE;
    }

    private final List<Column<?, T>> oldColumnOrder;

    private final List<Column<?, T>> newColumnOrder;

    private final boolean userOriginated;

    /**
     * Constructs a reorder event for grid columns.
     *
     * @param oldColumnOrder
     *            the old order
     * @param newColumnOrder
     *            the new order
     * @param userOriginated
     *            {@code true} if the event was triggered by user interaction,
     *            {@code false} otherwise
     */
    public ColumnReorderEvent(List<Column<?, T>> oldColumnOrder,
            List<Column<?, T>> newColumnOrder, boolean userOriginated) {
        this.oldColumnOrder = oldColumnOrder;
        this.newColumnOrder = newColumnOrder;
        this.userOriginated = userOriginated;
    }

    /**
     * Gets the ordering of columns prior to this event.
     *
     * @return the list of columns in the grid's order prior to this event
     */
    public List<Column<?, T>> getOldColumnOrder() {
        return oldColumnOrder;
    }

    /**
     * Gets the new ordering of columns.
     *
     * @return the list of columns in the grid's current order
     */
    public List<Column<?, T>> getNewColumnOrder() {
        return newColumnOrder;
    }

    /**
     * Check whether this event originated from the user reordering columns or
     * via API call.
     *
     * @return {@code true} if columns were reordered by the user, {@code false}
     *         if not
     */
    public boolean isUserOriginated() {
        return userOriginated;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Type<ColumnReorderHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(ColumnReorderHandler<T> handler) {
        handler.onColumnReorder(this);
    }

}
