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
import com.vaadin.client.widget.grid.selection.SelectionModel;

/**
 * A select all event, fired by the Grid when it needs all rows in data source
 * to be selected, OR when all rows have been selected and are now deselected.
 *
 * @since 7.4
 * @author Vaadin Ltd
 * @param <T>
 *            the type of the rows in grid
 */
public class SelectAllEvent<T> extends GwtEvent<SelectAllHandler<T>> {

    /**
     * Handler type.
     */
    private static final Type<SelectAllHandler<?>> TYPE = new Type<>();;

    private final SelectionModel<T> selectionModel;
    private final boolean allSelected;

    /**
     * Constructs a new select all event when all rows in grid are selected.
     *
     * @param selectionModel
     *            the selection model in use
     */
    public SelectAllEvent(SelectionModel<T> selectionModel) {
        this(selectionModel, true);
    }

    /**
     *
     *
     * @param selectionModel
     *            the selection model in use
     * @param allSelected
     *            {@code true} for all selected, {@code false} for all
     *            deselected
     */
    public SelectAllEvent(SelectionModel<T> selectionModel,
            boolean allSelected) {
        this.selectionModel = selectionModel;
        this.allSelected = allSelected;
    }

    /**
     * Gets the type of the handlers for this event.
     *
     * @return the handler type
     */
    public static final Type<SelectAllHandler<?>> getType() {
        return TYPE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Type<SelectAllHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(SelectAllHandler<T> handler) {
        handler.onSelectAll(this);
    }

    /**
     * The selection model in use.
     *
     * @return the selection model
     */
    public SelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    /**
     * Returns whether all the rows were selected, or deselected. Deselection
     * can only happen if all rows were previously selected.
     *
     * @return {@code true} for selecting all rows, or {@code false} for
     *         deselecting all rows
     */
    public boolean isAllSelected() {
        return allSelected;
    }
}
