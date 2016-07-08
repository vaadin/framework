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
package com.vaadin.client.tokka.connectors.components;

import com.vaadin.client.data.DataSource;
import com.vaadin.client.tokka.connectors.data.HasSelection;
import com.vaadin.client.tokka.data.selection.SelectionModel;
import com.vaadin.client.ui.AbstractComponentConnector;

import elemental.json.JsonObject;

/**
 * Base connector for AbstractListings base implementations for storing and
 * retrieving the data source and selection model. Used by default
 * implementations for SelectionModels.
 * 
 * @since
 */
public abstract class AbstractListingConnector extends
        AbstractComponentConnector implements HasSelection {

    private SelectionModel model = null;
    private DataSource<JsonObject> dataSource = null;

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        if (this.dataSource != null) {
            removeDataSource(this.dataSource);
        }
        this.dataSource = dataSource;
    }

    @Override
    public DataSource<JsonObject> getDataSource() {
        return dataSource;
    }

    @Override
    public void setSelectionModel(SelectionModel selectionModel) {
        if (model != null) {
            removeSelectionModel(model);
        }
        model = selectionModel;
    }

    @Override
    public SelectionModel getSelectionModel() {
        return model;
    }

    /**
     * Method that is executed when removing an old data source.
     * 
     * @param dataSource
     *            old data source
     */
    protected void removeDataSource(DataSource<JsonObject> dataSource) {
        // NO-OP
    }

    /**
     * Method that is executed when removing an old selection model.
     * 
     * @param selectionModel
     *            old selection model
     */
    protected void removeSelectionModel(SelectionModel selectionModel) {
        // NO-OP
    }
}
