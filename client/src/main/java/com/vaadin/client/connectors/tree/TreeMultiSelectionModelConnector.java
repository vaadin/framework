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
package com.vaadin.client.connectors.tree;

import com.vaadin.client.connectors.grid.MultiSelectionModelConnector;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.selection.ClickSelectHandler;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.tree.TreeMultiSelectionModelState;
import com.vaadin.ui.Tree.TreeMultiSelectionModel;

import elemental.json.JsonObject;

/**
 * Connector for the server side multiselection model of the tree component.
 * <p>
 * Implementation detail: The selection is updated immediately on client side,
 * without waiting for the server response.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(TreeMultiSelectionModel.class)
public class TreeMultiSelectionModelConnector
        extends MultiSelectionModelConnector {

    private ClickSelectHandler<JsonObject> clickSelectHandler;

    @Override
    protected MultiSelectionModel createSelectionModel() {
        return new MultiSelectionModel() {
            @Override
            public Renderer<Boolean> getRenderer() {
                // Prevent selection column.
                return null;
            }
        };
    }

    @Override
    public TreeMultiSelectionModelState getState() {
        return (TreeMultiSelectionModelState) super.getState();
    }

    @Override
    protected void initSelectionModel() {
        super.initSelectionModel();
        clickSelectHandler = new ClickSelectHandler<>(getParent().getWidget());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        if (clickSelectHandler != null) {
            clickSelectHandler.removeHandler();
        }
    }
}
