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
package com.vaadin.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.vaadin.event.selection.SelectionListener;
import com.vaadin.shared.Registration;

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
                getSelectedItem().ifPresent(this::deselect);
            }
        }

        @Override
        public default void deselectAll() {
            setSelectedItem(null);
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
        public default Set<T> getSelectedItems() {
            return getSelectedItem().map(Collections::singleton)
                    .orElse(Collections.emptySet());
        }

        @Override
        default Optional<T> getFirstSelectedItem() {
            return getSelectedItem();
        }

        /**
         * Sets whether it's allowed to deselect the selected row through the
         * UI. Deselection is allowed by default.
         *
         * @param deselectAllowed
         *            <code>true</code> if the selected row can be deselected
         *            without selecting another row instead; otherwise
         *            <code>false</code>.
         */
        public void setDeselectAllowed(boolean deselectAllowed);

        /**
         * Gets whether it's allowed to deselect the selected row through the
         * UI.
         *
         * @return <code>true</code> if deselection is allowed; otherwise
         *         <code>false</code>
         */
        public boolean isDeselectAllowed();
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
         * Adds the given item to the set of currently selected items.
         * <p>
         * By default this does not clear any previous selection. To do that,
         * use {@link #deselectAll()}.
         * <p>
         * If the the item was already selected, this is a NO-OP.
         *
         * @param item
         *            the item to add to selection, not {@code null}
         */
        @Override
        public default void select(T item) {
            Objects.requireNonNull(item);
            selectItems(item);
        };

        /**
         * Adds the given items to the set of currently selected items.
         * <p>
         * By default this does not clear any previous selection. To do that,
         * use {@link #deselectAll()}.
         * <p>
         * If the all the items were already selected, this is a NO-OP.
         * <p>
         * This is a short-hand for {@link #updateSelection(Set, Set)} with
         * nothing to deselect.
         *
         * @param items
         *            to add to selection, not {@code null}
         */
        public default void selectItems(T... items) {
            Objects.requireNonNull(items);
            Stream.of(items).forEach(Objects::requireNonNull);

            updateSelection(new LinkedHashSet<>(Arrays.asList(items)),
                    Collections.emptySet());
        }

        @SuppressWarnings("unchecked")
        @Override
        public default void deselect(T item) {
            deselectItems(item);
        }

        /**
         * Removes the given items from the set of currently selected items.
         * <p>
         * If the none of the items were selected, this is a NO-OP.
         * <p>
         * This is a short-hand for {@link #updateSelection(Set, Set)} with
         * nothing to select.
         *
         * @param items
         *            to remove from selection, not {@code null}
         */
        public default void deselectItems(T... items) {
            Objects.requireNonNull(items);
            Stream.of(items).forEach(Objects::requireNonNull);

            updateSelection(Collections.emptySet(),
                    new LinkedHashSet<>(Arrays.asList(items)));
        }

        /**
         * Updates the selection by adding and removing the given items from it.
         * <p>
         * If all the added items were already selected and the removed items
         * were not selected, this is a NO-OP.
         * <p>
         * Duplicate items (in both add &amp; remove sets) are ignored.
         *
         * @param addedItems
         *            the items to add, not {@code null}
         * @param removedItems
         *            the items to remove, not {@code null}
         */
        public void updateSelection(Set<T> addedItems, Set<T> removedItems);

        @Override
        default Optional<T> getFirstSelectedItem() {
            return getSelectedItems().stream().findFirst();
        }

        /**
         * Selects all available the items.
         */
        public void selectAll();
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
     * Get first selected data item.
     * <p>
     * This is the same as {@link Single#getSelectedItem()} in case of single
     * selection and the first selected item from
     * {@link Multi#getSelectedItems()} in case of multiselection.
     *
     * @return the first selected item.
     */
    Optional<T> getFirstSelectedItem();

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
     * Deselects all currently selected items, if any.
     */
    public void deselectAll();

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

    /**
     * Adds a generic listener to this selection model, accepting both single
     * and multiselection events.
     *
     * @param listener
     *            the listener to add
     * @return a registration handle for removing the listener
     */
    public Registration addSelectionListener(SelectionListener<T> listener);
}
