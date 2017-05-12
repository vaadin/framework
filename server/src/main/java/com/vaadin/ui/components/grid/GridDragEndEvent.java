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

import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.ui.Grid;
import com.vaadin.ui.dnd.event.DragEndEvent;

/**
 * Drop event on an HTML5 drop target {@link Grid} row.
 *
 * @param <T>
 *            The Grid bean type.
 * @author Vaadin Ltd.
 * @see GridDragSource#addGridDragStartListener(GridDragStartListener)
 * @since 8.1
 */
public class GridDragEndEvent<T> extends DragEndEvent<Grid<T>> {

    private final Set<T> draggedItems;

    /**
     * Creates a drag end event.
     *
     * @param source
     *            Grid component in which the items were dragged.
     * @param dropEffect
     *            Drop effect from {@code DataTransfer.dropEffect} object.
     * @param draggedItems
     *            Set of items having been dragged.
     */
    public GridDragEndEvent(Grid<T> source, DropEffect dropEffect,
            Set<T> draggedItems) {
        super(source, dropEffect);

        this.draggedItems = draggedItems;
    }

    /**
     * Get the dragged row items.
     *
     * @return an unmodifiable set of items that were being dragged.
     */
    public Set<T> getDraggedItems() {
        return Collections.unmodifiableSet(draggedItems);
    }
}
