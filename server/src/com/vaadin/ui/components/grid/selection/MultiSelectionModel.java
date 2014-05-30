/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.ui.components.grid.selection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.vaadin.data.Container.Indexed;

/**
 * A default implementation of a {@link SelectionModel.Multi}
 * 
 * @since 7.4.0
 * @author Vaadin Ltd
 */
public class MultiSelectionModel extends AbstractSelectionModel implements
        SelectionModel.Multi {

    @Override
    public boolean select(final Object... itemIds)
            throws IllegalArgumentException {
        if (itemIds != null) {
            // select will fire the event
            return select(Arrays.asList(itemIds));
        } else {
            throw new IllegalArgumentException(
                    "Vararg array of itemIds may not be null");
        }
    }

    @Override
    public boolean select(final Collection<?> itemIds)
            throws IllegalArgumentException {
        if (itemIds == null) {
            throw new IllegalArgumentException("itemIds may not be null");
        }

        final boolean hasSomeDifferingElements = !selection
                .containsAll(itemIds);
        if (hasSomeDifferingElements) {
            final HashSet<Object> oldSelection = new HashSet<Object>(selection);
            selection.addAll(itemIds);
            fireSelectionChangeEvent(oldSelection, selection);
        }
        return hasSomeDifferingElements;
    }

    @Override
    public boolean deselect(final Object... itemIds)
            throws IllegalArgumentException {
        if (itemIds != null) {
            // deselect will fire the event
            return deselect(Arrays.asList(itemIds));
        } else {
            throw new IllegalArgumentException(
                    "Vararg array of itemIds may not be null");
        }
    }

    @Override
    public boolean deselect(final Collection<?> itemIds)
            throws IllegalArgumentException {
        if (itemIds == null) {
            throw new IllegalArgumentException("itemIds may not be null");
        }

        final boolean hasCommonElements = !Collections.disjoint(itemIds,
                selection);
        if (hasCommonElements) {
            final HashSet<Object> oldSelection = new HashSet<Object>(selection);
            selection.removeAll(itemIds);
            fireSelectionChangeEvent(oldSelection, selection);
        }
        return hasCommonElements;
    }

    @Override
    public boolean selectAll() {
        // select will fire the event
        final Indexed container = grid.getContainerDatasource();
        if (container != null) {
            return select(container.getItemIds());
        } else if (selection.isEmpty()) {
            return false;
        } else {
            /*
             * this should never happen (no container but has a selection), but
             * I guess the only theoretically correct course of action...
             */
            return deselectAll();
        }
    }

    @Override
    public boolean deselectAll() {
        // deselect will fire the event
        return deselect(getSelectedRows());
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned Collection is in <strong>order of selection</strong> &ndash;
     * the item that was first selected will be first in the collection, and so
     * on. Should an item have been selected twice without being deselected in
     * between, it will have remained in its original position.
     */
    @Override
    public Collection<Object> getSelectedRows() {
        // overridden only for JavaDoc
        return super.getSelectedRows();
    }

    /**
     * Resets the selection model.
     * <p>
     * Equivalent to calling {@link #deselectAll()}
     */
    @Override
    public void reset() {
        deselectAll();
    }
}
