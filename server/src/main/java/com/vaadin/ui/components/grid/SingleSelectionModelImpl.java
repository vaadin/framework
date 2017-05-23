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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.grid.SingleSelectionModelState;
import com.vaadin.ui.SingleSelect;

/**
 * Single selection model for grid.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 *
 * @param <T>
 *            the type of the selected item in grid.
 */
public class SingleSelectionModelImpl<T> extends AbstractSelectionModel<T>
        implements SingleSelectionModel<T> {

    private T selectedItem = null;

    @Override
    protected void init() {
        registerRpc(new SelectionServerRpc() {

            @Override
            public void select(String key) {
                setSelectedFromClient(key);
            }

            @Override
            public void deselect(String key) {
                if (isKeySelected(key)) {
                    setSelectedFromClient(null);
                }
            }
        });
    }

    @Override
    protected SingleSelectionModelState getState() {
        return (SingleSelectionModelState) super.getState();
    }

    @Override
    protected SingleSelectionModelState getState(boolean markAsDirty) {
        return (SingleSelectionModelState) super.getState(markAsDirty);
    }

    @Override
    public Registration addSingleSelectionListener(
            SingleSelectionListener<T> listener) {
        return addListener(SingleSelectionEvent.class, listener,
                SingleSelectionListener.SELECTION_CHANGE_METHOD);
    }

    @Override
    public Optional<T> getSelectedItem() {
        return Optional.ofNullable(selectedItem);
    }

    @Override
    public void deselect(T item) {
        Objects.requireNonNull(item, "deselected item cannot be null");
        if (isSelected(item)) {
            setSelectedFromServer(null);
        }
    }

    @Override
    public void select(T item) {
        Objects.requireNonNull(item, "selected item cannot be null");
        setSelectedFromServer(item);
    }

    /**
     * Returns whether the given key maps to the currently selected item.
     *
     * @param key
     *            the key to test or {@code null} to test whether nothing is
     *            selected
     * @return {@code true} if the key equals the key of the currently selected
     *         item (or {@code null} if no selection), {@code false} otherwise.
     */
    protected boolean isKeySelected(String key) {
        return isSelected(getData(key));
    }

    /**
     * Sets the selected item. If the item is {@code null}, clears the current
     * selection if any.
     *
     * @param item
     *            the selected item or {@code null} to clear selection
     */
    protected void doSetSelected(T item) {
        if (getParent() == null) {
            throw new IllegalStateException(
                    "Trying to update selection for grid selection model that has been detached from the grid.");
        }

        if (selectedItem != null) {
            getGrid().getDataCommunicator().refresh(selectedItem);
        }
        selectedItem = item;
        if (selectedItem != null) {
            getGrid().getDataCommunicator().refresh(selectedItem);
        }
    }

    /**
     * Sets the selection based on a client request. Does nothing if the select
     * component is {@linkplain SingleSelect#isReadOnly()} or if the selection
     * would not change. Otherwise updates the selection and fires a selection
     * change event with {@code isUserOriginated == true}.
     *
     * @param key
     *            the key of the item to select or {@code null} to clear
     *            selection
     */
    protected void setSelectedFromClient(String key) {
        if (!isUserSelectionAllowed()) {
            throw new IllegalStateException("Client tried to update selection"
                    + " although user selection is disallowed");
        }
        T item = getData(key);
        if (isSelected(item)) {
            return;
        }

        T oldSelection = selectedItem;
        doSetSelected(item);
        fireEvent(new SingleSelectionEvent<>(getGrid(), asSingleSelect(),
                oldSelection, true));
    }

    /**
     * Sets the selection based on server API call. Does nothing if the
     * selection would not change; otherwise updates the selection and fires a
     * selection change event with {@code isUserOriginated == false}.
     *
     * @param item
     *            the item to select or {@code null} to clear selection
     */
    protected void setSelectedFromServer(T item) {
        if (isSelected(item)) {
            // Avoid generating an extra key when item matches a stale one.
            return;
        }

        T oldSelection = this.getSelectedItem()
                .orElse(asSingleSelect().getEmptyValue());
        doSetSelected(item);
        fireEvent(new SingleSelectionEvent<>(getGrid(), asSingleSelect(),
                oldSelection, false));
    }

    @Override
    public Set<T> getSelectedItems() {
        if (selectedItem != null) {
            return new HashSet<>(Arrays.asList(selectedItem));
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public void setDeselectAllowed(boolean deselectAllowed) {
        getState().deselectAllowed = deselectAllowed;
    }

    @Override
    public boolean isDeselectAllowed() {
        return getState().deselectAllowed;
    }

    /**
     * Gets a wrapper for using this grid as a single select in a binder.
     *
     * @return a single select wrapper for grid
     */
    @Override
    public SingleSelect<T> asSingleSelect() {
        return new SingleSelect<T>() {

            @Override
            public void setValue(T value) {
                SingleSelectionModelImpl.this.setSelectedFromServer(value);
            }

            @Override
            public T getValue() {
                return SingleSelectionModelImpl.this.getSelectedItem()
                        .orElse(null);
            }

            @Override
            public Registration addValueChangeListener(
                    com.vaadin.data.HasValue.ValueChangeListener<T> listener) {
                return SingleSelectionModelImpl.this.addSingleSelectionListener(
                        (SingleSelectionListener<T>) event -> listener
                                .valueChange(event));
            }

            @Override
            public void setRequiredIndicatorVisible(
                    boolean requiredIndicatorVisible) {
                // TODO support required indicator when grid is used in binder ?
                throw new UnsupportedOperationException(
                        "Required indicator is not supported for Grid.");
            }

            @Override
            public boolean isRequiredIndicatorVisible() {
                throw new UnsupportedOperationException(
                        "Required indicator is not supported for Grid.");
            }

            @Override
            public void setReadOnly(boolean readOnly) {
                setUserSelectionAllowed(!readOnly);
            }

            @Override
            public boolean isReadOnly() {
                return !isUserSelectionAllowed();
            }
        };
    }

    @Override
    public void refreshData(T item) {
        if (isSelected(item)) {
            selectedItem = item;
        }
    }

    @Override
    public boolean isSelected(T item) {
        // Quick comparison of objects directly
        if (Objects.equals(item, selectedItem)) {
            return true;
        }

        // Id based check
        return item != null && selectedItem != null
                && getGrid().getDataProvider().getId(selectedItem)
                        .equals(getGrid().getDataProvider().getId(item));
    }
}
