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

import java.util.Map;
import java.util.Optional;

import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.event.DropEvent;

/**
 * Drop event on an HTML5 drop target {@link Grid} row.
 *
 * @param <T>
 *            The Grid bean type.
 * @author Vaadin Ltd.
 * @see GridDropTarget#addGridDropListener(GridDropListener)
 * @since 8.1
 */
public class GridDropEvent<T> extends DropEvent<Grid<T>> {

    private final T dropTargetRow;
    private final DropLocation dropLocation;

    /**
     * Creates a Grid row drop event.
     *
     * @param target
     *            Grid that received the drop.
     * @param data
     *            Map containing all types and corresponding data from the
     *            {@code
     *         DataTransfer} object.
     * @param dropEffect
     *            the desired drop effect
     * @param dragSourceExtension
     *            Drag source extension of the component that initiated the drop
     *            event.
     * @param dropTargetRow
     *            Target row that received the drop, or {@code null} if dropped
     *            on empty grid
     * @param dropLocation
     *            Location of the drop within the target row.
     */
    public GridDropEvent(Grid<T> target, Map<String, String> data,
            DropEffect dropEffect,
            DragSourceExtension<? extends AbstractComponent> dragSourceExtension,
            T dropTargetRow, DropLocation dropLocation) {
        super(target, data, dropEffect, dragSourceExtension);

        this.dropTargetRow = dropTargetRow;
        this.dropLocation = dropLocation;
    }

    /**
     * Get the row the drop happened on.
     * <p>
     * If the drop was not on top of a row (see {@link #getDropLocation()}),
     * then returns an empty optional.
     *
     * @return The row the drop happened on, or an empty optional if dropped on
     *         the in grid but not on top of any row, like to an empty grid
     */
    public Optional<T> getDropTargetRow() {
        return Optional.ofNullable(dropTargetRow);
    }

    /**
     * Get the location of the drop within the row.
     * <p>
     * <em>NOTE: when dropped on an empty grid, or when {@link DropMode#ON_TOP}
     * is used and the drop happened on empty space after last row or on top of
     * the header / footer, the location will be
     * {@link DropLocation#EMPTY}.</em>
     *
     * @return Location of the drop within the row.
     * @see GridDropTarget#setDropMode(DropMode)
     */
    public DropLocation getDropLocation() {
        return dropLocation;
    }
}
