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
import com.vaadin.client.widgets.Grid.Column;

/**
 * An event for notifying that the columns in the Grid's have changed
 * visibility.
 * 
 * @param <T>
 *            The row type of the grid. The row type is the POJO type from where
 *            the data is retrieved into the column cells.
 * @since 7.5.0
 * @author Vaadin Ltd
 */
public class ColumnVisibilityChangeEvent<T> extends
        GwtEvent<ColumnVisibilityChangeHandler<T>> {

    private final static Type<ColumnVisibilityChangeHandler<?>> TYPE = new Type<ColumnVisibilityChangeHandler<?>>();

    public static final Type<ColumnVisibilityChangeHandler<?>> getType() {
        return TYPE;
    }

    private final Column<?, T> column;

    private final boolean userOriginated;

    private final boolean hidden;

    public ColumnVisibilityChangeEvent(Column<?, T> column, boolean hidden,
            boolean userOriginated) {
        this.column = column;
        this.hidden = hidden;
        this.userOriginated = userOriginated;
    }

    /**
     * Returns the column where the visibility change occurred.
     * 
     * @return the column where the visibility change occurred.
     */
    public Column<?, T> getColumn() {
        return column;
    }

    /**
     * Was the column set hidden or visible.
     * 
     * @return <code>true</code> if the column was hidden <code>false</code> if
     *         it was set visible
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Is the visibility change triggered by user.
     * 
     * @return <code>true</code> if the change was triggered by user,
     *         <code>false</code> if not
     */
    public boolean isUserOriginated() {
        return userOriginated;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ColumnVisibilityChangeHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(ColumnVisibilityChangeHandler<T> handler) {
        handler.onVisibilityChange(this);
    }

}
