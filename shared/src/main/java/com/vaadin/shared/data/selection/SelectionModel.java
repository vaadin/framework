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
package com.vaadin.shared.data.selection;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Models the selection logic of a {@code Listing} component. Determines how
 * items can be selected and deselected.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the type of the items to select
 * @since 8.0
 */
public interface SelectionModel<T> extends Serializable {

    /**
     * A selection model in which at most one item can be selected at a time.
     * Selecting another item deselects the originally selected item.
     *
     * @param <T>
     *            the type of the items to select
     */
    public interface Single<T> extends SelectionModel<T> {

        /**
         * Selects the given item. If another item was already selected, that
         * item is deselected.
         */
        @Override
        public void select(T item);

        /**
         * Returns the currently selected item, or an empty optional if no item
         * is selected.
         *
         * @return an optional of the selected item if any, an empty optional
         *         otherwise
         */
        public Optional<T> getSelectedItem();

        /**
         * Sets the current selection to the given item, or clears selection if
         * given {@code null}.
         * 
         * @param item
         *            the item to select or {@code null} to clear selection
         */
        public default void setSelectedItem(T item) {
            if (item != null) {
                select(item);
            } else {
                deselectAll();
            }
        }

        /**
         * Returns a singleton set of the currently selected item or an empty
         * set if no item is selected.
         *
         * @return a singleton set of the selected item if any, an empty set
         *         otherwise
         * 
         * @see #getSelectedItem()
         */
        @Override
        default Set<T> getSelectedItems() {
            return getSelectedItem().map(Collections::singleton)
                    .orElse(Collections.emptySet());
        }
    }

    /**
     * A selection model in which multiple items can be selected at the same
     * time. Selecting an item adds it to the selection.
     *
     * @param <T>
     *            the type of the items to select
     */
    public interface Multi<T> extends SelectionModel<T> {

        /**
         * Adds the given items to the set of currently selected items.
         */
        @Override
        public void select(T item);
    }

    /**
     * Returns an immutable set of the currently selected items. It is safe to
     * invoke other {@code SelectionModel} methods while iterating over the set.
     * <p>
     * <em>Implementation note:</em> the iteration order of the items in the
     * returned set should be well-defined and documented by the implementing
     * class.
     *
     * @return the items in the current selection, not null
     */
    public Set<T> getSelectedItems();

    /**
     * Selects the given item. Depending on the implementation, may cause other
     * items to be deselected. If the item is already selected, does nothing.
     *
     * @param item
     *            the item to select, not null
     */
    public void select(T item);

    /**
     * Deselects the given item. If the item is not currently selected, does
     * nothing.
     *
     * @param item
     *            the item to deselect, not null
     */
    public void deselect(T item);

    /**
     * Deselects all currently selected items.
     */
    public default void deselectAll() {
        getSelectedItems().forEach(this::deselect);
    }

    /**
     * Returns whether the given item is currently selected.
     *
     * @param item
     *            the item to check, not null
     * @return {@code true} if the item is selected, {@code false} otherwise
     */
    public default boolean isSelected(T item) {
        return getSelectedItems().contains(item);
    }
}
