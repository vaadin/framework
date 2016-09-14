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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.vaadin.data.Listing;
import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.data.DataGenerator;
import com.vaadin.server.data.DataSource;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.optiongroup.CheckBoxGroupConstants;
import com.vaadin.shared.ui.optiongroup.CheckBoxGroupState;

import elemental.json.JsonObject;

/**
 * A group of Checkboxes. Individual checkboxes are made from items supplied by
 * a {@link DataSource}. Checkboxes may have captions and icons.
 *
 * @param <T>
 *            item type
 * @author Vaadin Ltd
 * @since 8.0
 */
public class CheckBoxGroup<T> extends AbstractMultiSelect<T> {

    private final class SimpleMultiSelectModel
            implements SelectionModel.Multi<T> {

        private Set<T> selection = new LinkedHashSet<>();

        @Override
        public void select(T item) {
            // Not user originated
            select(item, false);
        }

        private void select(T item, boolean userOriginated) {
            if (selection.contains(item)) {
                return;
            }

            updateSelection(set -> set.add(item), userOriginated);
        }

        @Override
        public Set<T> getSelectedItems() {
            return Collections.unmodifiableSet(selection);
        }

        @Override
        public void deselect(T item) {
            // Not user originated
            deselect(item, false);
        }

        private void deselect(T item, boolean userOriginated) {
            if (!selection.contains(item)) {
                return;
            }

            updateSelection(set -> set.remove(item), userOriginated);
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

            fireEvent(new MultiSelectionEvent<>(CheckBoxGroup.this,
                    oldSelection, newSelection, userOriginated));

            getDataCommunicator().reset();
        }

        @Override
        public boolean isSelected(T item) {
            return selection.contains(item);
        }
    }

    private Function<T, Resource> itemIconProvider = item -> null;

    private Function<T, String> itemCaptionProvider = String::valueOf;

    private Predicate<T> itemEnabledProvider = item -> true;

    /**
     * Constructs a new CheckBoxGroup with caption.
     *
     * @param caption
     *            caption text
     * @see Listing#setDataSource(DataSource)
     */
    public CheckBoxGroup(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new CheckBoxGroup with caption and DataSource.
     *
     * @param caption
     *            the caption text
     * @param dataSource
     *            the data source, not null
     * @see Listing#setDataSource(DataSource)
     */
    public CheckBoxGroup(String caption, DataSource<T> dataSource) {
        this(caption);
        setDataSource(dataSource);
    }

    /**
     * Constructs a new CheckBoxGroup with caption and DataSource containing
     * given items.
     *
     * @param caption
     *            the caption text
     * @param items
     *            the data items to use, not null
     * @see Listing#setDataSource(DataSource)
     */
    public CheckBoxGroup(String caption, Collection<T> items) {
        this(caption, DataSource.create(items));
    }

    /**
     * Constructs a new CheckBoxGroup.
     *
     * @see Listing#setDataSource(DataSource)
     */
    public CheckBoxGroup() {
        setSelectionModel(new SimpleMultiSelectModel());

        registerRpc(new SelectionServerRpc() {

            @Override
            public void select(String key) {
                getItemForSelectionChange(key).ifPresent(
                        item -> getSelectionModel().select(item, true));
            }

            @Override
            public void deselect(String key) {
                getItemForSelectionChange(key).ifPresent(
                        item -> getSelectionModel().deselect(item, true));
            }

            private Optional<T> getItemForSelectionChange(String key) {
                T item = getDataCommunicator().getKeyMapper().get(key);
                if (item == null || !itemEnabledProvider.test(item)) {
                    return Optional.empty();
                }

                return Optional.of(item);
            }

            private SimpleMultiSelectModel getSelectionModel() {
                return (SimpleMultiSelectModel) CheckBoxGroup.this
                        .getSelectionModel();
            }
        });

        addDataGenerator(new DataGenerator<T>() {
            @Override
            public void generateData(T data, JsonObject jsonObject) {
                jsonObject.put(CheckBoxGroupConstants.JSONKEY_ITEM_VALUE,
                        itemCaptionProvider.apply(data));
                Resource icon = itemIconProvider.apply(data);
                if (icon != null) {
                    String iconUrl = ResourceReference
                            .create(icon, CheckBoxGroup.this, null).getURL();
                    jsonObject.put(CheckBoxGroupConstants.JSONKEY_ITEM_ICON,
                            iconUrl);
                }
                if (!itemEnabledProvider.test(data)) {
                    jsonObject.put(CheckBoxGroupConstants.JSONKEY_ITEM_DISABLED,
                            true);
                }

                if (getSelectionModel().isSelected(data)) {
                    jsonObject.put(CheckBoxGroupConstants.JSONKEY_ITEM_SELECTED,
                            true);
                }
            }

            @Override
            public void destroyData(T data) {
            }
        });

    }

    /**
     * Sets whether html is allowed in the item captions. If set to true, the
     * captions are passed to the browser as html and the developer is
     * responsible for ensuring no harmful html is used. If set to false, the
     * content is passed to the browser as plain text.
     *
     * @param htmlContentAllowed
     *            true if the captions are used as html, false if used as plain
     *            text
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        getState().htmlContentAllowed = htmlContentAllowed;
    }

    /**
     * Checks whether captions are interpreted as html or plain text.
     *
     * @return true if the captions are used as html, false if used as plain
     *         text
     * @see #setHtmlContentAllowed(boolean)
     */
    public boolean isHtmlContentAllowed() {
        return getState(false).htmlContentAllowed;
    }

    @Override
    protected CheckBoxGroupState getState() {
        return (CheckBoxGroupState) super.getState();
    }

    @Override
    protected CheckBoxGroupState getState(boolean markAsDirty) {
        return (CheckBoxGroupState) super.getState(markAsDirty);
    }

    /**
     * Returns the item icons provider.
     *
     * @return the icons provider for items
     * @see #setItemIconProvider
     */
    public Function<T, Resource> getItemIconProvider() {
        return itemIconProvider;
    }

    /**
     * Sets the item icon provider for this checkbox group. The icon provider is
     * queried for each item to optionally display an icon next to the item
     * caption. If the provider returns null for an item, no icon is displayed.
     * The default provider always returns null (no icons).
     *
     * @param itemIconProvider
     *            icons provider, not null
     */
    public void setItemIconProvider(Function<T, Resource> itemIconProvider) {
        Objects.nonNull(itemIconProvider);
        this.itemIconProvider = itemIconProvider;
    }

    /**
     * Returns the item caption provider.
     *
     * @return the captions provider
     * @see #setItemCaptionProvider
     */
    public Function<T, String> getItemCaptionProvider() {
        return itemCaptionProvider;
    }

    /**
     * Sets the item caption provider for this checkbox group. The caption
     * provider is queried for each item to optionally display an item textual
     * representation. The default provider returns
     * {@code String.valueOf(item)}.
     *
     * @param itemCaptionProvider
     *            the item caption provider, not null
     */
    public void setItemCaptionProvider(
            Function<T, String> itemCaptionProvider) {
        Objects.nonNull(itemCaptionProvider);
        this.itemCaptionProvider = itemCaptionProvider;
    }

    /**
     * Returns the item enabled predicate.
     *
     * @return the item enabled predicate
     * @see #setItemEnabledProvider
     */
    public Predicate<T> getItemEnabledProvider() {
        return itemEnabledProvider;
    }

    /**
     * Sets the item enabled predicate for this checkbox group. The predicate is
     * applied to each item to determine whether the item should be enabled
     * (true) or disabled (false). Disabled items are displayed as grayed out
     * and the user cannot select them. The default predicate always returns
     * true (all the items are enabled).
     *
     * @param itemEnabledProvider
     *            the item enable predicate, not null
     */
    public void setItemEnabledProvider(Predicate<T> itemEnabledProvider) {
        Objects.nonNull(itemEnabledProvider);
        this.itemEnabledProvider = itemEnabledProvider;
    }
}
