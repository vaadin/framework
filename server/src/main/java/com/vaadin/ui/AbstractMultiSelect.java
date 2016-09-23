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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.data.DataGenerator;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.MultiSelectServerRpc;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.shared.data.selection.SelectionModel.Multi;
import com.vaadin.shared.ui.ListingJsonConstants;
import com.vaadin.util.ReflectTools;

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
public abstract class AbstractMultiSelect<T>
        extends AbstractListing<T, Multi<T>> {

    /**
     * Simple implementation of multiselectmodel.
     */
    protected class SimpleMultiSelectModel implements SelectionModel.Multi<T> {

        private Set<T> selection = new LinkedHashSet<>();

        @Override
        public void select(T item) {
            // Not user originated
            select(item, false);
        }

        /**
         * Selects the given item. Depending on the implementation, may cause
         * other items to be deselected. If the item is already selected, does
         * nothing.
         *
         * @param item
         *            the item to select, not null
         * @param userOriginated
         *            {@code true} if this was used originated, {@code false} if
         *            not
         */
        protected void select(T item, boolean userOriginated) {
            if (selection.contains(item)) {
                return;
            }

            updateSelection(set -> set.add(item), userOriginated);
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
         *            {@code true} if this was used originated, {@code false} if
         *            not
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
        public void deselect(T item) {
            // Not user originated
            deselect(item, false);
        }

        /**
         * Deselects the given item. If the item is not currently selected, does
         * nothing.
         *
         * @param item
         *            the item to deselect, not null
         * @param userOriginated
         *            {@code true} if this was used originated, {@code false} if
         *            not
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
         *            {@code true} if this was used originated, {@code false} if
         *            not
         */
        protected void deselect(Set<T> items, boolean userOriginated) {
            Objects.requireNonNull(items);
            if (items.stream().noneMatch(i -> isSelected(i))) {
                return;
            }

            updateSelection(set -> set.removeAll(items), userOriginated);
        }

        @Override
        public void deselectAll() {
            if (selection.isEmpty()) {
                return;
            }

            updateSelection(Set::clear, false);
        }

        private void updateSelection(Consumer<Set<T>> handler,
                boolean userOriginated) {
            LinkedHashSet<T> oldSelection = new LinkedHashSet<>(selection);
            handler.accept(selection);
            LinkedHashSet<T> newSelection = new LinkedHashSet<>(selection);

            fireEvent(new MultiSelectionEvent<>(AbstractMultiSelect.this,
                    oldSelection, newSelection, userOriginated));

            getDataCommunicator().reset();
        }

        @Override
        public boolean isSelected(T item) {
            return selection.contains(item);
        }
    }

    private class MultiSelectServerRpcImpl implements MultiSelectServerRpc {
        @Override
        public void updateSelection(Set<String> selectedItemKeys,
                Set<String> deselectedItemKeys) {
            getSelectionModel().updateSelection(
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

        private SimpleMultiSelectModel getSelectionModel() {
            return (SimpleMultiSelectModel) AbstractMultiSelect.this
                    .getSelectionModel();
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

            if (getSelectionModel().isSelected(data)) {
                jsonObject.put(ListingJsonConstants.JSONKEY_ITEM_SELECTED,
                        true);
            }
        }

        @Override
        public void destroyData(T data) {
        }
    }

    @Deprecated
    private static final Method SELECTION_CHANGE_METHOD = ReflectTools
            .findMethod(MultiSelectionListener.class, "accept",
                    MultiSelectionEvent.class);

    /**
     * The item icon caption provider.
     */
    private ItemCaptionGenerator<T> itemCaptionGenerator = String::valueOf;

    /**
     * The item icon provider. It is up to the implementing class to support
     * this or not.
     */
    private IconGenerator<T> itemIconGenerator = item -> null;

    /**
     * The item enabled status provider. It is up to the implementing class to
     * support this or not.
     */
    private Predicate<T> itemEnabledProvider = item -> true;

    /**
     * Creates a new multi select with an empty data source.
     */
    protected AbstractMultiSelect() {
        setSelectionModel(new SimpleMultiSelectModel());

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
    public Registration addSelectionListener(
            MultiSelectionListener<T> listener) {
        addListener(MultiSelectionEvent.class, listener,
                SELECTION_CHANGE_METHOD);
        return () -> removeListener(MultiSelectionEvent.class, listener);
    }

    /**
     * Gets the item caption generator that is used to produce the strings shown
     * in the select for each item.
     *
     * @return the item caption generator used, not {@code null}
     * @see #setItemCaptionGenerator(ItemCaptionGenerator)
     */
    public ItemCaptionGenerator<T> getItemCaptionGenerator() {
        return itemCaptionGenerator;
    }

    /**
     * Sets the item caption generator that is used to produce the strings shown
     * in the select for each item. By default, {@link String#valueOf(Object)}
     * is used.
     *
     * @param itemCaptionGenerator
     *            the item caption generator to use, not {@code null}
     */
    public void setItemCaptionGenerator(
            ItemCaptionGenerator<T> itemCaptionGenerator) {
        Objects.requireNonNull(itemCaptionGenerator);
        this.itemCaptionGenerator = itemCaptionGenerator;
        getDataCommunicator().reset();
    }

    /**
     * Returns the item icon generator for this multiselect.
     * <p>
     * <em>Implementation note:</em> Override this method and
     * {@link #setItemIconGenerator(IconGenerator)} as {@code public} and invoke
     * {@code super} methods to support this feature in the multiselect
     * component.
     *
     * @return the item icon generator, not {@code null}
     * @see #setItemIconGenerator(IconGenerator)
     */
    protected IconGenerator<T> getItemIconGenerator() {
        return itemIconGenerator;
    }

    /**
     * Sets the item icon generator for this multiselect. The icon generator is
     * queried for each item to optionally display an icon next to the item
     * caption. If the generator returns null for an item, no icon is displayed.
     * The default provider always returns null (no icons).
     * <p>
     * <em>Implementation note:</em> Override this method and
     * {@link #getItemIconGenerator()} as {@code public} and invoke
     * {@code super} methods to support this feature in the multiselect
     * component.
     *
     * @param itemIconGenerator
     *            the item icon generator to set, not {@code null}
     */
    protected void setItemIconGenerator(IconGenerator<T> itemIconGenerator) {
        Objects.requireNonNull(itemIconGenerator);
        this.itemIconGenerator = itemIconGenerator;
    }

    /**
     * Returns the item enabled provider for this multiselect.
     * <p>
     * <em>Implementation note:</em> Override this method and
     * {@link #setItemEnabledProvider(Predicate)} as {@code public} and invoke
     * {@code super} methods to support this feature in the multiselect
     * component.
     *
     * @return the item enabled provider, not {@code null}
     * @see #setItemEnabledProvider(Predicate)
     */
    protected Predicate<T> getItemEnabledProvider() {
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
    protected void setItemEnabledProvider(Predicate<T> itemEnabledProvider) {
        Objects.requireNonNull(itemEnabledProvider);
        this.itemEnabledProvider = itemEnabledProvider;
    }

}
