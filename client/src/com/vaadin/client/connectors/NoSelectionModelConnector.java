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
package com.vaadin.client.connectors;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widget.grid.selection.SelectionModelNone;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.Grid.NoSelectionModel;

import elemental.json.JsonObject;

/**
 * Connector for server-side {@link NoSelectionModel}.
 * 
 * @since 7.6
 * @author Vaadin Ltd
 */
@Connect(NoSelectionModel.class)
public class NoSelectionModelConnector extends
        AbstractSelectionModelConnector<SelectionModel<JsonObject>> {

    @Override
    protected void extend(ServerConnector target) {
        getGrid().setSelectionModel(createSelectionModel());
    }

    @Override
    protected SelectionModel<JsonObject> createSelectionModel() {
        return new SelectionModelNone<JsonObject>();
    }
}