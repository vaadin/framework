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
package com.vaadin.event.selection;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.ui.AbstractMultiSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.MultiSelect;

/**
 * Event fired when the the selection changes in a
 * {@link com.vaadin.data.SelectionModel.Multi}.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <T>
 *            the data type of the selection model
 */
public class MultiSelectionEvent<T> extends ValueChangeEvent<Set<T>>
        implements SelectionEvent<T> {

    /**
     * Creates a new event.
     *
     * @param source
     *            the listing component in which the selection changed
     * @param oldSelection
     *            the old set of selected items
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     */
    public MultiSelectionEvent(AbstractMultiSelect<T> source,
            Set<T> oldSelection, boolean userOriginated) {
        super(source, oldSelection, userOriginated);
    }

    /**
     * Creates a new selection change event in a multiselect component.
     *
     * @param component
     *            the component
     * @param source
     *            the multiselect source
     * @param oldSelection
     *            the old set of selected items
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     */
    public MultiSelectionEvent(Component component, MultiSelect<T> source,
            Set<T> oldSelection, boolean userOriginated) {
        super(component, source, oldSelection, userOriginated);
    }

    /**
     * Gets the new selection.
     * <p>
     * The result is the current selection of the source
     * {@link AbstractMultiSelect} object. So it's always exactly the same as
     * {@link AbstractMultiSelect#getValue()}
     *
     * @see #getValue()
     *
     * @return a set of items selected after the selection was changed
     */
    public Set<T> getNewSelection() {
        return getValue();
    }

    /**
     * Gets the old selection.
     *
     * @return a set of items selected before the selection was changed
     */
    public Set<T> getOldSelection() {
        return Collections.unmodifiableSet(getOldValue());
    }

    /**
     * Gets the items that were removed from selection.
     * <p>
     * This is just a convenience method for checking what was previously
     * selected in {@link #getOldSelection()} but not selected anymore in
     * {@link #getNewSelection()}.
     *
     * @return the items that were removed from selection
     */
    public Set<T> getRemovedSelection() {
        LinkedHashSet<T> copy = new LinkedHashSet<>(getOldValue());
        copy.removeAll(getNewSelection());
        return copy;
    }

    /**
     * Gets the items that were added to selection.
     * <p>
     * This is just a convenience method for checking what is new selected in
     * {@link #getNewSelection()} and wasn't selected in
     * {@link #getOldSelection()}.
     *
     * @return the items that were removed from selection
     */
    public Set<T> getAddedSelection() {
        LinkedHashSet<T> copy = new LinkedHashSet<>(getValue());
        copy.removeAll(getOldValue());
        return copy;
    }

    @Override
    public Optional<T> getFirstSelectedItem() {
        return getValue().stream().findFirst();
    }

    /**
     * The multiselect on which the Event initially occurred.
     *
     * @return the multiselect on which the Event initially occurred.
     */
    @Override
    public MultiSelect<T> getSource() {
        return (MultiSelect<T>) super.getSource();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the same as {@link #getValue()}.
     */
    @Override
    public Set<T> getAllSelectedItems() {
        return getValue();
    }
}
