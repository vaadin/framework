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
package com.vaadin.event.dnd.grid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.event.dnd.DragStartEvent;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.Grid;

/**
 * Server side drop event on an HTML5 drop target {@link Grid} row.
 *
 * @param <T>
 *         The Grid bean type.
 * @author Vaadin Ltd.
 * @see com.vaadin.ui.GridDragSourceExtension#addGridDragStartListener(GridDragStartListener)
 * @since
 */
public class GridDragStartEvent<T> extends DragStartEvent<Grid<T>> {

    private final Set<T> draggedItems;

    /**
     * Creates a server side drag start event.
     *
     * @param source
     *         Grid component in which the items are dragged.
     * @param dataTransferText
     *         Data of type {@code "text"} from the {@code DataTransfer}
     *         object.
     * @param effectAllowed
     *         Allowed effect from {@code DataTransfer.effectAllowed} object.
     * @param draggedItemKeys
     *         Keys of the items being dragged.
     */
    public GridDragStartEvent(Grid<T> source, String dataTransferText,
            EffectAllowed effectAllowed, List<String> draggedItemKeys) {
        super(source, dataTransferText, effectAllowed);

        draggedItems = new HashSet<>();
        draggedItemKeys.forEach(key -> draggedItems
                .add(source.getDataCommunicator().getKeyMapper().get(key)));
    }

    /**
     * Get the dragged row items.
     *
     * @return Set of row items that are being dragged.
     */
    public Set<T> getDraggedItems() {
        return draggedItems;
    }
}
