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
package com.vaadin.client.connectors.grid;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.shared.ui.Connect;

/**
 * Connector for grids selection model that doesn't allow selecting anything.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 */
@Connect(com.vaadin.ui.components.grid.NoSelectionModel.class)
public class NoSelectionModelConnector extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        assert target instanceof GridConnector : "NoSelectionModelConnector cannot extend anything else than Grid.";

        ((GridConnector) target).getWidget()
                .setSelectionModel(new SelectionModel.NoSelectionModel<>());
    }

}
