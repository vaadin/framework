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
package com.vaadin.v7.client.connectors;

import java.util.Collection;

import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.v7.client.widget.grid.selection.SelectionModel;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.shared.ui.grid.GridState;

import elemental.json.JsonObject;

/**
 * Base class for all selection model connectors.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public abstract class AbstractSelectionModelConnector<T extends SelectionModel<JsonObject>>
        extends AbstractExtensionConnector {

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

    protected Grid<JsonObject> getGrid() {
        return getParent().getWidget();
    }

    protected RowHandle<JsonObject> getRowHandle(JsonObject row) {
        return getGrid().getDataSource().getHandle(row);
    }

    protected String getRowKey(JsonObject row) {
        return row != null ? getParent().getRowKey(row) : null;
    }

    protected abstract T createSelectionModel();

    public abstract static class AbstractSelectionModel
            implements SelectionModel<JsonObject> {

        @Override
        public boolean isSelected(JsonObject row) {
            return row.hasKey(GridState.JSONKEY_SELECTED);
        }

        @Override
        public void setGrid(Grid<JsonObject> grid) {
            // NO-OP
        }

        @Override
        public void reset() {
            // Should not need any actions.
        }

        @Override
        public Collection<JsonObject> getSelectedRows() {
            throw new UnsupportedOperationException(
                    "This client-side selection model "
                            + getClass().getSimpleName()
                            + " does not know selected rows.");
        }
    }
}
