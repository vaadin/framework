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
package com.vaadin.ui.components.grid;

import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.grid.AbstractSelectionModelState;
import com.vaadin.ui.AbstractListing;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.AbstractGridExtension;

import elemental.json.JsonObject;

/**
 * Abstract selection model for grid.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <T>
 *            the type of the items in grid.
 */
public abstract class AbstractSelectionModel<T> extends AbstractGridExtension<T>
        implements GridSelectionModel<T> {

    @Override
    public void generateData(T item, JsonObject jsonObject) {
        if (isSelected(item)) {
            // Pre-emptive update in case used a stale element in selection.
            refreshData(item);

            jsonObject.put(DataCommunicatorConstants.SELECTED, true);
        }
    }

    @Override
    public void destroyAllData() {
        deselectAll();
    }

    @Override
    protected AbstractSelectionModelState getState() {
        return (AbstractSelectionModelState) super.getState();
    }

    @Override
    protected AbstractSelectionModelState getState(boolean markAsDirty) {
        return (AbstractSelectionModelState) super.getState(markAsDirty);
    }

    /**
     * Returns the grid this selection model is attached to using, or throws
     * {@link IllegalStateException} if not attached to any selection model.
     *
     * @return the grid this selection model is attached to
     * @throws IllegalStateException
     *             if this selection mode is not attached to any grid
     */
    protected Grid<T> getGrid() throws IllegalStateException {
        Grid<T> parent = getParent();
        if (parent == null) {
            throw new IllegalStateException(
                    "This selection model is no currently attached to any grid.");
        }
        return parent;
    }

    @Override
    public void extend(AbstractListing<T> grid) {
        if (getParent() != null) {
            throw new IllegalStateException(
                    "This selection model has been bound to a grid already. Please remove the existing binding with model.remove() first.");
        }
        // type is verified in parent
        super.extend(grid);

        init();
    }

    /**
     * Initializes the selection model after it has been attached to a grid.
     */
    protected abstract void init();

    @Override
    public void remove() {
        deselectAll();

        super.remove();
    }

    @Override
    public boolean isUserSelectionAllowed() {
        return getState(false).selectionAllowed;
    }

    @Override
    public void setUserSelectionAllowed(boolean allowed) {
        getState().selectionAllowed = allowed;
    }

}
