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
package com.vaadin.client.widget.grid.selection;

import com.vaadin.shared.data.DataCommunicatorConstants;

import elemental.json.JsonObject;

/**
 * Models the selection logic of a {@code Grid} component. Determines how items
 * can be selected and deselected.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the type of the items to select
 * @since 8.0
 */
public interface SelectionModel<T> {

    public static class NoSelectionModel<T> implements SelectionModel<T> {

        @Override
        public void select(T item) {
        }

        @Override
        public void deselect(T item) {
        }

        @Override
        public boolean isSelected(T item) {
            return false;
        }

        @Override
        public void deselectAll() {
        }

        @Override
        public void setSelectionAllowed(boolean selectionAllowed) {
        }

        @Override
        public boolean isSelectionAllowed() {
            return false;
        }
    }

    /**
     * Selects the given item. If another item was already selected, that item
     * is deselected.
     *
     * @param item
     *            the item to select, not null
     */
    void select(T item);

    /**
     * Deselects the given item. If the item is not currently selected, does
     * nothing.
     *
     * @param item
     *            the item to deselect, not null
     */
    void deselect(T item);

    /**
     * Returns whether the given item is currently selected.
     *
     * @param item
     *            the item to check, not null
     * @return {@code true} if the item is selected, {@code false} otherwise
     */
    boolean isSelected(T item);

    /**
     * Deselects all currently selected items.
     */
    void deselectAll();

    /**
     * Sets whether the user is allowed to change the selection.
     * <p>
     * The check is done only for the client side actions. It doesn't affect
     * selection requests sent from the server side.
     *
     * @param selectionAllowed
     *            <code>true</code> if the user is allowed to change the
     *            selection, <code>false</code> otherwise
     */
    void setSelectionAllowed(boolean selectionAllowed);

    /**
     * Checks if the user is allowed to change the selection.
     * <p>
     * The check is done only for the client side actions. It doesn't affect
     * selection requests sent from the server side.
     *
     * @return <code>true</code> if the user is allowed to change the selection,
     *         <code>false</code> otherwise
     */
    boolean isSelectionAllowed();

    /**
     * Gets the selected state from a given grid row json object. This is a
     * helper method for grid selection models.
     *
     * @param item
     *            a json object
     * @return {@code true} if the json object is marked as selected;
     *         {@code false} if not
     */
    public static boolean isItemSelected(JsonObject item) {
        return item.hasKey(DataCommunicatorConstants.SELECTED)
                && item.getBoolean(DataCommunicatorConstants.SELECTED);
    }

}
