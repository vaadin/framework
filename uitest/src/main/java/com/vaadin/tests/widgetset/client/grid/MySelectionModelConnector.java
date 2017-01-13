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
package com.vaadin.tests.widgetset.client.grid;

import com.vaadin.client.connectors.grid.MultiSelectionModelConnector;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.selection.ClickSelectHandler;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

@Connect(com.vaadin.tests.components.grid.GridCustomSelectionModel.MySelectionModel.class)
public class MySelectionModelConnector extends MultiSelectionModelConnector {

    protected class MyMultiSelectionModel extends MultiSelectionModel {
        @Override
        public Renderer<Boolean> getRenderer() {
            return null;
        }
    }

    private ClickSelectHandler<JsonObject> handler;

    @Override
    protected void initSelectionModel() {
        super.initSelectionModel();
        getGrid().setSelectionModel(new MyMultiSelectionModel());
        handler = new ClickSelectHandler<>(getGrid());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        handler.removeHandler();
        handler = null;
    }

}
