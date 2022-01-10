/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.dnd.DragSourceExtension;

/**
 * Drop event on an HTML5 drop target {@link TreeGrid} row.
 *
 * @param <T>
 *            The TreeGrid bean type.
 * @author Vaadin Ltd.
 * @see TreeGridDropTarget#addTreeGridDropListener(TreeGridDropListener)
 * @since 8.1
 */
public class TreeGridDropEvent<T> extends GridDropEvent<T> {

    private Integer depth;
    private Boolean collapsed;

    /**
     * Creates a TreeGrid row drop event.
     *
     * @param target
     *            TreeGrid that received the drop.
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
     * @param mouseEventDetails
     *            Mouse event details object containing information about the
     *            drop event
     * @param depth
     *            depth of the row in the hierarchy
     * @param collapsed
     *            whether the target row is collapsed
     */
    public TreeGridDropEvent(TreeGrid<T> target, Map<String, String> data,
            DropEffect dropEffect,
            DragSourceExtension<? extends AbstractComponent> dragSourceExtension,
            T dropTargetRow, DropLocation dropLocation,
            MouseEventDetails mouseEventDetails, Integer depth,
            Boolean collapsed) {
        super(target, data, dropEffect, dragSourceExtension, dropTargetRow,
                dropLocation, mouseEventDetails);

        this.depth = depth;
        this.collapsed = collapsed;
    }

    /**
     * Gets the depth of the drop target row in the hierarchy.
     *
     * @return the depth of the drop target row in the hierarchy
     */
    public Optional<Integer> getDropTargetRowDepth() {
        return Optional.ofNullable(depth);
    }

    /**
     * Tells whether the drop target row is collapsed.
     *
     * @return {@code true} if the drop target row is collapsed, {@code false}
     *         otherwise
     */
    public Optional<Boolean> isDropTargetRowCollapsed() {
        return Optional.ofNullable(collapsed);
    }
}
