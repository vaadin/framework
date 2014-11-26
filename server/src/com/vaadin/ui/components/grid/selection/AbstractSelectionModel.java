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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.vaadin.ui.Grid;

/**
 * A base class for SelectionModels that contains some of the logic that is
 * reusable.
 * 
 * @since
 * @author Vaadin Ltd
 */
public abstract class AbstractSelectionModel implements SelectionModel {
    protected final LinkedHashSet<Object> selection = new LinkedHashSet<Object>();
    protected Grid grid = null;

    @Override
    public boolean isSelected(final Object itemId) {
        return selection.contains(itemId);
    }

    @Override
    public Collection<Object> getSelectedRows() {
        return new ArrayList<Object>(selection);
    }

    @Override
    public void setGrid(final Grid grid) {
        this.grid = grid;
    }

    /**
     * Fires a {@link SelectionChangeEvent} to all the
     * {@link SelectionChangeListener SelectionChangeListeners} currently added
     * to the Grid in which this SelectionModel is.
     * <p>
     * Note that this is only a helper method, and routes the call all the way
     * to Grid. A {@link SelectionModel} is not a
     * {@link SelectionChangeNotifier}
     * 
     * @param oldSelection
     *            the complete {@link Collection} of the itemIds that were
     *            selected <em>before</em> this event happened
     * @param newSelection
     *            the complete {@link Collection} of the itemIds that are
     *            selected <em>after</em> this event happened
     */
    protected void fireSelectionChangeEvent(
            final Collection<Object> oldSelection,
            final Collection<Object> newSelection) {
        grid.fireSelectionChangeEvent(oldSelection, newSelection);
    }
}
