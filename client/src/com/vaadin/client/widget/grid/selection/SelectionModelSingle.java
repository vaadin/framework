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
package com.vaadin.client.widget.grid.selection;

import java.util.Collection;
import java.util.Collections;

import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widgets.Grid;

/**
 * Single-row selection model.
 * 
 * @author Vaadin Ltd
 * @since 7.4
 */
public class SelectionModelSingle<T> extends AbstractRowHandleSelectionModel<T>
        implements SelectionModel.Single<T> {

    private Grid<T> grid;
    private RowHandle<T> selectedRow;

    /** Event handling for selection with space key */
    private SpaceSelectHandler<T> spaceSelectHandler;

    /** Event handling for selection by clicking cells */
    private ClickSelectHandler<T> clickSelectHandler;

    private boolean deselectAllowed = true;

    @Override
    public boolean isSelected(T row) {
        return selectedRow != null
                && selectedRow.equals(grid.getDataSource().getHandle(row));
    }

    @Override
    public Renderer<Boolean> getSelectionColumnRenderer() {
        // No Selection column renderer for single selection
        return null;
    }

    @Override
    public void setGrid(Grid<T> grid) {
        if (this.grid != null && grid != null) {
            // Trying to replace grid
            throw new IllegalStateException(
                    "Selection model is already attached to a grid. "
                            + "Remove the selection model first from "
                            + "the grid and then add it.");
        }

        this.grid = grid;
        if (this.grid != null) {
            spaceSelectHandler = new SpaceSelectHandler<T>(grid);
            clickSelectHandler = new ClickSelectHandler<T>(grid);
            updateHandlerDeselectAllowed();
        } else {
            spaceSelectHandler.removeHandler();
            clickSelectHandler.removeHandler();
            spaceSelectHandler = null;
            clickSelectHandler = null;
        }
    }

    @Override
    public boolean select(T row) {

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        T removed = getSelectedRow();
        if (selectByHandle(grid.getDataSource().getHandle(row))) {
            grid.fireEvent(new SelectionEvent<T>(grid, row, removed, false));

            return true;
        }
        return false;
    }

    @Override
    public boolean deselect(T row) {

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        if (isSelected(row)) {
            deselectByHandle(selectedRow);
            grid.fireEvent(new SelectionEvent<T>(grid, null, row, false));
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
        if (selectedRow != null) {
            deselect(getSelectedRow());
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
        if (handle != null && !handle.equals(selectedRow)) {
            deselectByHandle(selectedRow);
            selectedRow = handle;
            selectedRow.pin();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean deselectByHandle(RowHandle<T> handle) {
        if (handle != null && handle.equals(selectedRow)) {
            selectedRow.unpin();
            selectedRow = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setDeselectAllowed(boolean deselectAllowed) {
        this.deselectAllowed = deselectAllowed;
        updateHandlerDeselectAllowed();
    }

    @Override
    public boolean isDeselectAllowed() {
        return deselectAllowed;
    }

    private void updateHandlerDeselectAllowed() {
        if (spaceSelectHandler != null) {
            spaceSelectHandler.setDeselectAllowed(deselectAllowed);
        }
        if (clickSelectHandler != null) {
            clickSelectHandler.setDeselectAllowed(deselectAllowed);
        }
    }

}
