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

import java.util.List;
import java.util.Map;

import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.event.dnd.DropEvent;
import com.vaadin.event.dnd.DropListener;
import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Grid;

/**
 * Server side drop event on an HTML5 drop target {@link Grid} row.
 *
 * @param <T>
 *         The Grid bean type.
 * @author Vaadin Ltd.
 * @see com.vaadin.ui.GridDropTargetExtension#addDropListener(GridDropListener)
 * @since
 */
public class GridDropEvent<T> extends DropEvent<Grid<T>> {

    private final T dropTargetRow;

    /**
     * Creates a server side Grid row drop event.
     *
     * @param target
     *         Grid that received the drop.
     * @param types
     *         List of data types from {@code DataTransfer.types} object.
     * @param data
     *         Map containing all types and corresponding data from the {@code
     *         DataTransfer} object.
     * @param dropEffect
     *         Drop effect from {@code DataTransfer.dropEffect} object.
     * @param dragSourceExtension
     *         Drag source extension of the component that initiated the drop
     *         event.
     * @param dropTargetRowKey
     *         Key of the target row that received the drop.
     */
    public GridDropEvent(Grid<T> target, List<String> types,
            Map<String, String> data, DropEffect dropEffect,
            DragSourceExtension<AbstractComponent> dragSourceExtension,
            String dropTargetRowKey) {
        super(target, types, data, dropEffect, dragSourceExtension);

        dropTargetRow = target.getDataCommunicator().getKeyMapper()
                .get(dropTargetRowKey);
    }

    /**
     * Get the row item source of this event.
     *
     * @return The row item this event was originated from.
     */
    public T getDropTargetRow() {
        return dropTargetRow;
    }
}
