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
package com.vaadin.client.connectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.HasRequiredIndicator;
import com.vaadin.shared.Range;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.selection.MultiSelectServerRpc;
import com.vaadin.shared.ui.ListingJsonConstants;
import com.vaadin.shared.ui.abstractmultiselect.AbstractMultiSelectState;

import elemental.json.JsonObject;

/**
 * A base connector class for multiselects.
 * <p>
 * Does not care about the framework provided selection model for now, instead
 * just passes selection information per item.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.0
 */
public abstract class AbstractMultiSelectConnector
        extends AbstractListingConnector implements HasRequiredIndicator {

    /**
     * Abstraction layer to help populate different multiselect widgets based on
     * same JSON data.
     */
    public interface MultiSelectWidget {

        /**
         * Sets the given items to the select.
         *
         * @param items
         *            the items for the select
         */
        void setItems(List<JsonObject> items);

        /**
         * Adds a selection change listener the select.
         *
         * @param selectionChangeListener
         *            the listener to add, not {@code null}
         * @return a registration handle to remove the listener
         */
        Registration addSelectionChangeListener(
                BiConsumer<Set<String>, Set<String>> selectionChangeListener);

        /**
         * Returns the caption for the given item.
         *
         * @param item
         *            the item, not {@code null}
         * @return caption of the item
         */
        static String getCaption(JsonObject item) {
            return item.getString(ListingJsonConstants.JSONKEY_ITEM_VALUE);
        }

        /**
         * Returns the key for the given item.
         *
         * @param item
         *            the item, not {@code null}
         * @return key of the item
         */
        static String getKey(JsonObject item) {
            return getRowKey(item);
        }

        /**
         * Returns whether the given item is enabled or not.
         * <p>
         * Disabling items is not supported by all multiselects.
         *
         * @param item
         *            the item, not {@code null}
         * @return {@code true} enabled, {@code false} if not
         */
        static boolean isEnabled(JsonObject item) {
            return !(item.hasKey(ListingJsonConstants.JSONKEY_ITEM_DISABLED)
                    && item.getBoolean(
                            ListingJsonConstants.JSONKEY_ITEM_DISABLED));
        }

        /**
         * Returns whether this item is selected or not.
         *
         * @param item
         *            the item, not {@code null}
         * @return {@code true} is selected, {@code false} if not
         */
        static boolean isSelected(JsonObject item) {
            return item.getBoolean(ListingJsonConstants.JSONKEY_ITEM_SELECTED);
        }

        /**
         * Returns the optional icon URL for the given item.
         * <p>
         * Item icons are not supported by all multiselects.
         *
         * @param item
         *            the item
         * @return the optional icon URL, or an empty optional if none specified
         */
        static Optional<String> getIconUrl(JsonObject item) {
            return Optional.ofNullable(
                    item.getString(ListingJsonConstants.JSONKEY_ITEM_ICON));
        }
    }

    /**
     * Returns the multiselect widget for this connector.
     * <p>
     * This is used because {@link #getWidget()} returns a class
     * ({@link Widget}) instead of an interface ({@link IsWidget}), and most
     * multiselects extends {@link Composite}.
     *
     * @return the multiselect widget
     */
    public abstract MultiSelectWidget getMultiSelectWidget();

    @Override
    protected void init() {
        super.init();

        MultiSelectServerRpc rpcProxy = getRpcProxy(MultiSelectServerRpc.class);
        getMultiSelectWidget().addSelectionChangeListener(
                (addedItems, removedItems) -> rpcProxy
                        .updateSelection(addedItems, removedItems));
    }

    @Override
    public AbstractMultiSelectState getState() {
        return (AbstractMultiSelectState) super.getState();
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        dataSource.addDataChangeHandler(this::onDataChange);
        super.setDataSource(dataSource);
    }

    /**
     * This method handles the parsing of the new JSON data containing the items
     * and the selection information.
     *
     * @param range
     *            the updated range, never {@code null}
     */
    protected void onDataChange(Range range) {
        assert range.getStart() == 0
                && range.getEnd() == getDataSource().size() : getClass()
                        .getSimpleName()
                        + " only supports full updates, but got range " + range;
        List<JsonObject> items = new ArrayList<>(range.length());
        for (int i = 0; i < range.getEnd(); i++) {
            items.add(getDataSource().getRow(i));
        }
        getMultiSelectWidget().setItems(items);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return getState().required && !isReadOnly();
    }
}
