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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.Renderer;

/**
 * Multi-row selection model.
 *
 * @author Vaadin Ltd
 * @since 7.4
 */
public class SelectionModelMulti<T> implements SelectionModel.Multi<T> {

    private final Renderer<T> renderer;
    private final Set<T> selectedRows;
    private Grid<T> grid;

    public SelectionModelMulti() {
        grid = null;
        renderer = null;
        selectedRows = new LinkedHashSet<T>();
    }

    @Override
    public boolean isSelected(T row) {
        return selectedRows.contains(row);
    }

    @Override
    public Renderer<T> getSelectionColumnRenderer() {
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

            SelectionChangeEvent<T> event = new SelectionChangeEvent<T>(grid,
                    null, selectedRows);
            selectedRows.clear();
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
            if (selectedRows.add(row)) {
                added.add(row);
            }
        }

        if (added.size() > 0) {
            grid.fireEvent(new SelectionChangeEvent<T>(grid, added, null));

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
            if (selectedRows.remove(row)) {
                removed.add(row);
            }
        }

        if (removed.size() > 0) {
            grid.fireEvent(new SelectionChangeEvent<T>(grid, null, removed));

            return true;
        }
        return false;
    }

    @Override
    public Collection<T> getSelectedRows() {
        return Collections.unmodifiableSet(selectedRows);
    }

    @Override
    public void reset() {
        deselectAll();
    }

}
