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
package com.vaadin.tokka.server.communication.data;

import java.io.Serializable;

import elemental.json.JsonObject;

/**
 * Simple typed data generator for {@link DataProvider}.
 * 
 * @since
 */
public interface TypedDataGenerator<T> extends Serializable {

    /**
     * Adds data for given object to {@link JsonObject}. This JsonObject will be
     * sent to client-side DataSource.
     * 
     * @param data
     *            data object
     * @param jsonObject
     *            json object being sent to the client
     */
    void generateData(T data, JsonObject jsonObject);

    /**
     * Informs the {@link TypedDataGenerator} that given data has been dropped
     * and is no longer needed. This method should clean up any unneeded
     * information stored for this data.
     * 
     * @param data
     *            dropped data
     */
    public void destroyData(T data);
}