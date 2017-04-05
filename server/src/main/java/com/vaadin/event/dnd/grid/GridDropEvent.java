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

import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.event.dnd.DropEvent;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Grid;

/**
 * Drop event on an HTML5 drop target {@link Grid} row.
 *
 * @param <T>
 *         The Grid bean type.
 * @author Vaadin Ltd.
 * @see com.vaadin.ui.GridDropTargetExtension#addGridDropListener(GridDropListener)
 * @since
 */
public class GridDropEvent<T> extends DropEvent<Grid<T>> {

    private final T dropTargetRow;
    private final DropLocation dropLocation;

    /**
     * Creates a Grid row drop event.
     *
     * @param target
     *         Grid that received the drop.
     * @param dataTransferText
     *         Data of type {@code "text"} from the {@code DataTransfer}
     *         object.
     * @param dragSourceExtension
     *         Drag source extension of the component that initiated the drop
     *         event.
     * @param dropTargetRow
     *         Target row that received the drop.
     * @param dropLocation
     *         Location of the drop within the target row.
     */
    public GridDropEvent(Grid<T> target, String dataTransferText,
            DragSourceExtension<? extends AbstractComponent> dragSourceExtension,
            T dropTargetRow, DropLocation dropLocation) {
        super(target, dataTransferText, dragSourceExtension);

        this.dropTargetRow = dropTargetRow;
        this.dropLocation = dropLocation;
    }

    /**
     * Get the row item source of this event.
     *
     * @return The row item this event was originated from.
     */
    public T getDropTargetRow() {
        return dropTargetRow;
    }

    /**
     * Get the location of the drop within the row.
     *
     * @return Location of the drop within the row.
     */
    public DropLocation getDropLocation() {
        return dropLocation;
    }
}
