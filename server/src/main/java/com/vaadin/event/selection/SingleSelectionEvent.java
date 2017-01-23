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
import java.util.Optional;
import java.util.Set;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.SelectionModel;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.SingleSelect;

/**
 * Fired when the selection changes in a listing component.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the type of the selected item
 * @since 8.0
 */
public class SingleSelectionEvent<T> extends ValueChangeEvent<T>
        implements SelectionEvent<T> {

    /**
     * Creates a new selection change event.
     *
     * @param source
     *            the listing that fired the event
     * @param oldSelection
     *            the item that was previously selected
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     */
    public SingleSelectionEvent(AbstractSingleSelect<T> source, T oldSelection,
            boolean userOriginated) {
        super(source, oldSelection, userOriginated);
    }

    /**
     * Creates a new selection change event in a component.
     *
     * @param component
     *            the component where the event originated
     * @param source
     *            the single select source
     * @param oldSelection
     *            the item that was previously selected
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     */
    public SingleSelectionEvent(Component component, SingleSelect<T> source,
            T oldSelection, boolean userOriginated) {
        super(component, source, oldSelection, userOriginated);
    }

    /**
     * Returns an optional of the item that was selected, or an empty optional
     * if a previously selected item was deselected.
     * <p>
     * The result is the current selection of the source
     * {@link AbstractSingleSelect} object. So it's always exactly the same as
     * optional describing {@link AbstractSingleSelect#getValue()}.
     *
     * @see #getValue()
     *
     * @return the selected item or an empty optional if deselected
     *
     * @see SelectionModel.Single#getSelectedItem()
     */
    public Optional<T> getSelectedItem() {
        return Optional.ofNullable(getValue());
    }

    /**
     * The single select on which the Event initially occurred.
     *
     * @return The single select on which the Event initially occurred.
     */
    @Override
    public SingleSelect<T> getSource() {
        return (SingleSelect<T>) super.getSource();
    }

    @Override
    public Optional<T> getFirstSelectedItem() {
        return getSelectedItem();
    }

    @Override
    public Set<T> getAllSelectedItems() {
        Optional<T> selectedItem = getSelectedItem();
        if (selectedItem.isPresent()) {
            return Collections.singleton(selectedItem.get());
        } else {
            return Collections.emptySet();
        }
    }
}
