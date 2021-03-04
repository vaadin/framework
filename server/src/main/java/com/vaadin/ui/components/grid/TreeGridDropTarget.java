/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.shared.ui.treegrid.TreeGridDropTargetRpc;
import com.vaadin.shared.ui.treegrid.TreeGridDropTargetState;
import com.vaadin.ui.TreeGrid;

/**
 * Makes the rows of a TreeGrid HTML5 drop targets. This is the server side
 * counterpart of GridDropTargetExtensionConnector.
 *
 * @param <T>
 *            Type of the TreeGrid bean.
 * @author Vaadin Ltd
 * @since 8.1
 */
public class TreeGridDropTarget<T> extends GridDropTarget<T> {

    /**
     * Extends a TreeGrid and makes it's rows drop targets for HTML5 drag and
     * drop.
     *
     * @param target
     *            TreeGrid to be extended.
     * @param dropMode
     *            Drop mode that describes the allowed drop locations within the
     *            TreeGrid's row.
     */
    public TreeGridDropTarget(TreeGrid<T> target, DropMode dropMode) {
        super(target, dropMode);
    }

    /**
     * Attaches drop listener for the current drop target.
     * {@link TreeGridDropListener#drop(TreeGridDropEvent)} is called when drop
     * event happens on the client side.
     *
     * @param listener
     *            Listener to handle drop event.
     * @return Handle to be used to remove this listener.
     */
    public Registration addTreeGridDropListener(
            TreeGridDropListener<T> listener) {
        return addListener(TreeGridDropEvent.class, listener,
                TreeGridDropListener.DROP_METHOD);
    }

    @Override
    protected void registerDropTargetRpc() {
        registerRpc((TreeGridDropTargetRpc) (types, data, dropEffect, rowKey,
                depth, collapsed, dropLocation, mouseEventDetails) -> {

            // Create a linked map that preserves the order of types
            Map<String, String> dataPreserveOrder = new LinkedHashMap<>();
            types.forEach(type -> dataPreserveOrder.put(type, data.get(type)));

            T dropTargetRow = getParent().getDataCommunicator().getKeyMapper()
                    .get(rowKey);

            TreeGridDropEvent<T> event = new TreeGridDropEvent<>(getParent(),
                    dataPreserveOrder,
                    DropEffect.valueOf(dropEffect.toUpperCase(Locale.ROOT)),
                    getUI().getActiveDragSource(), dropTargetRow, dropLocation,
                    mouseEventDetails, depth, collapsed);

            fireEvent(event);
        });
    }

    @Override
    public TreeGrid<T> getParent() {
        return (TreeGrid<T>) super.getParent();
    }

    @Override
    protected TreeGridDropTargetState getState() {
        return (TreeGridDropTargetState) super.getState();
    }

    @Override
    protected TreeGridDropTargetState getState(boolean markAsDirty) {
        return (TreeGridDropTargetState) super.getState(markAsDirty);
    }
}
