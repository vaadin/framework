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
package com.vaadin.client.ui.grid.selection;

import java.util.Collection;
import java.util.Collections;

import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.Renderer;

/**
 * Single-row selection model.
 * 
 * @author Vaadin Ltd
 * @since
 */
public class SelectionModelSingle<T> extends AbstractRowHandleSelectionModel<T>
        implements SelectionModel.Single<T> {

    private Grid<T> grid;
    private RowHandle<T> selectedRow;
    private Renderer<Boolean> renderer;

    @Override
    public boolean isSelected(T row) {
        return selectedRow != null
                && selectedRow.equals(grid.getDataSource().getHandle(row));
    }

    @Override
    public Renderer<Boolean> getSelectionColumnRenderer() {
        return renderer;
    }

    @Override
    public void setGrid(Grid<T> grid) {
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null");
        }

        if (this.grid == null) {
            this.grid = grid;
        } else {
            throw new IllegalStateException(
                    "Grid reference cannot be reassigned");
        }
        renderer = new MultiSelectionRenderer<T>(grid);
    }

    @Override
    public boolean select(T row) {

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        if (isSelected(row)) {
            return false;
        }

        T removed = getSelectedRow();
        if (selectedRow != null) {
            selectedRow.unpin();
        }
        selectedRow = grid.getDataSource().getHandle(row);
        selectedRow.pin();

        grid.fireEvent(new SelectionChangeEvent<T>(grid, row, removed));

        return true;
    }

    @Override
    public boolean deselect(T row) {

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        if (isSelected(row)) {
            T removed = selectedRow.getRow();
            selectedRow.unpin();
            selectedRow = null;
            grid.fireEvent(new SelectionChangeEvent<T>(grid, null, removed));
            return true;
        }

        return false;
    }

    @Override
    public T getSelectedRow() {
        return (selectedRow != null ? selectedRow.getRow() : null);
    }

    @Override
    public void reset() {
        T removed = getSelectedRow();

        if (removed != null) {
            deselect(removed);
        }
    }

    @Override
    public Collection<T> getSelectedRows() {
        if (getSelectedRow() != null) {
            return Collections.singleton(getSelectedRow());
        }
        return Collections.emptySet();
    }

    @Override
    protected boolean selectByHandle(RowHandle<T> handle) {
        if (!handle.equals(selectedRow)) {
            selectedRow = handle;
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean deselectByHandle(RowHandle<T> handle) {
        if (handle.equals(selectedRow)) {
            selectedRow = null;
            return true;
        } else {
            return false;
        }
    }
}
