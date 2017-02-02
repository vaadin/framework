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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;

import com.vaadin.data.HasValue;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.SelectionModel.Multi;
import com.vaadin.data.provider.DataGenerator;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.SerializableConsumer;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.MultiSelectServerRpc;
import com.vaadin.shared.ui.ListingJsonConstants;
import com.vaadin.shared.ui.abstractmultiselect.AbstractMultiSelectState;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

import elemental.json.JsonObject;

/**
 * Base class for listing components that allow selecting multiple items.
 * <p>
 * Sends selection information individually for each item.
 *
 * @param <T>
 *            item type
 * @author Vaadin Ltd
 * @since 8.0
 */
public abstract class AbstractMultiSelect<T> extends AbstractListing<T>
        implements MultiSelect<T> {

    private List<T> selection = new ArrayList<>();

    private class MultiSelectServerRpcImpl implements MultiSelectServerRpc {
        @Override
        public void updateSelection(Set<String> selectedItemKeys,
                Set<String> deselectedItemKeys) {
            AbstractMultiSelect.this.updateSelection(
                    getItemsForSelectionChange(selectedItemKeys),
                    getItemsForSelectionChange(deselectedItemKeys), true);
        }

        private Set<T> getItemsForSelectionChange(Set<String> keys) {
            return keys.stream().map(key -> getItemForSelectionChange(key))
                    .filter(Optional::isPresent).map(Optional::get)
                    .collect(Collectors.toSet());
        }

        private Optional<T> getItemForSelectionChange(String key) {
            T item = getDataCommunicator().getKeyMapper().get(key);
            if (item == null || !getItemEnabledProvider().test(item)) {
                return Optional.empty();
            }

            return Optional.of(item);
        }

    }

    private class MultiSelectDataGenerator implements DataGenerator<T> {
        @Override
        public void generateData(T data, JsonObject jsonObject) {
            String caption = getItemCaptionGenerator().apply(data);
            if (caption != null) {
                jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_VALUE,
                        caption);
            } else {
                jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_VALUE, "");
            }
            Resource icon = getItemIconGenerator().apply(data);
            if (icon != null) {
                String iconUrl = ResourceReference
                        .create(icon, AbstractMultiSelect.this, null).getURL();
                jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_ICON, iconUrl);
            }
            if (!getItemEnabledProvider().test(data)) {
                jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_DISABLED,
                        true);
            }

            if (isSelected(data)) {
                jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_SELECTED,
                        true);
            }
        }

        @Override
        public void destroyData(T data) {
        }

        @Override
        public void destroyAllData() {
            AbstractMultiSelect.this.deselectAll();
        }

        @Override
        public void refreshData(T item) {
            refreshSelectedItem(item);
        }
    }

    /**
     * The item enabled status provider. It is up to the implementing class to
     * support this or not.
     */
    private SerializablePredicate<T> itemEnabledProvider = item -> true;

    /**
     * Creates a new multi select with an empty data provider.
     */
    protected AbstractMultiSelect() {
        registerRpc(new MultiSelectServerRpcImpl());

        // #FIXME it should be the responsibility of the SelectionModel
        // (AbstractSelectionModel) to add selection data for item
        addDataGenerator(new MultiSelectDataGenerator());
    }

    /**
     * Adds a selection listener that will be called when the selection is
     * changed either by the user or programmatically.
     *
     * @param listener
     *            the value change listener, not {@code null}
     * @return a registration for the listener
     */
    @Override
    public Registration addSelectionListener(
            MultiSelectionListener<T> listener) {
        return addListener(MultiSelectionEvent.class, listener,
                MultiSelectionListener.SELECTION_CHANGE_METHOD);
    }

    @Override
    public ItemCaptionGenerator<T> getItemCaptionGenerator() {
        return super.getItemCaptionGenerator();
    }

    @Override
    public void setItemCaptionGenerator(
            ItemCaptionGenerator<T> itemCaptionGenerator) {
        super.setItemCaptionGenerator(itemCaptionGenerator);
    }

    /**
     * Returns the current value of this object which is an immutable set of the
     * currently selected items.
     * <p>
     * The call is delegated to {@link #getSelectedItems()}
     *
     * @return the current selection
     *
     * @see #getSelectedItems()
     * @see SelectionModel#getSelectedItems
     */
    @Override
    public Set<T> getValue() {
        return getSelectedItems();
    }

    /**
     * Sets the value of this object which is a set of items to select. If the
     * new value is not equal to {@code getValue()}, fires a value change event.
     * May throw {@code IllegalArgumentException} if the value is not
     * acceptable.
     * <p>
     * The method effectively selects the given items and deselects previously
     * selected. The call is delegated to
     * {@link Multi#updateSelection(Set, Set)}.
     *
     * @see Multi#updateSelection(Set, Set)
     *
     * @param value
     *            the items to select, not {@code null}
     * @throws NullPointerException
     *             if the value is invalid
     */
    @Override
    public void setValue(Set<T> value) {
        Objects.requireNonNull(value);
        Set<T> copy = value.stream().map(Objects::requireNonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        updateSelection(copy, new LinkedHashSet<>(getSelectedItems()));
    }

    /**
     * Adds a value change listener. The listener is called when the selection
     * set of this multi select is changed either by the user or
     * programmatically.
     *
     * @see #addSelectionListener(MultiSelectionListener)
     *
     * @param listener
     *            the value change listener, not null
     * @return a registration for the listener
     */
    @Override
    public Registration addValueChangeListener(
            HasValue.ValueChangeListener<Set<T>> listener) {
        return addSelectionListener(
                event -> listener.valueChange(new ValueChangeEvent<>(this,
                        event.getOldValue(), event.isUserOriginated())));
    }

    /**
     * Returns the item enabled provider for this multiselect.
     * <p>
     * <em>Implementation note:</em> Override this method and
     * {@link #setItemEnabledProvider(SerializablePredicate)} as {@code public}
     * and invoke {@code super} methods to support this feature in the
     * multiselect component.
     *
     * @return the item enabled provider, not {@code null}
     * @see #setItemEnabledProvider(SerializablePredicate)
     */
    protected SerializablePredicate<T> getItemEnabledProvider() {
        return itemEnabledProvider;
    }

    /**
     * Sets the item enabled predicate for this multiselect. The predicate is
     * applied to each item to determine whether the item should be enabled (
     * {@code true}) or disabled ({@code false}). Disabled items are displayed
     * as grayed out and the user cannot select them. The default predicate
     * always returns {@code true} (all the items are enabled).
     * <p>
     * <em>Implementation note:</em> Override this method and
     * {@link #getItemEnabledProvider()} as {@code public} and invoke
     * {@code super} methods to support this feature in the multiselect
     * component.
     *
     * @param itemEnabledProvider
     *            the item enabled provider to set, not {@code null}
     */
    protected void setItemEnabledProvider(
            SerializablePredicate<T> itemEnabledProvider) {
        Objects.requireNonNull(itemEnabledProvider);
        this.itemEnabledProvider = itemEnabledProvider;
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
    protected AbstractMultiSelectState getState() {
        return (AbstractMultiSelectState) super.getState();
    }

    @Override
    protected AbstractMultiSelectState getState(boolean markAsDirty) {
        return (AbstractMultiSelectState) super.getState(markAsDirty);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly();
    }

    @Override
    public void updateSelection(Set<T> addedItems, Set<T> removedItems) {
        updateSelection(addedItems, removedItems, false);
    }

    /**
     * Updates the selection by adding and removing the given items.
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

        updateSelection(set -> {
            // order of add / remove does not matter since no duplicates
            set.removeAll(removedItems);
            set.addAll(addedItems);
        }, userOriginated);
    }

    @Override
    public Set<T> getSelectedItems() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(selection));
    }

    @Override
    public void deselectAll() {
        if (selection.isEmpty()) {
            return;
        }

        updateSelection(Collection::clear, false);
    }

    @Override
    public boolean isSelected(T item) {
        DataProvider<T, ?> dataProvider = internalGetDataProvider();
        Object id = dataProvider.getId(item);
        return selection.stream().map(dataProvider::getId).anyMatch(id::equals);

    }

    /**
     * Deselects the given item. If the item is not currently selected, does
     * nothing.
     *
     * @param item
     *            the item to deselect, not null
     * @param userOriginated
     *            {@code true} if this was used originated, {@code false} if not
     */
    protected void deselect(T item, boolean userOriginated) {
        if (!selection.contains(item)) {
            return;
        }

        updateSelection(set -> set.remove(item), userOriginated);
    }

    /**
     * Removes the given items. Any item that is not currently selected, is
     * ignored. If none of the items are selected, does nothing.
     *
     * @param items
     *            the items to deselect, not {@code null}
     * @param userOriginated
     *            {@code true} if this was used originated, {@code false} if not
     */
    protected void deselect(Set<T> items, boolean userOriginated) {
        Objects.requireNonNull(items);
        if (items.stream().noneMatch(i -> isSelected(i))) {
            return;
        }

        updateSelection(set -> set.removeAll(items), userOriginated);
    }

    /**
     * Selects the given item. Depending on the implementation, may cause other
     * items to be deselected. If the item is already selected, does nothing.
     *
     * @param item
     *            the item to select, not null
     * @param userOriginated
     *            {@code true} if this was used originated, {@code false} if not
     */
    protected void select(T item, boolean userOriginated) {
        if (selection.contains(item)) {
            return;
        }

        updateSelection(set -> set.add(item), userOriginated);
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> attributes = super.getCustomAttributes();
        // "value" is not an attribute for the component. "selected" attribute
        // is used in "option"'s tag to mark selection which implies value for
        // multiselect component
        attributes.add("value");
        return attributes;
    }

    @Override
    protected Element writeItem(Element design, T item, DesignContext context) {
        Element element = super.writeItem(design, item, context);

        if (isSelected(item)) {
            element.attr("selected", "");
        }

        return element;
    }

    @Override
    protected void readItems(Element design, DesignContext context) {
        Set<T> selected = new HashSet<>();
        List<T> items = design.children().stream()
                .map(child -> readItem(child, selected, context))
                .collect(Collectors.toList());
        deselectAll();
        if (!items.isEmpty()) {
            setItems(items);
        }
        selected.forEach(this::select);
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

    private void updateSelection(SerializableConsumer<Collection<T>> handler,
            boolean userOriginated) {
        LinkedHashSet<T> oldSelection = new LinkedHashSet<>(selection);
        handler.accept(selection);

        fireEvent(new MultiSelectionEvent<>(AbstractMultiSelect.this,
                oldSelection, userOriginated));

        getDataCommunicator().reset();
    }

    private final void refreshSelectedItem(T item) {
        DataProvider<T, ?> dataProvider = internalGetDataProvider();
        Object id = dataProvider.getId(item);
        for (int i = 0; i < selection.size(); ++i) {
            if (id.equals(dataProvider.getId(selection.get(i)))) {
                selection.set(i, item);
                return;
            }
        }
    }
}
