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
package com.vaadin.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import com.vaadin.data.selection.SelectionModel;
import com.vaadin.server.data.DataSource;

/**
 * A generic interface for components that show a list of data.
 *
 * @author Vaadin Ltd.
 * @param <T>
 *            the item data type
 * @param <SELECTIONMODEL>
 *            the selection logic supported by this listing
 * @since
 */
public interface Listing<T, SELECTIONMODEL extends SelectionModel<T>> extends
        Serializable {

    /**
     * Returns the source of data items used by this listing.
     *
     * @return the data source, not null
     */
    DataSource<T> getDataSource();

    /**
     * Sets the source of data items used by this listing. The data source is
     * queried for displayed items as needed.
     *
     * @param dataSource
     *            the data source, not null
     */
    void setDataSource(DataSource<T> dataSource);

    /**
     * Returns the selection model for this listing.
     * 
     * @return the selection model, not null
     */
    SELECTIONMODEL getSelectionModel();

    /**
     * Sets the collection of data items of this listing.
     *
     * @param items
     *            the data items to display
     * 
     */
    default void setItems(Collection<T> items) {
        setDataSource(DataSource.create(items));
    }

    /**
     * Sets the data items of this listing.
     *
     * @param items
     *            the data items to display
     */
    default void setItems(T... items) {
        setDataSource(DataSource.create(items));
    }

    /* SelectionModel helper methods */

    /**
     * Returns an immutable set of the currently selected items. The iteration
     * order of the items in the returned set is specified by the
     * {@linkplain #getSelectionModel() selection model} used.
     * 
     * @return the current selection
     * 
     * @see SelectionModel#getSelectedItems
     */
    default Set<T> getSelectedItems() {
        return getSelectionModel().getSelectedItems();
    }

    /**
     * Selects the given item. If the item is already selected, does nothing.
     * 
     * @param item
     *            the item to select, not null
     * 
     * @see SelectionModel#select
     */
    default void select(T item) {
        getSelectionModel().select(item);
    }

    /**
     * Deselects the given item. If the item is not currently selected, does
     * nothing.
     * 
     * @param item
     *            the item to deselect, not null
     * 
     * @see SelectionModel#deselect
     */
    default void deselect(T item) {
        getSelectionModel().deselect(item);
    }

    /**
     * Returns whether the given item is currently selected.
     * 
     * @param item
     *            the item to check, not null
     * @return {@code true} if the item is selected, {@code false} otherwise
     */
    default boolean isSelected(T item) {
        return getSelectionModel().isSelected(item);
    }
}
