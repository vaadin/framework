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
package com.vaadin.client.tokka.connectors.data;

import com.vaadin.client.data.DataSource;

import elemental.json.JsonObject;

/**
 * Marker interface for Connectors that have a {@link DataSource}.
 */
public interface HasDataSource {

    /**
     * Sets the data source for this Connector.
     * 
     * @param dataSource
     *            new data source
     */
    public void setDataSource(DataSource<JsonObject> dataSource);

    /**
     * Gets the current data source for this Connector.
     * 
     * @return data source
     */
    DataSource<JsonObject> getDataSource();

}
