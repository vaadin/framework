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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.vaadin.data.HasValue;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.shared.Registration;

/**
 * Multi selection component which allows to select and deselect multiple items.
 *
 * @author Vaadin Ltd
 *
 * @param <T>
 *            the type of the items to select
 *
 * @since 8.0
 *
 */
public interface MultiSelect<T> extends HasValue<Set<T>>, Serializable {

    /**
     * Adds the given items to the set of currently selected items.
     * <p>
     * By default this does not clear any previous selection. To do that, use
     * {@link #deselectAll()}.
     * <p>
     * If the all the items were already selected, this is a NO-OP.
     * <p>
     * This is a short-hand for {@link #updateSelection(Set, Set)} with nothing
     * to deselect.
     *
     * @param items
     *            to add to selection, not {@code null}
     */
    public default void select(T... items) {
        Objects.requireNonNull(items);
        Stream.of(items).forEach(Objects::requireNonNull);

        updateSelection(new LinkedHashSet<>(Arrays.asList(items)),
                Collections.emptySet());
    }

    /**
     * Removes the given items from the set of currently selected items.
     * <p>
     * If the none of the items were selected, this is a NO-OP.
     * <p>
     * This is a short-hand for {@link #updateSelection(Set, Set)} with nothing
     * to select.
     *
     * @param items
     *            to remove from selection, not {@code null}
     */
    public default void deselect(T... items) {
        Objects.requireNonNull(items);
        Stream.of(items).forEach(Objects::requireNonNull);

        updateSelection(Collections.emptySet(),
                new LinkedHashSet<>(Arrays.asList(items)));
    }

    /**
     * Updates the selection by adding and removing the given items from it.
     * <p>
     * If all the added items were already selected and the removed items were
     * not selected, this is a NO-OP.
     * <p>
     * Duplicate items (in both add & remove sets) are ignored.
     *
     * @param addedItems
     *            the items to add, not {@code null}
     * @param removedItems
     *            the items to remove, not {@code null}
     */
    public void updateSelection(Set<T> addedItems, Set<T> removedItems);

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

    /**
     * Adds a selection listener that will be called when the selection is
     * changed either by the user or programmatically.
     *
     * @param listener
     *            the value change listener, not {@code null}
     * @return a registration for the listener
     */
    public Registration addSelectionListener(
            MultiSelectionListener<T> listener);

    /**
     * MultiSelect empty value should always be an empty set by default and not
     * {@code null}.
     *
     * @return An empty set, not {@code null}
     */
    public default Set<T> getEmptyValue() {
        return Collections.emptySet();
    }

}
