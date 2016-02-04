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
 * Interface for a {@link DataSource} to inform of various changes in the back
 * end.
 * 
 * @param <T>
 *            data type
 */
public interface DataChangeHandler<T> extends Serializable {

    /**
     * This method is called when a generic change in the DataSource. All cached
     * data should be considered invalid.
     */
    void onDataChange();

    /**
     * This method is called when a data object has been added as the last
     * object in the back end.
     * 
     * @param data
     *            new data object
     */
    void onDataAppend(T data);

    /**
     * This method is called when a data object has been removed from the back
     * end.
     * 
     * @param data
     *            removed data object
     */
    void onDataRemove(T data);

    /**
     * This method is called when an existing data object has been updated in
     * the back end.
     * 
     * @param data
     *            updated data object
     */
    void onDataUpdate(T data);
}