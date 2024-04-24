/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;

import com.vaadin.data.HasValue;
import com.vaadin.data.SelectionModel.Single;
import com.vaadin.data.provider.DataCommunicator;
import com.vaadin.data.provider.DataGenerator;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.AbstractSingleSelectState;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * An abstract base class for listing components that only support single
 * selection and no lazy loading of data items.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the item date type
 *
 * @see com.vaadin.data.SelectionModel.Single
 *
 * @since 8.0
 */
public abstract class AbstractSingleSelect<T> extends AbstractListing<T>
        implements SingleSelect<T> {

    private T selectedItem = null;

    /**
     * Creates a new {@code AbstractListing} with a default data communicator.
     * <p>
     */
    protected AbstractSingleSelect() {
        init();
    }

    /**
     * Creates a new {@code AbstractSingleSelect} with the given custom data
     * communicator.
     * <p>
     * <strong>Note:</strong> This method is for creating an
     * {@code AbstractSingleSelect} with a custom communicator. In the common
     * case {@link AbstractSingleSelect#AbstractSingleSelect()} should be used.
     * <p>
     *
     * @param dataCommunicator
     *            the data communicator to use, not null
     */
    protected AbstractSingleSelect(DataCommunicator<T> dataCommunicator) {
        super(dataCommunicator);
        init();
    }

    /**
     * Adds a selection listener to this select. The listener is called when the
     * selection is changed either by the user or programmatically.
     *
     * @param listener
     *            the selection listener, not null
     * @return a registration for the listener
     */
    public Registration addSelectionListener(
            SingleSelectionListener<T> listener) {
        return addListener(SingleSelectionEvent.class, listener,
                SingleSelectionListener.SELECTION_CHANGE_METHOD);
    }

    /**
     * Returns the currently selected item, or an empty optional if no item is
     * selected.
     *
     * @return an optional of the selected item if any, an empty optional
     *         otherwise
     */
    public Optional<T> getSelectedItem() {
        return Optional.ofNullable(selectedItem);
    }

    /**
     * Sets the current selection to the given item or clears selection if given
     * {@code null}.
     *
     * @param item
     *            the item to select or {@code null} to clear selection
     */
    public void setSelectedItem(T item) {
        setSelectedItem(item, false);
    }

    /**
     * Returns the current value of this object which is the currently selected
     * item.
     * <p>
     * The call is delegated to {@link #getSelectedItem()}
     *
     * @return the current selection, may be {@code null}
     *
     * @see #getSelectedItem()
     * @see Single#getSelectedItem
     */
    @Override
    public T getValue() {
        return getSelectedItem().orElse(null);
    }

    /**
     * Sets the value of this object which is an item to select. If the new
     * value is not equal to {@code getValue()}, fires a value change event. If
     * value is {@code null} then it deselects currently selected item.
     * <p>
     * The call is delegated to {@link #setSelectedItem(Object)}.
     *
     * @see #setSelectedItem(Object)
     * @see Single#setSelectedItem(Object)
     *
     * @param value
     *            the item to select or {@code null} to clear selection
     */
    @Override
    public void setValue(T value) {
        setSelectedItem(value);
    }

    @Override
    public Registration addValueChangeListener(
            HasValue.ValueChangeListener<T> listener) {
        return addSelectionListener(
                event -> listener.valueChange(new ValueChangeEvent<>(this,
                        event.getOldValue(), event.isUserOriginated())));
    }

    @Override
    protected AbstractSingleSelectState getState() {
        return (AbstractSingleSelectState) super.getState();
    }

    @Override
    protected AbstractSingleSelectState getState(boolean markAsDirty) {
        return (AbstractSingleSelectState) super.getState(markAsDirty);
    }

    @Override
    public void setRequiredIndicatorVisible(boolean visible) {
        super.setRequiredIndicatorVisible(visible);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return super.isRequiredIndicatorVisible();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly();
    }

    /**
     * Returns the item that the given key is assigned to, or {@code null} if
     * there is no such item.
     *
     * @param key
     *            the key whose item to return
     * @return the associated item if any, {@code null} otherwise.
     */
    protected T keyToItem(String key) {
        return getDataCommunicator().getKeyMapper().get(key);
    }

    /**
     * Returns whether the given item is currently selected.
     *
     * @param item
     *            the item to check, not null
     * @return {@code true} if the item is selected, {@code false} otherwise
     */
    public boolean isSelected(T item) {
        if (Objects.equals(selectedItem, item)) {
            return true;
        }

        if (item == null || selectedItem == null) {
            return false;
        }

        return Objects.equals(getDataProvider().getId(selectedItem),
                getDataProvider().getId(item));
    }

    @Override
    protected Element writeItem(Element design, T item, DesignContext context) {
        Element element = super.writeItem(design, item, context);

        if (isSelected(item)) {
            element.attr("selected", true);
        }

        return element;
    }

    @Override
    protected void readItems(Element design, DesignContext context) {
        Set<T> selected = new HashSet<>();
        List<T> items = design.children().stream()
                .map(child -> readItem(child, selected, context))
                .collect(Collectors.toList());
        if (!items.isEmpty()) {
            setItems(items);
        }
        selected.forEach(this::setValue);
    }

    /**
     * Reads an Item from a design and inserts it into the data source.
     * Hierarchical select components should override this method to recursively
     * recursively read any child items as well.
     *
     * @param child
     *            a child element representing the item
     * @param selected
     *            A set accumulating selected items. If the item that is read is
     *            marked as selected, its item id should be added to this set.
     * @param context
     *            the DesignContext instance used in parsing
     * @return the item id of the new item
     *
     * @throws DesignException
     *             if the tag name of the {@code child} element is not
     *             {@code option}.
     */
    protected T readItem(Element child, Set<T> selected,
            DesignContext context) {
        T item = readItem(child, context);

        if (child.hasAttr("selected")) {
            selected.add(item);
        }

        return item;
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> attributes = super.getCustomAttributes();
        // "value" is not an attribute for the component. "selected" attribute
        // is used in "option"'s tag to mark selection which implies value for
        // single select component
        attributes.add("value");
        return attributes;
    }

    private void init() {
        registerRpc(new SelectionServerRpc() {

            @Override
            public void select(String key) {
                setSelectedItem(keyToItem(key), true);
            }

            @Override
            public void deselect(String key) {
                T item = keyToItem(key);
                if (isSelected(item)) {
                    setSelectedItem(null, true);
                }
            }
        });
        addDataGenerator(new DataGenerator<T>() {

            @Override
            public void generateData(T item, JsonObject jsonObject) {
                if (isSelected(item)) {
                    // Deferred update of state.
                    updateSelectedItemState(item);
                }
            }

            @Override
            public void refreshData(T item) {
                if (isSelected(item)) {
                    selectedItem = item;
                    // Invalidate old data
                    updateSelectedItemState(null);
                }
            }
        });
    }

    /**
     * This method updates the internal selection state of the server-side of
     * {@code AbstractSingleSelect}.
     *
     * @param value
     *            the value that should be selected
     * @param userOriginated
     *            {@code true} if selection was done by user, {@code false} if
     *            not
     *
     * @since 8.5
     */
    protected void setSelectedItem(T value, boolean userOriginated) {
        if (isSelected(value)) {
            return;
        }

        // Update selection
        T oldValue = selectedItem;
        selectedItem = value;

        // Re-generate selected item data
        if (oldValue != null) {
            getDataCommunicator().refresh(oldValue);
        }
        if (value != null) {
            getDataCommunicator().refresh(value);
        }

        // Deselection can be handled immediately
        updateSelectedItemState(value);

        // Update diffstate to make sure null can be selected later.
        updateDiffstate("selectedItemKey", Json.createObject());

        fireEvent(new SingleSelectionEvent<>(AbstractSingleSelect.this,
                oldValue, userOriginated));
    }

    /**
     * This method updates the shared selection state of the
     * {@code AbstractSingleSelect}.
     *
     * @param value
     *            the value that is selected; may be {@code null}
     *
     * @since 8.5
     */
    protected void updateSelectedItemState(T value) {
        // FIXME: If selecting a value that does not exist, this will leave and
        // extra object in the key mapper that will not be dropped any time.
        getState().selectedItemKey = value != null
                ? getDataCommunicator().getKeyMapper().key(value)
                : null;
    }
}
