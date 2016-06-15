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
package com.vaadin.client.connectors.selection;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.data.HasSelection;
import com.vaadin.client.data.selection.SelectionModel;
import com.vaadin.client.data.selection.SelectionModel.Single;
import com.vaadin.server.communication.data.typed.SelectionModel.NullSelection;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

@Connect(NullSelection.class)
public class NullSelectionConnector extends AbstractSelectionConnector {

    @Override
    protected void extend(ServerConnector target) {
        if (target instanceof HasSelection) {
            super.extend(target);
        }
    }

    @Override
    protected SelectionModel createSelectionModel() {
        return new SelectionModel() {

            @Override
            public void select(JsonObject item) {
            }

            @Override
            public void deselect(JsonObject item) {
            }

            @Override
            public boolean isSelected(JsonObject item) {
                return false;
            }
        };
    }
}
