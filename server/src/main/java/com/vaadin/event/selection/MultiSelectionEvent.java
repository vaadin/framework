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
import java.util.Set;

import com.vaadin.event.ConnectorEvent;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.ui.AbstractListing;

/**
 * Event fired when the the selection changes in a
 * {@link com.vaadin.shared.data.selection.SelectionModel.Multi}.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <T>
 *            the data type of the selection model
 */
public class MultiSelectionEvent<T> extends ConnectorEvent {

    private Set<T> oldSelection;
    private Set<T> newSelection;

    /**
     * Creates a new event.
     *
     * @param source
     *            the listing component in which the selection changed
     * @param oldSelection
     *            the old set of selected items
     * @param newSelection
     *            the new set of selected items
     */
    public MultiSelectionEvent(
            AbstractListing<T, SelectionModel.Multi<T>> source,
            Set<T> oldSelection, Set<T> newSelection) {
        super(source);
        this.oldSelection = oldSelection;
        this.newSelection = newSelection;
    }

    /**
     * Gets the new selection.
     *
     * @return a set of items selected after the selection was changed
     */
    public Set<T> getNewSelection() {
        return Collections.unmodifiableSet(newSelection);
    }

    /**
     * Gets the old selection.
     *
     * @return a set of items selected before the selection was changed
     */
    public Set<T> getOldSelection() {
        return Collections.unmodifiableSet(oldSelection);
    }

}
