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
package com.vaadin.tests.widgetset.client.v7.grid;

import com.vaadin.client.ServerConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.v7.client.connectors.MultiSelectionModelConnector;
import com.vaadin.v7.client.renderers.ComplexRenderer;
import com.vaadin.v7.client.widget.grid.selection.ClickSelectHandler;
import com.vaadin.v7.client.widget.grid.selection.SelectionModel.Multi;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.tests.components.grid.GridCustomSelectionModel.MySelectionModel;

import elemental.json.JsonObject;

@Connect(MySelectionModel.class)
public class MySelectionModelConnector extends MultiSelectionModelConnector {

    private ClickSelectHandler<JsonObject> handler;

    @Override
    protected void extend(ServerConnector target) {
        super.extend(target);
        handler = new ClickSelectHandler<JsonObject>(getGrid());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        handler.removeHandler();
        handler = null;
    }

    @Override
    protected Multi<JsonObject> createSelectionModel() {
        return new MySelectionModel();
    }

    public class MySelectionModel extends MultiSelectionModel {

        @Override
        protected ComplexRenderer<Boolean> createSelectionColumnRenderer(
                Grid<JsonObject> grid) {
            // No Selection Column.
            return null;
        }
    }
}
