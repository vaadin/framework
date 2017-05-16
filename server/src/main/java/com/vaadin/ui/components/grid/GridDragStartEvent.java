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
package com.vaadin.ui.components.grid;

import java.util.Collections;
import java.util.Set;

import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.Grid;
import com.vaadin.ui.dnd.event.DragStartEvent;

/**
 * Drag start event on an HTML5 drag source {@link Grid} row.
 *
 * @param <T>
 *            The Grid bean type.
 * @author Vaadin Ltd.
 * @see GridDragSource#addGridDragStartListener(GridDragStartListener)
 * @since 8.1
 */
public class GridDragStartEvent<T> extends DragStartEvent<Grid<T>> {

    private final Set<T> draggedItems;

    /**
     * Creates a drag start event.
     *
     * @param source
     *            The source grid where the rows are being dragged from.
     * @param effectAllowed
     *            Allowed effect from {@code DataTransfer.effectAllowed} object.
     * @param draggedItems
     *            Set of items being dragged.
     */
    public GridDragStartEvent(Grid<T> source, EffectAllowed effectAllowed,
            Set<T> draggedItems) {
        super(source, effectAllowed);

        this.draggedItems = draggedItems;
    }

    /**
     * Get the dragged row items.
     *
     * @return an unmodifiable set of items that are being dragged.
     */
    public Set<T> getDraggedItems() {
        return Collections.unmodifiableSet(draggedItems);
    }
}
