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
package com.vaadin.shared.tokka.data;

import com.vaadin.shared.communication.ClientRpc;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * RPC interface used by DataProvider to send data to the client-side.
 * 
 * @since
 */
public interface DataCommunicatorClientRpc extends ClientRpc {

    /**
     * Informs the client-side DataSource that all data has been invalidated.
     * 
     * @param size
     *            size of the data source
     */
    void reset(int size);

    /**
     * Sets the data of the client-side DataSource to match the given data
     * starting from given index.
     * <p>
     * <strong>Note:</strong> This method will override any existing data in the
     * range starting from first index with the length of the data array.
     * 
     * @param firstIndex
     *            first index to update
     * @param data
     *            array of new data
     */
    void setData(int firstIndex, JsonArray data);

    /**
     * Adds a new data object to the client-side DataSource. Client-side will
     * request the new data if needed.
     * 
     * @param index
     *            added row index
     */
    void add(int index);

    /**
     * Removes data with given index from the client-side DataSource.
     * 
     * @param index
     *            removed row index
     */
    void drop(int index);

    /**
     * Updates an array of objects based on their identifying key.
     * 
     * @param data
     *            array of updated data
     */
    void updateData(JsonArray data);
}