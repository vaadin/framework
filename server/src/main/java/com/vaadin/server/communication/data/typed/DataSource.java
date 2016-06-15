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
package com.vaadin.server.communication.data.typed;

import java.io.Serializable;

/**
 * Minimal DataSource API for communication between the DataProvider and a back
 * end service.
 * 
 * @since
 * @param <T>
 *            data type
 */
public interface DataSource<T> extends Iterable<T>, Serializable {

    /**
     * Saves a data object to the back end. If it's a new object, it should be
     * created in the back end. Existing objects with changes should be stored.
     * 
     * @param data
     *            data object to save
     */
    void save(T data);

    /**
     * Removes the given data object from the back end.
     * 
     * @param data
     *            data object to remove
     */
    void remove(T data);

    /**
     * Adds a new DataChangeHandler to this DataSource. DataChangeHandler is
     * called when changes occur in DataSource.
     * 
     * @param handler
     *            data change handler
     */
    void addDataChangeHandler(DataChangeHandler<T> handler);

    /**
     * Removed a DataChangeHandler from this DataSource.
     * 
     * @param handler
     *            data change handler
     */
    void removeDataChangeHandler(DataChangeHandler<T> handler);

}