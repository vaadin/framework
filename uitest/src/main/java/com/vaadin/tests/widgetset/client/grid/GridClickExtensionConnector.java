/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.widgetset.client.grid;

import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.GridConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.events.BodyClickHandler;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.components.grid.GridExtensionCommunication.GridClickExtension;

import elemental.json.JsonObject;

@Connect(GridClickExtension.class)
public class GridClickExtensionConnector extends AbstractExtensionConnector {
    public interface GridClickServerRpc extends ServerRpc {

        public void click(String row, String column, MouseEventDetails click);
    }

    @Override
    protected void extend(ServerConnector target) {
        Grid<JsonObject> grid = getParent().getWidget();
        grid.addBodyClickHandler(new BodyClickHandler() {

            @Override
            public void onClick(GridClickEvent event) {
                CellReference<?> cellRef = event.getTargetCell();

                // Gather needed information.
                String rowKey = getParent()
                        .getRowKey((JsonObject) cellRef.getRow());
                String columnId = getParent().getColumnId(cellRef.getColumn());
                MouseEventDetails clickDetails = MouseEventDetailsBuilder
                        .buildMouseEventDetails(event.getNativeEvent());

                getRpcProxy(GridClickServerRpc.class).click(rowKey, columnId,
                        clickDetails);
            }
        });
    }

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

}
