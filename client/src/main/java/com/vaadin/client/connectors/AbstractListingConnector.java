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
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.ui.AbstractListing;

import elemental.json.JsonObject;

/**
 * Base connector class for {@link AbstractListing}.
 *
 * @since
 */
public abstract class AbstractListingConnector
        extends AbstractComponentConnector implements HasDataSource {

    private DataSource<JsonObject> dataSource = null;

    private SelectionModel<String> selectionModel = null;

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource<JsonObject> getDataSource() {
        return dataSource;
    }

    /**
     * Sets the selection model to use. Passing {@code null} disables selection.
     * 
     * @param selectionModel
     *            the selection model or null to disable
     */
    public void setSelectionModel(SelectionModel<String> selectionModel) {
        this.selectionModel = selectionModel;
    }

    public SelectionModel<String> getSelectionModel() {
        return selectionModel;
    }
}
