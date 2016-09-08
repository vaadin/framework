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

import java.util.Optional;

import com.vaadin.data.HasValue.ValueChange;
import com.vaadin.ui.AbstractListing;

/**
 * Fired when the selection changes in a listing component.
 * 
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the type of the selected item
 * @since 8.0
 */
public class SingleSelectionChange<T> extends ValueChange<T> {

    /**
     * Creates a new selection change event.
     * 
     * @param source
     *            the listing that fired the event
     * @param selectedItem
     *            the selected item or {@code null} if deselected
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     */
    public SingleSelectionChange(AbstractListing<T, ?> source,
            T selectedItem, boolean userOriginated) {
        super(source, selectedItem, userOriginated);
    }

    /**
     * Returns an optional of the item that was selected, or an empty optional
     * if a previously selected item was deselected.
     * 
     * @return the selected item or an empty optional if deselected
     *
     * @see SelectionModel.Single#getSelectedItem()
     */
    public Optional<T> getSelectedItem() {
        return Optional.ofNullable(getValue());
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractListing<T, ?> getSource() {
        return (AbstractListing<T, ?>) super.getSource();
    }
}
