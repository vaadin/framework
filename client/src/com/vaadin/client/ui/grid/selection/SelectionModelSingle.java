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

import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.Renderer;

/**
 * Single-row selection model.
 *
 * @author Vaadin Ltd
 * @since 7.4
 */
public class SelectionModelSingle<T> implements SelectionModel.Single<T> {

    private Grid<T> grid;
    private T selectedRow;

    @Override
    public boolean isSelected(T row) {
        return row == null ? null : row.equals(getSelectedRow());
    }

    @Override
    public Renderer<T> getSelectionColumnRenderer() {
        // TODO: Add implementation of SelectionColumnRenderer; currently none
        // exists
        return null;
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
    }

    @Override
    public boolean select(T row) {

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        if (row.equals(getSelectedRow())) {
            return false;
        }

        T removed = selectedRow;
        selectedRow = row;
        grid.fireEvent(new SelectionChangeEvent<T>(grid, row, removed));

        return true;
    }

    @Override
    public boolean deselect(T row) {

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        if (row.equals(selectedRow)) {
            T removed = selectedRow;
            selectedRow = null;
            grid.fireEvent(new SelectionChangeEvent<T>(grid, null, removed));
            return true;
        }

        return false;
    }

    @Override
    public T getSelectedRow() {
        return selectedRow;
    }

    @Override
    public void reset() {
        T removed = selectedRow;
        selectedRow = null;

        if (removed != null) {
            grid.fireEvent(new SelectionChangeEvent<T>(grid, null, removed));
        }
    }

    @Override
    public Collection<T> getSelectedRows() {
        if (getSelectedRow() != null) {
            return Collections.singleton(getSelectedRow());
        }
        return Collections.emptySet();
    }

}
