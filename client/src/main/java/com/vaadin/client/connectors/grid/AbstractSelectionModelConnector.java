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
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widget.grid.selection.SpaceSelectHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.grid.AbstractSelectionModelState;

import elemental.json.JsonObject;

/**
 * Abstract base class for grid's selection models.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 */
public abstract class AbstractSelectionModelConnector
        extends AbstractExtensionConnector {

    private SpaceSelectHandler<JsonObject> spaceSelectHandler;

    @Override
    protected void extend(ServerConnector target) {
        initSelectionModel();

        // Default selection style is space key.
        spaceSelectHandler = new SpaceSelectHandler<>(getGrid());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        if (spaceSelectHandler != null) {
            spaceSelectHandler.removeHandler();
            spaceSelectHandler = null;
        }
    }

    /**
     * Initializes the selection model and sets it to the grid.
     * <p>
     * This method is only invoked once by {@link #extend(ServerConnector)} in
     * {@link AbstractSelectionModelConnector} when the grid is available via
     * {@link #getGrid()} and the selection model should be taken into use.
     */
    protected abstract void initSelectionModel();

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

    /**
     * Shorthand for fetching the grid this selection model is bound to.
     *
     * @return the grid
     */
    protected Grid<JsonObject> getGrid() {
        return getParent().getWidget();
    }

    /**
     * Gets space selection handler registered for the Grid.
     * 
     * @return space selection handler
     */
    protected SpaceSelectHandler<JsonObject> getSpaceSelectionHandler() {
        return spaceSelectHandler;
    }

    @OnStateChange("selectionAllowed")
    private void onSelectionAllowedChange() {
        getGrid().getSelectionModel()
                .setSelectionAllowed(getState().selectionAllowed);
    }

    @Override
    public AbstractSelectionModelState getState() {
        return (AbstractSelectionModelState) super.getState();
    }

    /**
     * Returns whether the given item selected in grid or not.
     *
     * @param item
     *            the item to check
     * @return {@code true} if selected {@code false} if not
     */
    protected boolean isSelected(JsonObject item) {
        return SelectionModel.isItemSelected(item);
    }
}
