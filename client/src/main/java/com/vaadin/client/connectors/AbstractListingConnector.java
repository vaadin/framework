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
package com.vaadin.client.connectors;

import com.vaadin.client.connectors.data.HasDataSource;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.abstractlisting.AbstractListingState;
import com.vaadin.ui.AbstractListing;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * A base connector class for {@link AbstractListing}.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.0
 */
public abstract class AbstractListingConnector extends AbstractFieldConnector
        implements HasDataSource {

    private DataSource<JsonObject> dataSource = null;

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource<JsonObject> getDataSource() {
        return dataSource;
    }

    /**
     * Returns the key of the given data row.
     *
     * @param row
     *            the row
     * @return the row key
     */
    protected static String getRowKey(JsonObject row) {
        return row.getString(DataCommunicatorConstants.KEY);
    }

    /**
     * Returns the data of the given data row.
     *
     * @param row
     *            the row
     * @return the row data
     */
    protected static JsonValue getRowData(JsonObject row) {
        return row.get(DataCommunicatorConstants.DATA);
    }

    /**
     * Returns whether the given row is selected.
     *
     * @param row
     *            the row
     * @return {@code true} if the row is selected, {@code false} otherwise
     */
    protected boolean isRowSelected(JsonObject row) {
        return row.hasKey(DataCommunicatorConstants.SELECTED);
    }

    @Override
    public AbstractListingState getState() {
        return (AbstractListingState) super.getState();
    }
}
