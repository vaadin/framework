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
package com.vaadin.tokka.ui.components;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.server.communication.data.SelectionModel;

/**
 * Generic interface for Components that show a list of data.
 *
 * @param <T>
 *            data type for listing
 */
public interface Listing<T> extends Serializable {

    /**
     * Sets the {@link DataSource} used by this Listing.
     * 
     * @param data
     *            data source
     */
    void setDataSource(DataSource<T, ?> data);

    /**
     * Sets the options available for this Listing.
     * 
     * @param data
     *            collection of data
     */
    default void setItems(Collection<T> data) {
        setDataSource(DataSource.create(data));
    }

    /**
     * Sets the options available for this Listing.
     * 
     * @param data
     *            array of data
     */
    default void setItems(T... data) {
        setDataSource(DataSource.create(data));
    }

    /**
     * Returns the {@link DataSource} of this Listing.
     * 
     * @return data source
     */
    DataSource<T, ?> getDataSource();

    /**
     * Gets the {@link SelectionModel} for this Listing.
     * 
     * @return selection model
     */
    SelectionModel<T> getSelectionModel();

    /**
     * Sets the {@link SelectionModel} for this Listing.
     * 
     * @param model
     *            selection model
     */
    void setSelectionModel(SelectionModel<T> model);

    /* SelectionModel helper methods */

    /**
     * Gets the current selection from the {@link SelectionModel} of this
     * Listing.
     * 
     * @return collection of currently selected values
     * 
     * @see SelectionModel#getSelected
     */
    default Collection<T> getSelected() {
        return getSelectionModel().getSelected();
    }

    /**
     * Gets the first value from the current selection.
     * 
     * @return optional first selected value
     */
    default Optional<T> getFirstSelected() {
        return getSelected().stream().findFirst();
    }

    /**
     * Selects the given value.
     * 
     * @param bean
     *            value to select
     * 
     * @see SelectionModel#select
     */
    default void select(T bean) {
        getSelectionModel().select(bean);
    }

    /**
     * Deselects the given value.
     * 
     * @param bean
     *            value to deselect
     * 
     * @see SelectionModel#deselect
     */
    default void deselect(T bean) {
        getSelectionModel().deselect(bean);
    }
}
