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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widgets.Grid;

/**
 * Multi-row selection model.
 *
 * @author Vaadin Ltd
 * @since 7.4
 */
public class SelectionModelMulti<T> extends AbstractRowHandleSelectionModel<T>
        implements SelectionModel.Multi.Batched<T>, HasUserSelectionAllowed<T> {

    private final LinkedHashSet<RowHandle<T>> selectedRows;
    private Renderer<Boolean> renderer;
    private Grid<T> grid;

    private boolean batchStarted = false;
    private final LinkedHashSet<RowHandle<T>> selectionBatch = new LinkedHashSet<RowHandle<T>>();
    private final LinkedHashSet<RowHandle<T>> deselectionBatch = new LinkedHashSet<RowHandle<T>>();

    /* Event handling for selection with space key */
    private SpaceSelectHandler<T> spaceSelectHandler;
    private boolean userSelectionAllowed = true;

    public SelectionModelMulti() {
        grid = null;
        renderer = null;
        selectedRows = new LinkedHashSet<RowHandle<T>>();
    }

    @Override
    public boolean isSelected(T row) {
        return isSelectedByHandle(grid.getDataSource().getHandle(row));
    }

    @Override
    public Renderer<Boolean> getSelectionColumnRenderer() {
        return renderer;
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
            this.renderer = new MultiSelectionRenderer<T>(grid);
        } else {
            spaceSelectHandler.removeHandler();
            spaceSelectHandler = null;
            this.renderer = null;
        }

    }

    @Override
    public boolean select(T... rows) {
        if (rows == null) {
            throw new IllegalArgumentException("Rows cannot be null");
        }
        return select(Arrays.asList(rows));
    }

    @Override
    public boolean deselect(T... rows) {
        if (rows == null) {
            throw new IllegalArgumentException("Rows cannot be null");
        }
        return deselect(Arrays.asList(rows));
    }

    @Override
    public boolean deselectAll() {
        if (selectedRows.size() > 0) {

            @SuppressWarnings("unchecked")
            final LinkedHashSet<RowHandle<T>> selectedRowsClone = (LinkedHashSet<RowHandle<T>>) selectedRows
                    .clone();
            SelectionEvent<T> event = new SelectionEvent<T>(grid, null,
                    getSelectedRows(), isBeingBatchSelected());
            selectedRows.clear();

            if (isBeingBatchSelected()) {
                selectionBatch.clear();
                deselectionBatch.clear();
                deselectionBatch.addAll(selectedRowsClone);
            }

            grid.fireEvent(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean select(Collection<T> rows) {
        if (rows == null) {
            throw new IllegalArgumentException("Rows cannot be null");
        }

        Set<T> added = new LinkedHashSet<T>();

        for (T row : rows) {
            RowHandle<T> handle = grid.getDataSource().getHandle(row);
            if (selectByHandle(handle)) {
                added.add(row);
            }
        }

        if (added.size() > 0) {
            grid.fireEvent(new SelectionEvent<T>(grid, added, null,
                    isBeingBatchSelected()));

            return true;
        }
        return false;
    }

    @Override
    public boolean deselect(Collection<T> rows) {
        if (rows == null) {
            throw new IllegalArgumentException("Rows cannot be null");
        }

        Set<T> removed = new LinkedHashSet<T>();

        for (T row : rows) {
            RowHandle<T> handle = grid.getDataSource().getHandle(row);
            if (deselectByHandle(handle)) {
                removed.add(row);
            }
        }

        if (removed.size() > 0) {
            grid.fireEvent(new SelectionEvent<T>(grid, null, removed,
                    isBeingBatchSelected()));
            return true;
        }
        return false;
    }

    protected boolean isSelectedByHandle(RowHandle<T> handle) {
        return selectedRows.contains(handle);
    }

    @Override
    protected boolean selectByHandle(RowHandle<T> handle) {
        if (selectedRows.add(handle)) {
            handle.pin();

            if (isBeingBatchSelected()) {
                deselectionBatch.remove(handle);
                selectionBatch.add(handle);
            }

            return true;
        }
        return false;
    }

    @Override
    protected boolean deselectByHandle(RowHandle<T> handle) {
        if (selectedRows.remove(handle)) {

            if (!isBeingBatchSelected()) {
                handle.unpin();
            } else {
                selectionBatch.remove(handle);
                deselectionBatch.add(handle);
            }
            return true;
        }
        return false;
    }

    @Override
    public Collection<T> getSelectedRows() {
        Set<T> selected = new LinkedHashSet<T>();
        for (RowHandle<T> handle : selectedRows) {
            selected.add(handle.getRow());
        }
        return Collections.unmodifiableSet(selected);
    }

    @Override
    public void reset() {
        deselectAll();
    }

    @Override
    public void startBatchSelect() {
        assert !isBeingBatchSelected() : "Batch has already been started";
        batchStarted = true;
    }

    @Override
    public void commitBatchSelect() {
        assert isBeingBatchSelected() : "Batch was never started";
        if (!isBeingBatchSelected()) {
            return;
        }

        batchStarted = false;

        final Collection<T> added = getSelectedRowsBatch();
        selectionBatch.clear();

        final Collection<T> removed = getDeselectedRowsBatch();

        // unpin deselected rows
        for (RowHandle<T> handle : deselectionBatch) {
            handle.unpin();
        }
        deselectionBatch.clear();

        grid.fireEvent(new SelectionEvent<T>(grid, added, removed,
                isBeingBatchSelected()));
    }

    @Override
    public boolean isBeingBatchSelected() {
        return batchStarted;
    }

    @Override
    public Collection<T> getSelectedRowsBatch() {
        return rowHandlesToRows(selectionBatch);
    }

    @Override
    public Collection<T> getDeselectedRowsBatch() {
        return rowHandlesToRows(deselectionBatch);
    }

    private ArrayList<T> rowHandlesToRows(Collection<RowHandle<T>> rowHandles) {
        ArrayList<T> rows = new ArrayList<T>(rowHandles.size());
        for (RowHandle<T> handle : rowHandles) {
            rows.add(handle.getRow());
        }
        return rows;
    }

    @Override
    public boolean isUserSelectionAllowed() {
        return userSelectionAllowed;
    }

    @Override
    public void setUserSelectionAllowed(boolean userSelectionAllowed) {
        this.userSelectionAllowed = userSelectionAllowed;
    }
}
