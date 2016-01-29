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
package com.vaadin.client.connectors.data.typed;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.data.HasDataSource;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.datasources.ListDataSource;
import com.vaadin.server.communication.data.typed.DataProvider;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

/**
 * A simple connector for DataProvider class.
 * 
 * @since
 */
@Connect(DataProvider.class)
public class DataSourceConnector extends AbstractExtensionConnector {

    DataSource<JsonObject> ds = new ListDataSource<JsonObject>();

    @Override
    protected void extend(ServerConnector target) {
        ServerConnector parent = getParent();
        if (parent instanceof HasDataSource) {
            ((HasDataSource) parent).setDataSource(ds);
        } else {
            assert false : "Parent not implementing HasDataSource";
        }
    }

}
