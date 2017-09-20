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
package com.vaadin.v7.event;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An event that specifies what in a selection has changed, and where the
 * selection took place.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
@Deprecated
public class SelectionEvent extends EventObject {

    private LinkedHashSet<Object> oldSelection;
    private LinkedHashSet<Object> newSelection;

    public SelectionEvent(Object source, Collection<Object> oldSelection,
            Collection<Object> newSelection) {
        super(source);
        this.oldSelection = new LinkedHashSet<Object>(oldSelection);
        this.newSelection = new LinkedHashSet<Object>(newSelection);
    }

    /**
     * A {@link Collection} of all the itemIds that became selected.
     * <p>
     * <em>Note:</em> this excludes all itemIds that might have been previously
     * selected.
     *
     * @return a Collection of the itemIds that became selected
     */
    public Set<Object> getAdded() {
        return setDifference(newSelection, oldSelection);
    }

    /**
     * A {@link Collection} of all the itemIds that became deselected.
     * <p>
     * <em>Note:</em> this excludes all itemIds that might have been previously
     * deselected.
     *
     * @return a Collection of the itemIds that became deselected
     */
    public Set<Object> getRemoved() {
        return setDifference(oldSelection, newSelection);
    }

    /**
     * Slightly optimized set difference that can return the original set or a
     * modified one.
     *
     * @param set1
     *            original set
     * @param set2
     *            the set to subtract
     * @return the difference set
     */
    private static <T> Set<T> setDifference(Set<T> set1, Set<T> set2) {
        if (set2.isEmpty()) {
            return set1;
        } else {
            LinkedHashSet<T> set = new LinkedHashSet<T>(set1);
            set.removeAll(set2);
            return set;
        }
    }

    /**
     * A {@link Collection} of all the itemIds that are currently selected.
     *
     * @return a Collection of the itemIds that are currently selected
     */
    public Set<Object> getSelected() {
        return Collections.unmodifiableSet(newSelection);
    }

    /**
     * The listener interface for receiving {@link SelectionEvent
     * SelectionEvents}.
     */
    @Deprecated
    public interface SelectionListener extends Serializable {
        /**
         * Notifies the listener that the selection state has changed.
         *
         * @param event
         *            the selection change event
         */
        void select(SelectionEvent event);
    }

    /**
     * The interface for adding and removing listeners for {@link SelectionEvent
     * SelectionEvents}.
     */
    @Deprecated
    public interface SelectionNotifier extends Serializable {
        /**
         * Registers a new selection listener
         *
         * @param listener
         *            the listener to register
         */
        void addSelectionListener(SelectionListener listener);

        /**
         * Removes a previously registered selection change listener
         *
         * @param listener
         *            the listener to remove
         */
        void removeSelectionListener(SelectionListener listener);
    }
}
