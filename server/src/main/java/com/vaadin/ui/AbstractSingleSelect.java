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
package com.vaadin.ui;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

import com.vaadin.event.selection.SingleSelectionChange;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.AbstractSingleSelectState;
import com.vaadin.util.ReflectTools;

/**
 * An abstract base class for listing components that only support single
 * selection and no lazy loading of data items.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the item date type
 * 
 * @see com.vaadin.shared.data.selection.SelectionModel.Single
 * 
 * @since
 */
public abstract class AbstractSingleSelect<T> extends
        AbstractListing<T, AbstractSingleSelect<T>.SimpleSingleSelection> {

    /**
     * A simple single selection model using the {@code AbstractSingleSelect}
     * RPC and state to communicate with the client. Has no client-side
     * counterpart; the listing connector is expected to handle selection.
     * Client-to-server selection is passed via {@link SelectionServerRpc} and
     * server-to-client via {@link AbstractSingleSelectState#selectedItemKey}.
     */
    public class SimpleSingleSelection implements SelectionModel.Single<T> {

        /**
         * Creates a new {@code SimpleSingleSelection} instance.
         */
        public SimpleSingleSelection() {
            registerRpc(new SelectionServerRpc() {

                @Override
                public void select(String key) {
                    setSelectedKey(key);
                }

                @Override
                public void deselect(String key) {
                    if (Objects.equals(key, getState(false).selectedItemKey)) {
                        setSelectedKey(null);
                    }
                }
            });
        }

        @Override
        public void deselect(T item) {
            Objects.requireNonNull(item, "deselected item cannot be null");
            // TODO creates a key if item not in data source
            String key = getDataCommunicator().getKeyMapper().key(item);
            if (Objects.equals(key, getState(false).selectedItemKey)) {
                setSelectedItem(null);
            }
        }

        @Override
        public void select(T item) {
            Objects.requireNonNull(item, "selected item cannot be null");
            setSelectedItem(item);
        }

        @Override
        public Optional<T> getSelectedItem() {
            return Optional.ofNullable(getState(false).selectedItemKey).map(
                    getDataCommunicator().getKeyMapper()::get);
        }

        private void setSelectedKey(String key) {
            if (isReadOnly()) {
                return;
            }
            if (Objects.equals(key, getState(false).selectedItemKey)) {
                return;
            }

            getState().selectedItemKey = key;
            fireEvent(new SingleSelectionChange<>(AbstractSingleSelect.this,
                    getSelectedItem().orElse(null), true));
        }

        private void setSelectedItem(T item) {
            // TODO creates a key if item not in data source
            String key = Optional.ofNullable(item).map(getDataCommunicator()
                    .getKeyMapper()::key).orElse(null);

            if (Objects.equals(key, getState(false).selectedItemKey)) {
                return;
            }

            getState().selectedItemKey = key;
            fireEvent(new SingleSelectionChange<>(AbstractSingleSelect.this,
                    item, false));
        }
    }

    @Deprecated
    private static final Method SELECTION_CHANGE_METHOD = ReflectTools
            .findMethod(SingleSelectionListener.class, "accept",
                    SingleSelectionChange.class);

    /**
     * Adds a selection listener to this select. The listener is called when the
     * value of this select is changed either by the user or programmatically.
     *
     * @param listener
     *            the value change listener, not null
     * @return a registration for the listener
     */
    public Registration addSelectionListener(
            SingleSelectionListener<T> listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        addListener(SingleSelectionChange.class, listener,
                SELECTION_CHANGE_METHOD);
        return () -> removeListener(SingleSelectionChange.class, listener);
    }

    /**
     * Returns the currently selected item, or an empty optional if no item is
     * selected.
     * 
     * @return an optional of the selected item if any, an empty optional
     *         otherwise
     */
    public Optional<T> getSelectedItem() {
        return getSelectionModel().getSelectedItem();
    }

    @Override
    protected AbstractSingleSelectState getState() {
        return (AbstractSingleSelectState) super.getState();
    }

    @Override
    protected AbstractSingleSelectState getState(boolean markAsDirty) {
        return (AbstractSingleSelectState) super.getState(markAsDirty);
    }
}
