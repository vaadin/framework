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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.shared.ui.grid.GridDropTargetRpc;
import com.vaadin.shared.ui.grid.GridDropTargetState;
import com.vaadin.ui.Grid;
import com.vaadin.ui.dnd.DropTargetExtension;

/**
 * Makes the rows of a Grid HTML5 drop targets. This is the server side
 * counterpart of GridDropTargetExtensionConnector.
 *
 * @param <T>
 *            Type of the Grid bean.
 * @author Vaadin Ltd
 * @since 8.1
 * @see GridRowDragger
 */
public class GridDropTarget<T> extends DropTargetExtension<Grid<T>> {

    private Registration sortListenerRegistration;
    private DropMode cachedDropMode;
    private boolean dropAllowedOnRowsWhenSorted = true;

    /**
     * Extends a Grid and makes it's rows drop targets for HTML5 drag and drop.
     *
     * @param target
     *            Grid to be extended.
     * @param dropMode
     *            Drop mode that describes the allowed drop locations within the
     *            Grid's row.
     * @see GridDropEvent#getDropLocation()
     */
    public GridDropTarget(Grid<T> target, DropMode dropMode) {
        super(target);

        setDropMode(dropMode);
    }

    /**
     * Gets the grid this extension has been attached to.
     *
     * @return the grid for this extension
     * @since 8.2
     */
    public Grid<T> getGrid() {
        return getParent();
    }

    /**
     * Sets the drop mode of this drop target.
     * <p>
     * When using {@link DropMode#ON_TOP}, and the grid is either empty or has
     * empty space after the last row, the drop can still happen on the empty
     * space, and the {@link GridDropEvent#getDropTargetRow()} will return an
     * empty optional.
     * <p>
     * When using {@link DropMode#BETWEEN} or
     * {@link DropMode#ON_TOP_OR_BETWEEN}, and there is at least one row in the
     * grid, any drop after the last row in the grid will get the last row as
     * the {@link GridDropEvent#getDropTargetRow()}. If there are no rows in the
     * grid, then it will return an empty optional.
     * <p>
     * If using {@link DropMode#ON_GRID}, then the drop will not happen on any
     * row, but instead just "on the grid". The target row will not be present
     * in this case.
     * <p>
     * <em>NOTE: {@link DropMode#ON_GRID} is used automatically when the grid
     * has been sorted and {@link #setDropAllowedOnRowsWhenSorted(boolean)} is
     * {@code false} - since the drop location would not necessarily match the
     * correct row because of the sorting. During the sorting, any calls to this
     * method don't have any effect until the sorting has been removed, or
     * {@link #setDropAllowedOnRowsWhenSorted(boolean)} is set back to
     * {@code true}.</em>
     *
     * @param dropMode
     *            Drop mode that describes the allowed drop locations within the
     *            Grid's row.
     * @see GridDropEvent#getDropLocation()
     * @see #setDropAllowedOnRowsWhenSorted(boolean)
     */
    public void setDropMode(DropMode dropMode) {
        if (dropMode == null) {
            throw new IllegalArgumentException("Drop mode cannot be null");
        }

        if (cachedDropMode != null) {
            cachedDropMode = dropMode;
        } else {
            internalSetDropMode(dropMode);
        }

    }

    private void internalSetDropMode(DropMode dropMode) {
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
     * Sets whether the grid accepts drop on rows as target when the grid has
     * been sorted by the user.
     * <p>
     * Default value is {@code true} for backwards compatibility with 8.1. When
     * {@code true} is used or the grid is not sorted, the mode used in
     * {@link #setDropMode(DropMode)} is always used.
     * <p>
     * {@code false} value means that when the grid has been sorted, the drop
     * mode is always {@link DropMode#ON_GRID}, regardless of what was set with
     * {@link #setDropMode(DropMode)}. Once the grid is not sorted anymore, the
     * sort mode is reverted back to what was set with
     * {@link #setDropMode(DropMode)}.
     *
     * @param dropAllowedOnSortedGridRows
     *            {@code true} for allowing, {@code false} for not allowing
     *            drops on sorted grid rows
     * @since 8.2
     */
    public void setDropAllowedOnRowsWhenSorted(
            boolean dropAllowedOnSortedGridRows) {
        if (this.dropAllowedOnRowsWhenSorted != dropAllowedOnSortedGridRows) {
            this.dropAllowedOnRowsWhenSorted = dropAllowedOnSortedGridRows;

            if (!dropAllowedOnSortedGridRows) {

                sortListenerRegistration = getParent()
                        .addSortListener(event -> {
                            updateDropModeForSortedGrid(
                                    !event.getSortOrder().isEmpty());
                        });

                updateDropModeForSortedGrid(
                        !getParent().getSortOrder().isEmpty());

            } else {
                // if the grid has been sorted, but now dropping on sorted grid
                // is allowed, switch back to the previously allowed drop mode
                if (cachedDropMode != null) {
                    internalSetDropMode(cachedDropMode);
                }
                sortListenerRegistration.remove();
                sortListenerRegistration = null;
                cachedDropMode = null;
            }
        }
    }

    private void updateDropModeForSortedGrid(boolean sorted) {
        if (sorted && cachedDropMode == null) {
            cachedDropMode = getDropMode();
            internalSetDropMode(DropMode.ON_GRID);
        } else if (!sorted && cachedDropMode != null) {
            internalSetDropMode(cachedDropMode);
            cachedDropMode = null;
        }
    }

    /**
     * Gets whether drops are allowed on rows as target, when the user has
     * sorted the grid.
     *
     * @return whether drop are allowed for the grid's rows when user has sorted
     *         the grid
     * @since 8.2
     */
    public boolean isDropAllowedOnRowsWhenSorted() {
        return dropAllowedOnRowsWhenSorted;
    }

    /**
     * Attaches drop listener for the current drop target.
     * {@link GridDropListener#drop(GridDropEvent)} is called when drop event
     * happens on the client side.
     *
     * @param listener
     *            Listener to handle drop event.
     * @return Handle to be used to remove this listener.
     */
    public Registration addGridDropListener(GridDropListener<T> listener) {
        return addListener(GridDropEvent.class, listener,
                GridDropListener.DROP_METHOD);
    }

    /**
     * Sets the threshold between drop locations from the top and the bottom of
     * a row in pixels.
     * <p>
     * Dropping an element
     * <ul>
     * <li>within {@code threshold} pixels from the top of a row results in a
     * drop event with {@link com.vaadin.shared.ui.grid.DropLocation#ABOVE
     * DropLocation.ABOVE}</li>
     * <li>within {@code threshold} pixels from the bottom of a row results in a
     * drop event with {@link com.vaadin.shared.ui.grid.DropLocation#BELOW
     * DropLocation.BELOW}</li>
     * <li>anywhere else within the row results in a drop event with
     * {@link com.vaadin.shared.ui.grid.DropLocation#ON_TOP
     * DropLocation.ON_TOP}</li>
     * </ul>
     * The value only has an effect when drop mode is set to
     * {@link DropMode#ON_TOP_OR_BETWEEN}.
     * <p>
     * Default is 5 pixels.
     *
     * @param threshold
     *            The threshold from the top and bottom of the row in pixels.
     */
    public void setDropThreshold(int threshold) {
        getState().dropThreshold = threshold;
    }

    /**
     * Gets the threshold between drop locations from the top and the bottom of
     * the row.
     *
     * @return The threshold in pixels.
     */
    public int getDropThreshold() {
        return getState(false).dropThreshold;
    }

    @Override
    protected void registerDropTargetRpc() {
        registerRpc((GridDropTargetRpc) (types, data, dropEffect, rowKey,
                dropLocation, mouseEventDetails) -> {

            // Create a linked map that preserves the order of types
            Map<String, String> dataPreserveOrder = new LinkedHashMap<>();
            types.forEach(type -> dataPreserveOrder.put(type, data.get(type)));

            T dropTargetRow = getParent().getDataCommunicator().getKeyMapper()
                    .get(rowKey);

            GridDropEvent<T> event = new GridDropEvent<>(getParent(),
                    dataPreserveOrder,
                    DropEffect.valueOf(dropEffect.toUpperCase(Locale.ROOT)),
                    getUI().getActiveDragSource(), dropTargetRow, dropLocation,
                    mouseEventDetails);

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

    @Override
    public void remove() {
        super.remove();

        // this handler can be removed from the grid and cannot be added to
        // another grid, thus enough to just remove the listener
        if (sortListenerRegistration != null) {
            sortListenerRegistration.remove();
            sortListenerRegistration = null;
        }
    }
}
