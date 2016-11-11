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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.GridMultiSelectServerRpc;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.AbstractGridExtension;
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.MultiSelect;
import com.vaadin.util.ReflectTools;

import elemental.json.JsonObject;

/**
 * Multiselection model for grid.
 * <p>
 * Shows a column of checkboxes as the first column of grid. Each checkbox
 * triggers the selection for that row.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 *
 * @param <T>
 *            the type of the selected item in grid.
 */
public class MultiSelectionModelImpl<T> extends AbstractGridExtension<T>
        implements MultiSelectionModel<T> {

    private class GridMultiSelectServerRpcImpl
            implements GridMultiSelectServerRpc {

        @Override
        public void select(String key) {
            MultiSelectionModelImpl.this.updateSelection(
                    new LinkedHashSet<>(Arrays.asList(getData(key))),
                    Collections.emptySet(), true);
        }

        @Override
        public void deselect(String key) {
            MultiSelectionModelImpl.this.updateSelection(Collections.emptySet(),
                    new LinkedHashSet<>(Arrays.asList(getData(key))), true);
        }

        @Override
        public void selectAll() {
            // TODO will be added in another patch
            throw new UnsupportedOperationException("Select all not supported");
        }

        @Override
        public void deselectAll() {
            // TODO will be added in another patch
            throw new UnsupportedOperationException(
                    "Deelect all not supported");
        }
    }

    @Deprecated
    private static final Method SELECTION_CHANGE_METHOD = ReflectTools
            .findMethod(MultiSelectionListener.class, "accept",
                    MultiSelectionEvent.class);

    private final Grid<T> grid;

    private Set<T> selection = new LinkedHashSet<>();

    /**
     * Constructs a new multiselection model for the given grid.
     *
     * @param grid
     *            the grid to bind the selection model into
     */
    public MultiSelectionModelImpl(Grid<T> grid) {
        this.grid = grid;
        extend(grid);

        registerRpc(new GridMultiSelectServerRpcImpl());
    }

    @Override
    public void remove() {
        updateSelection(Collections.emptySet(), getSelectedItems(), false);

        super.remove();
    }

    /**
     * Adds a selection listener that will be called when the selection is
     * changed either by the user or programmatically.
     *
     * @param listener
     *            the value change listener, not {@code null}
     * @return a registration for the listener
     */
    public Registration addSelectionListener(
            MultiSelectionListener<T> listener) {
        addListener(MultiSelectionEvent.class, listener,
                SELECTION_CHANGE_METHOD);
        return () -> removeListener(MultiSelectionEvent.class, listener);
    }

    @Override
    public void generateData(T item, JsonObject jsonObject) {
        // in case of all items selected, don't write individual items as
        // seleted
        if (isSelected(item)) {
            jsonObject.put(DataCommunicatorConstants.SELECTED, true);
        }
    }

    @Override
    public Set<T> getSelectedItems() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(selection));
    }

    @Override
    public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
        updateSelection(addedItems, removedItems, false);
    }

    /**
     * Gets a wrapper for using this grid as a multiselect in a binder.
     *
     * @return a multiselect wrapper for grid
     */
    @Override
    public MultiSelect<T> asMultiSelect() {
        return new MultiSelect<T>() {

            @Override
            public void setValue(Set<T> value) {
                Objects.requireNonNull(value);
                Set<T> copy = value.stream().map(Objects::requireNonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                updateSelection(copy, new LinkedHashSet<>(getSelectedItems()));
            }

            @Override
            public Set<T> getValue() {
                return getSelectedItems();
            }

            @Override
            public Registration addValueChangeListener(
                    com.vaadin.data.HasValue.ValueChangeListener<Set<T>> listener) {
                return addSelectionListener(event -> listener.accept(event));
            }

            @Override
            public void setRequiredIndicatorVisible(
                    boolean requiredIndicatorVisible) {
                // TODO support required indicator for grid ?
                throw new UnsupportedOperationException(
                        "Required indicator is not supported in grid.");
            }

            @Override
            public boolean isRequiredIndicatorVisible() {
                // TODO support required indicator for grid ?
                throw new UnsupportedOperationException(
                        "Required indicator is not supported in grid.");
            }

            @Override
            public void setReadOnly(boolean readOnly) {
                // TODO support read only in grid ?
                throw new UnsupportedOperationException(
                        "Read only mode is not supported for grid.");
            }

            @Override
            public boolean isReadOnly() {
                // TODO support read only in grid ?
                throw new UnsupportedOperationException(
                        "Read only mode is not supported for grid.");
            }

            @Override
            public void updateSelection(Set<T> addedItems,
                    Set<T> removedItems) {
                MultiSelectionModelImpl.this.updateSelection(addedItems,
                        removedItems);
            }

            @Override
            public Set<T> getSelectedItems() {
                return MultiSelectionModelImpl.this.getSelectedItems();
            }

            @Override
            public Registration addSelectionListener(
                    MultiSelectionListener<T> listener) {
                return MultiSelectionModelImpl.this
                        .addSelectionListener(listener);
            }
        };
    }

    /**
     * Updates the selection by adding and removing the given items.
     * <p>
     * All selection updates should go through this method, since it handles
     * incorrect parameters, removing duplicates, notifying data communicator
     * and and firing events.
     *
     * @param addedItems
     *            the items added to selection, not {@code} null
     * @param removedItems
     *            the items removed from selection, not {@code} null
     * @param userOriginated
     *            {@code true} if this was used originated, {@code false} if not
     */
    protected void updateSelection(Set<T> addedItems, Set<T> removedItems,
            boolean userOriginated) {
        Objects.requireNonNull(addedItems);
        Objects.requireNonNull(removedItems);

        // if there are duplicates, some item is both added & removed, just
        // discard that and leave things as was before
        addedItems.removeIf(item -> removedItems.remove(item));

        if (selection.containsAll(addedItems)
                && Collections.disjoint(selection, removedItems)) {
            return;
        }

        doUpdateSelection(set -> {
            // order of add / remove does not matter since no duplicates
            set.removeAll(removedItems);
            set.addAll(addedItems);
            removedItems.forEach(grid.getDataCommunicator()::refresh);
            addedItems.forEach(grid.getDataCommunicator()::refresh);
        }, userOriginated);
    }

    private void doUpdateSelection(Consumer<Set<T>> handler,
            boolean userOriginated) {
        if (getParent() == null) {
            throw new IllegalStateException(
                    "Trying to update selection for grid selection model that has been detached from the grid.");
        }

        LinkedHashSet<T> oldSelection = new LinkedHashSet<>(selection);
        handler.accept(selection);

        fireEvent(new MultiSelectionEvent<>(grid, asMultiSelect(), oldSelection,
                userOriginated));
    }
}
