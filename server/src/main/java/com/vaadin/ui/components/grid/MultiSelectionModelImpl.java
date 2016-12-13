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
import java.util.stream.Stream;

import com.vaadin.event.Listener;
import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.server.data.DataCommunicator;
import com.vaadin.server.data.DataProvider;
import com.vaadin.server.data.Query;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.GridMultiSelectServerRpc;
import com.vaadin.shared.ui.grid.MultiSelectionModelState;
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.MultiSelect;
import com.vaadin.util.ReflectTools;

/**
 * Multiselection model for grid.
 * <p>
 * Shows a column of checkboxes as the first column of grid. Each checkbox
 * triggers the selection for that row.
 * <p>
 * Implementation detail: The Grid selection is updated immediately after user
 * selection on client side, without waiting for the server response.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 *
 * @param <T>
 *            the type of the selected item in grid.
 */
public class MultiSelectionModelImpl<T> extends AbstractSelectionModel<T>
        implements MultiSelectionModel<T> {

    /**
     * State for showing the select all checkbox in the grid's default header
     * row for the selection column.
     * <p>
     * Default value is {@link #DEFAULT}, which means that the select all is
     * only visible if an in-memory data provider is used
     * {@link DataSource#isInMemory()}.
     */
    public enum SelectAllCheckBoxVisible {
        /**
         * Shows the select all checkbox, regardless of data provider used.
         * <p>
         * <b>For a lazy data provider, selecting all will result in to all rows
         * being fetched from backend to application memory!</b>
         */
        VISIBLE,
        /**
         * Never shows the select all checkbox, regardless of data provider
         * used.
         */
        HIDDEN,
        /**
         * By default select all checkbox depends on the grid's dataprovider.
         * <ul>
         * <li>Visible, if the data provider is in-memory</li>
         * <li>Hidden, if the data provider is NOT in-memory (lazy)</li>
         * </ul>
         *
         * @see DataProvider#isInMemory()}.
         */
        DEFAULT;
    }

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
            if (getState(false).allSelected) {
                // updated right away on client side
                getState(false).allSelected = false;
                getUI().getConnectorTracker()
                        .getDiffState(MultiSelectionModelImpl.this)
                        .put("allSelected", false);
            }
            MultiSelectionModelImpl.this.updateSelection(Collections.emptySet(),
                    new LinkedHashSet<>(Arrays.asList(getData(key))), true);
        }

        @Override
        public void selectAll() {
            onSelectAll(true);
        }

        @Override
        public void deselectAll() {
            onDeselectAll(true);
        }
    }

    @Deprecated
    private static final Method SELECTION_CHANGE_METHOD = ReflectTools
            .findMethod(Listener.class, "onEvent",
                    MultiSelectionEvent.class);

    private Set<T> selection = new LinkedHashSet<>();

    private SelectAllCheckBoxVisible selectAllCheckBoxVisible = SelectAllCheckBoxVisible.DEFAULT;

    @Override
    protected void init() {
        registerRpc(new GridMultiSelectServerRpcImpl());
    }

    @Override
    protected MultiSelectionModelState getState() {
        return (MultiSelectionModelState) super.getState();
    }

    @Override
    protected MultiSelectionModelState getState(boolean markAsDirty) {
        return (MultiSelectionModelState) super.getState(markAsDirty);
    }

    /**
     * Sets the select all checkbox visibility mode.
     * <p>
     * The default value is {@link SelectAllCheckBoxVisible#DEFAULT}, which
     * means that the checkbox is only visible if the grid's data provider is
     * in- memory.
     *
     * @param selectAllCheckBoxVisible
     *            the visiblity mode to use
     * @see SelectAllCheckBoxVisible
     */
    public void setSelectAllCheckBoxVisible(
            SelectAllCheckBoxVisible selectAllCheckBoxVisible) {
        if (this.selectAllCheckBoxVisible != selectAllCheckBoxVisible) {
            this.selectAllCheckBoxVisible = selectAllCheckBoxVisible;
            markAsDirty();
        }
    }

    /**
     * Gets the current mode for the select all checkbox visibility.
     *
     * @return the select all checkbox visibility mode
     * @see SelectAllCheckBoxVisible
     * @see #isSelectAllCheckBoxVisible()
     */
    public SelectAllCheckBoxVisible getSelectAllCheckBoxVisible() {
        return selectAllCheckBoxVisible;
    }

    /**
     * Returns whether the select all checkbox will be visible with the current
     * setting of
     * {@link #setSelectAllCheckBoxVisible(SelectAllCheckBoxVisible)}.
     *
     * @return {@code true} if the checkbox will be visible with the current
     *         settings
     * @see SelectAllCheckBoxVisible
     * @see #setSelectAllCheckBoxVisible(SelectAllCheckBoxVisible)
     */
    public boolean isSelectAllCheckBoxVisible() {
        updateCanSelectAll();
        return getState(false).selectAllCheckBoxVisible;
    }

    /**
     * Returns whether all items are selected or not.
     * <p>
     * This is only {@code true} if user has selected all rows with the select
     * all checkbox on client side, or if {@link #selectAll()} has been used
     * from server side.
     *
     * @return {@code true} if all selected, {@code false} if not
     */
    public boolean isAllSelected() {
        return getState(false).allSelected;
    }

    @Override
    public boolean isSelected(T item) {
        return isAllSelected()
                || com.vaadin.ui.Grid.MultiSelectionModel.super.isSelected(
                        item);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        updateCanSelectAll();
    }

    /**
     * Controls whether the select all checkbox is visible in the grid default
     * header, or not.
     * <p>
     * This is updated as a part of {@link #beforeClientResponse(boolean)},
     * since the data provider for grid can be changed on the fly.
     *
     * @see SelectAllCheckBoxVisible
     */
    protected void updateCanSelectAll() {
        switch (selectAllCheckBoxVisible) {
        case VISIBLE:
            getState(false).selectAllCheckBoxVisible = true;
            break;
        case HIDDEN:
            getState(false).selectAllCheckBoxVisible = false;
            break;
        case DEFAULT:
            getState(false).selectAllCheckBoxVisible = getGrid()
                    .getDataProvider().isInMemory();
            break;
        default:
            break;
        }
    }

    @Override
    public Registration addMultiSelectionListener(Listener<MultiSelectionEvent<T>> multiSelectionEventListenerListener) {
        return addListener(MultiSelectionEvent.class, multiSelectionEventListenerListener,
                SELECTION_CHANGE_METHOD);
    }

    @Override
    public Set<T> getSelectedItems() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(selection));
    }

    @Override
    public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
        updateSelection(addedItems, removedItems, false);
    }

    @Override
    public void selectAll() {
        onSelectAll(false);
    }

    @Override
    public void deselectAll() {
        onDeselectAll(false);
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
                    com.vaadin.event.Listener<ValueChangeEvent<Set<T>>> listener) {
                return addSelectionListener(event -> listener.onEvent(event));
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
                getState().selectionAllowed = readOnly;
            }

            @Override
            public boolean isReadOnly() {
                return isUserSelectionAllowed();
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
            public Registration addSelectionListener(Listener<MultiSelectionEvent<T>> listener) {
                return MultiSelectionModelImpl.this
                        .addMultiSelectionListener(listener);
            }
        };
    }

    /**
     * Triggered when the user checks the select all checkbox.
     *
     * @param userOriginated
     *            {@code true} if originated from client side by user
     */
    protected void onSelectAll(boolean userOriginated) {
        if (userOriginated) {
            verifyUserCanSelectAll();
            // all selected state has been updated in client side already
            getState(false).allSelected = true;
            getUI().getConnectorTracker().getDiffState(this).put("allSelected",
                    true);
        } else {
            getState().allSelected = true;
        }

        DataProvider<T, ?> dataSource = getGrid().getDataProvider();
        // this will fetch everything from backend
        Stream<T> stream = dataSource.fetch(new Query<>());
        LinkedHashSet<T> allItems = new LinkedHashSet<>();
        stream.forEach(allItems::add);
        updateSelection(allItems, Collections.emptySet(), userOriginated);
    }

    /**
     * Triggered when the user unchecks the select all checkbox.
     *
     * @param userOriginated
     *            {@code true} if originated from client side by user
     */
    protected void onDeselectAll(boolean userOriginated) {
        if (userOriginated) {
            verifyUserCanSelectAll();
            // all selected state has been update in client side already
            getState(false).allSelected = false;
            getUI().getConnectorTracker().getDiffState(this).put("allSelected",
                    false);
        } else {
            getState().allSelected = false;
        }

        updateSelection(Collections.emptySet(), new LinkedHashSet<>(selection),
                userOriginated);
    }

    private void verifyUserCanSelectAll() {
        if (!getState(false).selectAllCheckBoxVisible) {
            throw new IllegalStateException(
                    "Cannot select all from client since select all checkbox should not be visible");
        }
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

        if (userOriginated && !isUserSelectionAllowed()) {
            throw new IllegalStateException("Client tried to update selection"
                    + " although user selection is disallowed");
        }

        // if there are duplicates, some item is both added & removed, just
        // discard that and leave things as was before
        addedItems.removeIf(item -> removedItems.remove(item));

        if (selection.containsAll(addedItems)
                && Collections.disjoint(selection, removedItems)) {
            return;
        }

        // update allSelected for server side selection updates
        if (getState(false).allSelected && !removedItems.isEmpty()
                && !userOriginated) {
            getState().allSelected = false;
        }

        doUpdateSelection(set -> {
            // order of add / remove does not matter since no duplicates
            set.removeAll(removedItems);
            set.addAll(addedItems);

            // refresh method is NOOP for items that are not present client side
            DataCommunicator<T, ?> dataCommunicator = getGrid()
                    .getDataCommunicator();
            removedItems.forEach(dataCommunicator::refresh);
            addedItems.forEach(dataCommunicator::refresh);
        }, userOriginated);
    }

    private boolean isUserSelectionAllowed() {
        return getState(false).selectionAllowed;
    }

    private void doUpdateSelection(Consumer<Set<T>> handler,
            boolean userOriginated) {
        if (getParent() == null) {
            throw new IllegalStateException(
                    "Trying to update selection for grid selection model that has been detached from the grid.");
        }

        LinkedHashSet<T> oldSelection = new LinkedHashSet<>(selection);
        handler.accept(selection);

        fireEvent(new MultiSelectionEvent<>(getGrid(), asMultiSelect(),
                oldSelection, userOriginated));
    }
}
