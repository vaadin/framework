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
package com.vaadin.event;

import java.io.Serializable;
import java.util.List;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.SortOrder;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;

/**
 * Event describing a change in sorting of a {@link DataProvider}. Fired by
 * {@link SortNotifier SortNotifiers}.
 *
 * @see SortListener
 * @see SortOrder
 * @param <T>
 *            the type of the sorting information
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public class SortEvent<T extends SortOrder<?>> extends Component.Event {

    private final List<T> sortOrder;
    private final boolean userOriginated;

    /**
     * Creates a new sort order change event with a sort order list.
     *
     * @param source
     *            the component from which the event originates
     * @param sortOrder
     *            the new sort order list
     * @param userOriginated
     *            <code>true</code> if event is a result of user interaction,
     *            <code>false</code> if from API call
     */
    public SortEvent(Component source, List<T> sortOrder,
            boolean userOriginated) {
        super(source);
        this.sortOrder = sortOrder;
        this.userOriginated = userOriginated;
    }

    /**
     * Gets the sort order list.
     *
     * @return the sort order list
     */
    public List<T> getSortOrder() {
        return sortOrder;
    }

    /**
     * Returns whether this event originated from actions done by the user.
     *
     * @return true if sort event originated from user interaction
     */
    public boolean isUserOriginated() {
        return userOriginated;
    }

    /**
     * Listener for sort order change events.
     *
     * @param <T>
     *            the type of the sorting information
     */
    @FunctionalInterface
    public interface SortListener<T extends SortOrder<?>> extends Serializable {
        /**
         * Called when the sort order has changed.
         *
         * @param event
         *            the sort order change event
         */
        public void sort(SortEvent<T> event);
    }

    /**
     * The interface for adding and removing listeners for {@link SortEvent
     * SortEvents}.
     *
     * @param <T>
     *            the type of the sorting information
     */
    public interface SortNotifier<T extends SortOrder<?>> extends Serializable {
        /**
         * Adds a sort order change listener that gets notified when the sort
         * order changes.
         *
         * @param listener
         *            the sort order change listener to add
         * @return a registration object for removing the listener
         */
        public Registration addSortListener(SortListener<T> listener);
    }
}
