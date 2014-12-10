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

import java.util.Collection;
import java.util.Collections;

/**
 * A default implementation of a {@link SelectionModel.Single}
 * 
 * @since
 * @author Vaadin Ltd
 */
public class SingleSelectionModel extends AbstractSelectionModel implements
        SelectionModel.Single {
    @Override
    public boolean select(final Object itemId) {
        if (itemId == null) {
            return deselect(getSelectedRow());
        }

        checkItemIdExists(itemId);

        final Object selectedRow = getSelectedRow();
        final boolean modified = selection.add(itemId);
        if (modified) {
            final Collection<Object> deselected;
            if (selectedRow != null) {
                deselectInternal(selectedRow, false);
                deselected = Collections.singleton(selectedRow);
            } else {
                deselected = Collections.emptySet();
            }

            fireSelectionChangeEvent(deselected, selection);
        }

        return modified;
    }

    private boolean deselect(final Object itemId) {
        return deselectInternal(itemId, true);
    }

    private boolean deselectInternal(final Object itemId,
            boolean fireEventIfNeeded) {
        final boolean modified = selection.remove(itemId);
        if (fireEventIfNeeded && modified) {
            fireSelectionChangeEvent(Collections.singleton(itemId),
                    Collections.emptySet());
        }
        return modified;
    }

    @Override
    public Object getSelectedRow() {
        if (selection.isEmpty()) {
            return null;
        } else {
            return selection.iterator().next();
        }
    }

    /**
     * Resets the selection state.
     * <p>
     * If an item is selected, it will become deselected.
     */
    @Override
    public void reset() {
        deselect(getSelectedRow());
    }
}
