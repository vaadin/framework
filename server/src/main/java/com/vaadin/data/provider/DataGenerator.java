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
package com.vaadin.data.provider;

import java.io.Serializable;

import elemental.json.JsonObject;

/**
 * A data generator for {@link DataCommunicator}. Used to inject custom data to
 * data items sent to the client for extension purposes.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the data type
 *
 * @since 8.0
 */
@FunctionalInterface
public interface DataGenerator<T> extends Serializable {

    /**
     * Adds custom data for the given item to its serialized {@code JsonObject}
     * representation. This JSON object will be sent to client-side
     * DataProvider.
     *
     * @param item
     *            the data item being serialized
     * @param jsonObject
     *            the JSON object being sent to the client
     */
    void generateData(T item, JsonObject jsonObject);

    /**
     * Informs the {@code DataGenerator} that the given data item has been
     * dropped and is no longer needed. This method should clean up any unneeded
     * information stored for this item.
     *
     * @param item
     *            the dropped data item
     */
    public default void destroyData(T item) {
    }

    /**
     * Informs the {@code DataGenerator} that all data has been dropped. This
     * method should clean up any unneeded information stored for items.
     */
    public default void destroyAllData() {
    }

    /**
     * Informs the {@code DataGenerator} that a data object has been updated.
     * This method should update any unneeded information stored for given item.
     *
     * @param item
     *            the updated item
     */
    public default void refreshData(T item) {
    }
}
