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
import com.vaadin.server.data.DataCommunicator;
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
 * @since 8.0
 */
public abstract class AbstractSingleSelect<T> extends
        AbstractListing<T, AbstractSingleSelect<T>.AbstractSingleSelection> {

    /**
     * A base class for single selection model implementations. Listens to
     * {@code SelectionServerRpc} invocations to track selection requests by the
     * client. Maintaining the selection state and communicating it from the
     * server to the client is the responsibility of the implementing class.
     */
    public abstract class AbstractSingleSelection
            implements SelectionModel.Single<T> {

        /**
         * Creates a new {@code SimpleSingleSelection} instance.
         */
        public AbstractSingleSelection() {
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
         * Returns the communication key of the selected item or {@code null} if
         * no item is selected.
         *
         * @return the key of the selected item if any, {@code null} otherwise.
         */
        protected abstract String getSelectedKey();

        /**
         * Sets the selected item based on the given communication key. If the
         * key is {@code null}, clears the current selection if any.
         *
         * @param key
         *            the key of the selected item or {@code null} to clear
         *            selection
         */
        protected abstract void doSetSelectedKey(String key);

        /**
         * Sets the selection based on a client request. Does nothing if the
         * select component is {@linkplain Component#isReadOnly()} or if the
         * selection would not change. Otherwise updates the selection and fires
         * a selection change event with {@code isUserOriginated == true}.
         *
         * @param key
         *            the key of the item to select or {@code null} to clear
         *            selection
         */
        protected void setSelectedFromClient(String key) {
            if (isReadOnly()) {
                return;
            }
            if (isKeySelected(key)) {
                return;
            }

            doSetSelectedKey(key);
            fireEvent(new SingleSelectionChange<>(AbstractSingleSelect.this,
                    getSelectedItem().orElse(null), true));
        }

        /**
         * Sets the selection based on server API call. Does nothing if the
         * selection would not change; otherwise updates the selection and fires
         * a selection change event with {@code isUserOriginated == false}.
         *
         * @param item
         *            the item to select or {@code null} to clear selection
         */
        protected void setSelectedFromServer(T item) {
            // TODO creates a key if item not in data source
            String key = itemToKey(item);

            if (isKeySelected(key) || isSelected(item)) {
                return;
            }

            doSetSelectedKey(key);
            fireEvent(new SingleSelectionChange<>(AbstractSingleSelect.this,
                    item, false));
        }

        /**
         * Returns whether the given key maps to the currently selected item.
         *
         * @param key
         *            the key to test or {@code null} to test whether nothing is
         *            selected
         * @return {@code true} if the key equals the key of the currently
         *         selected item (or {@code null} if no selection),
         *         {@code false} otherwise.
         */
        protected boolean isKeySelected(String key) {
            return Objects.equals(key, getSelectedKey());
        }

        /**
         * Returns the communication key assigned to the given item.
         *
         * @param item
         *            the item whose key to return
         * @return the assigned key
         */
        protected String itemToKey(T item) {
            if (item == null) {
                return null;
            } else {
                // TODO creates a key if item not in data source
                return getDataCommunicator().getKeyMapper().key(item);
            }
        }

        /**
         * Returns the item that the given key is assigned to, or {@code null}
         * if there is no such item.
         *
         * @param key
         *            the key whose item to return
         * @return the associated item if any, {@code null} otherwise.
         */
        protected T keyToItem(String key) {
            return getDataCommunicator().getKeyMapper().get(key);
        }
    }

    /**
     * A simple single selection model using the {@code AbstractSingleSelect}
     * RPC and state to communicate with the client. Has no client-side
     * counterpart; the listing connector is expected to handle selection.
     * Client-to-server selection is passed via {@link SelectionServerRpc} and
     * server-to-client via {@link AbstractSingleSelectState#selectedItemKey}.
     */
    protected class SimpleSingleSelection extends AbstractSingleSelection {

        /**
         * Creates a new {@code SimpleSingleSelection}.
         */
        public SimpleSingleSelection() {
        }

        @Override
        public Optional<T> getSelectedItem() {
            return Optional.ofNullable(keyToItem(getSelectedKey()));
        }

        @Override
        protected String getSelectedKey() {
            return getState(false).selectedItemKey;
        }

        @Override
        protected void doSetSelectedKey(String key) {
            getState().selectedItemKey = key;
        }
    }

    @Deprecated
    private static final Method SELECTION_CHANGE_METHOD = ReflectTools
            .findMethod(SingleSelectionListener.class, "accept",
                    SingleSelectionChange.class);

    /**
     * Creates a new {@code AbstractListing} with a default data communicator.
     * <p>
     * <strong>Note:</strong> This constructor does not set a selection model
     * for the new listing. The invoking constructor must explicitly call
     * {@link #setSelectionModel(SelectionModel)} with an
     * {@link AbstractSingleSelect.AbstractSingleSelection} .
     */
    protected AbstractSingleSelect() {
    }

    /**
     * Creates a new {@code AbstractSingleSelect} with the given custom data
     * communicator.
     * <p>
     * <strong>Note:</strong> This method is for creating an
     * {@code AbstractSingleSelect} with a custom communicator. In the common
     * case {@link AbstractSingleSelect#AbstractSingleSelect()} should be used.
     * <p>
     * <strong>Note:</strong> This constructor does not set a selection model
     * for the new listing. The invoking constructor must explicitly call
     * {@link #setSelectionModel(SelectionModel)} with an
     * {@link AbstractSingleSelect.AbstractSingleSelection} .
     *
     * @param dataCommunicator
     *            the data communicator to use, not null
     */
    protected AbstractSingleSelect(DataCommunicator<T> dataCommunicator) {
        super(dataCommunicator);
    }

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

    /**
     * Sets the current selection to the given item or clears selection if given
     * {@code null}.
     *
     * @param item
     *            the item to select or {@code null} to clear selection
     */
    public void setSelectedItem(T item) {
        getSelectionModel().setSelectedItem(item);
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
