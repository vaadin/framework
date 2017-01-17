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

import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.widget.grid.events.GridSelectionAllowedEvent;
import com.vaadin.client.widget.grid.selection.ClickSelectHandler;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.SingleSelectionModelState;

import elemental.json.JsonObject;

/**
 * Client side connector for grid single selection model.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.0
 */
@Connect(com.vaadin.ui.components.grid.SingleSelectionModelImpl.class)
public class SingleSelectionModelConnector
        extends AbstractSelectionModelConnector {

    private ClickSelectHandler<JsonObject> clickSelectHandler;

    /**
     * Single selection model for grid.
     */
    protected class SingleSelectionModel implements SelectionModel<JsonObject> {

        private boolean isSelectionAllowed = true;

        private boolean deselectAllowed = true;

        @Override
        public void select(JsonObject item) {
            getRpcProxy(SelectionServerRpc.class)
                    .select(item.getString(DataCommunicatorConstants.KEY));
        }

        @Override
        public void deselect(JsonObject item) {
            getRpcProxy(SelectionServerRpc.class)
                    .deselect(item.getString(DataCommunicatorConstants.KEY));
        }

        @Override
        public boolean isSelected(JsonObject item) {
            return SingleSelectionModelConnector.this.isSelected(item);
        }

        @Override
        public void deselectAll() {
            getRpcProxy(SelectionServerRpc.class).select(null);
        }

        @Override
        public void setSelectionAllowed(boolean selectionAllowed) {
            isSelectionAllowed = selectionAllowed;
            getGrid()
                    .fireEvent(new GridSelectionAllowedEvent(selectionAllowed));
        }

        @Override
        public boolean isSelectionAllowed() {
            return isSelectionAllowed;
        }

        /**
         * Sets whether it's allowed to deselect the selected row through the
         * UI. Deselection is allowed by default.
         *
         * @param deselectAllowed
         *            <code>true</code> if the selected row can be deselected
         *            without selecting another row instead; otherwise
         *            <code>false</code>.
         */
        public void setDeselectAllowed(boolean deselectAllowed) {
            this.deselectAllowed = deselectAllowed;
            updateHandlerDeselectAllowed();
        }

        /**
         * Gets whether it's allowed to deselect the selected row through the
         * UI.
         *
         * @return <code>true</code> if deselection is allowed; otherwise
         *         <code>false</code>
         */
        public boolean isDeselectAllowed() {
            return deselectAllowed;
        }

        private void updateHandlerDeselectAllowed() {
            if (clickSelectHandler != null) {
                clickSelectHandler.setDeselectAllowed(deselectAllowed);
            }
            getSpaceSelectionHandler().setDeselectAllowed(deselectAllowed);
        }

    }

    @Override
    protected void initSelectionModel() {
        getGrid().setSelectionModel(new SingleSelectionModel());

        clickSelectHandler = new ClickSelectHandler<>(getParent().getWidget());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        if (clickSelectHandler != null) {
            clickSelectHandler.removeHandler();
        }
    }

    @Override
    public SingleSelectionModelState getState() {
        return (SingleSelectionModelState) super.getState();
    }

    @OnStateChange("deselectAllowed")
    private void updateDeselectAllowed() {
        getSelectionModel().setDeselectAllowed(getState().deselectAllowed);
    }

    private SingleSelectionModel getSelectionModel() {
        return (SingleSelectionModel) getGrid().getSelectionModel();
    }

}
