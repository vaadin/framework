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
package com.vaadin.ui;

import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.event.dnd.grid.GridDropEvent;
import com.vaadin.event.dnd.grid.GridDropListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.shared.ui.grid.GridDropTargetRpc;
import com.vaadin.shared.ui.grid.GridDropTargetState;

/**
 * Makes the rows of a Grid HTML5 drop targets. This is the server side
 * counterpart of GridDropTargetExtensionConnector.
 *
 * @param <T>
 *         Type of the Grid bean.
 * @author Vaadin Ltd
 * @since
 */
public class GridDropTarget<T> extends DropTargetExtension<Grid<T>> {

    /**
     * Extends a Grid and makes it's rows drop targets for HTML5 drag and drop.
     *
     * @param target
     *         Grid to be extended.
     * @param dropMode
     *         Drop mode that describes the allowed drop locations within the
     *         Grid's row.
     * @see GridDropEvent#getDropLocation()
     */
    public GridDropTarget(Grid<T> target, DropMode dropMode) {
        super(target);

        setDropMode(dropMode);
    }

    /**
     * Sets the drop mode of this drop target.
     *
     * @param dropMode
     *         Drop mode that describes the allowed drop locations within the
     *         Grid's row.
     * @see GridDropEvent#getDropLocation()
     */
    public void setDropMode(DropMode dropMode) {
        if (dropMode == null) {
            throw new IllegalArgumentException("Drop mode cannot be null");
        }

        getState().dropMode = dropMode;
    }

    /**
     * Gets the drop mode of this drop target.
     *
     * @return Drop mode that describes the allowed drop locations within the
     *         Grid's row.
     */
    public DropMode getDropMode() {
        return getState(false).dropMode;
    }

    /**
     * Attaches drop listener for the current drop target. {@link
     * GridDropListener#drop(GridDropEvent)} is called when drop event happens
     * on the client side.
     *
     * @param listener
     *         Listener to handle drop event.
     * @return Handle to be used to remove this listener.
     */
    public Registration addGridDropListener(GridDropListener<T> listener) {
        return addListener(GridDropEvent.class, listener,
                GridDropListener.DROP_METHOD);
    }

    @Override
    protected void registerDropTargetRpc(Grid<T> target) {
        registerRpc(
                (GridDropTargetRpc) (dataTransferText, rowKey, dropLocation) -> {

                    T dropTargetRow = target.getDataCommunicator()
                            .getKeyMapper().get(rowKey);

                    GridDropEvent<T> event = new GridDropEvent<>(target,
                            dataTransferText, getUI().getActiveDragSource(),
                            dropTargetRow, dropLocation);

                    fireEvent(event);
                });
    }

    @Override
    protected GridDropTargetState getState() {
        return (GridDropTargetState) super.getState();
    }

    @Override
    protected GridDropTargetState getState(boolean markAsDirty) {
        return (GridDropTargetState) super.getState(markAsDirty);
    }
}
