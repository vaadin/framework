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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.GridMultiSelectServerRpc;
import com.vaadin.shared.ui.grid.MultiSelectionModelState;
import com.vaadin.ui.MultiSelect;

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
     * {@link DataProvider#isInMemory()}.
     */
    public enum SelectAllCheckBoxVisibility {
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

    private List<T> selection = new ArrayList<>();

    private SelectAllCheckBoxVisibility selectAllCheckBoxVisibility = SelectAllCheckBoxVisibility.DEFAULT;

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
     * The default value is {@link SelectAllCheckBoxVisibility#DEFAULT}, which
     * means that the checkbox is only visible if the grid's data provider is
     * in- memory.
     *
     * @param selectAllCheckBoxVisibility
     *            the visiblity mode to use
     * @see SelectAllCheckBoxVisibility
     */
    public void setSelectAllCheckBoxVisibility(
            SelectAllCheckBoxVisibility selectAllCheckBoxVisibility) {
        if (this.selectAllCheckBoxVisibility != selectAllCheckBoxVisibility) {
            this.selectAllCheckBoxVisibility = selectAllCheckBoxVisibility;
            markAsDirty();
        }
    }

    /**
     * Gets the current mode for the select all checkbox visibility.
     *
     * @return the select all checkbox visibility mode
     * @see SelectAllCheckBoxVisibility
     * @see #isSelectAllCheckBoxVisible()
     */
    public SelectAllCheckBoxVisibility getSelectAllCheckBoxVisibility() {
        return selectAllCheckBoxVisibility;
    }

    /**
     * Returns whether the select all checkbox will be visible with the current
     * setting of
     * {@link #setSelectAllCheckBoxVisibility(SelectAllCheckBoxVisibility)}.
     *
     * @return {@code true} if the checkbox will be visible with the current
     *         settings
     * @see SelectAllCheckBoxVisibility
     * @see #setSelectAllCheckBoxVisibility(SelectAllCheckBoxVisibility)
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
                || selectionContainsId(getGrid().getDataProvider().getId(item));
    }

    /**
     * Returns if the given id belongs to one of the selected items.
     *
     * @param id
     *            the id to check for
     * @return {@code true} if id is selected, {@code false} if not
     */
    protected boolean selectionContainsId(Object id) {
        DataProvider<T, ?> dataProvider = getGrid().getDataProvider();
        return selection.stream().map(dataProvider::getId)
                .anyMatch(i -> id.equals(i));
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
     * @see SelectAllCheckBoxVisibility
     */
    protected void updateCanSelectAll() {
        switch (selectAllCheckBoxVisibility) {
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
    public Registration addMultiSelectionListener(
            MultiSelectionListener<T> listener) {
        return addListener(MultiSelectionEvent.class, listener,
                MultiSelectionListener.SELECTION_CHANGE_METHOD);
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
                    com.vaadin.data.HasValue.ValueChangeListener<Set<T>> listener) {
                return addSelectionListener(
                        event -> listener.valueChange(event));
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
                setUserSelectionAllowed(!readOnly);
            }

            @Override
            public boolean isReadOnly() {
                return !isUserSelectionAllowed();
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
            DataCommunicator<T> dataCommunicator = getGrid()
                    .getDataCommunicator();
            removedItems.forEach(dataCommunicator::refresh);
            addedItems.forEach(dataCommunicator::refresh);
        }, userOriginated);
    }

    private void doUpdateSelection(Consumer<Collection<T>> handler,
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

    @Override
    public void refreshData(T item) {
        DataProvider<T, ?> dataProvider = getGrid().getDataProvider();
        Object refreshId = dataProvider.getId(item);
        for (int i = 0; i < selection.size(); ++i) {
            if (dataProvider.getId(selection.get(i)).equals(refreshId)) {
                selection.set(i, item);
                return;
            }
        }
    }
}
