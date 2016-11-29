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
import com.vaadin.ui.Grid.AbstractGridExtension;
import com.vaadin.ui.Grid.GridSelectionModel;

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
            jsonObject.put(DataCommunicatorConstants.SELECTED, true);
        }
    }

    @Override
    protected AbstractSelectionModelState getState() {
        return (AbstractSelectionModelState) super.getState();
    }

    @Override
    protected AbstractSelectionModelState getState(boolean markAsDirty) {
        return (AbstractSelectionModelState) super.getState(markAsDirty);
    }

    @Override
    public void remove() {
        deselectAll();

        super.remove();
    }

}
