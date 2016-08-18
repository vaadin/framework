/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import com.vaadin.client.widgets.Grid.Column;

/**
 * An event for notifying that the columns in the Grid have been resized.
 *
 * @param <T>
 *            The row type of the grid. The row type is the POJO type from where
 *            the data is retrieved into the column cells.
 * @since 7.6
 * @author Vaadin Ltd
 */
public class ColumnResizeEvent<T> extends GwtEvent<ColumnResizeHandler<T>> {

    /**
     * Handler type.
     */
    private final static Type<ColumnResizeHandler<?>> TYPE = new Type<ColumnResizeHandler<?>>();

    private Column<?, T> column;

    /**
     * @param column
     */
    public ColumnResizeEvent(Column<?, T> column) {
        this.column = column;
    }

    public static final Type<ColumnResizeHandler<?>> getType() {
        return TYPE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Type<ColumnResizeHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(ColumnResizeHandler<T> handler) {
        handler.onColumnResize(this);
    }

    /**
     * @return the column
     */
    public Column<?, T> getColumn() {
        return column;
    }
}
