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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataGenerator;
import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.event.dnd.grid.GridDragEndEvent;
import com.vaadin.event.dnd.grid.GridDragEndListener;
import com.vaadin.event.dnd.grid.GridDragStartEvent;
import com.vaadin.event.dnd.grid.GridDragStartListener;
import com.vaadin.server.SerializableFunction;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.GridDragSourceRpc;
import com.vaadin.shared.ui.grid.GridDragSourceState;

import elemental.json.JsonObject;

/**
 * Makes a Grid's rows draggable for HTML5 drag and drop functionality.
 * <p>
 * When dragging a selected row, all the visible selected rows are dragged. Note
 * that ONLY visible rows are taken into account.
 *
 * @param <T>
 *            The Grid bean type.
 * @author Vaadin Ltd.
 * @since 8.1
 */
public class GridDragSource<T> extends DragSourceExtension<Grid<T>> {

    /**
     * Drag data generator that appends drag data to each row.
     */
    private DataGenerator<T> dragDataGenerator;

    /**
     * Drag data generator function that is executed for each row.
     */
    private SerializableFunction<T, JsonObject> generatorFunction;

    /**
     * Extends a Grid and makes it's rows draggable.
     *
     * @param target
     *            Grid to be extended.
     */
    public GridDragSource(Grid<T> target) {
        super(target);

        // Create drag data generator
        dragDataGenerator = this::generateDragData;

        // Add drag data generator to Grid
        target.addDataGenerator(dragDataGenerator);
    }

    @Override
    protected void registerDragSourceRpc(Grid<T> target) {
        registerRpc(new GridDragSourceRpc() {
            @Override
            public void dragStart(List<String> draggedItemKeys) {

                GridDragStartEvent<T> event = new GridDragStartEvent<>(target,
                        getState(false).effectAllowed,
                        getDraggedItems(target, draggedItemKeys));

                fireEvent(event);
            }

            @Override
            public void dragEnd(DropEffect dropEffect,
                    List<String> draggedItemKeys) {

                GridDragEndEvent<T> event = new GridDragEndEvent<>(target,
                        dropEffect, getDraggedItems(target, draggedItemKeys));

                fireEvent(event);
            }
        });
    }

    /**
     * Collects the dragged items of a Grid given the list of item keys.
     */
    private Set<T> getDraggedItems(Grid<T> grid, List<String> draggedItemKeys) {
        if (draggedItemKeys == null || draggedItemKeys.isEmpty()) {
            throw new IllegalStateException(
                    "The drag event does not contain dragged items");
        }

        return draggedItemKeys.stream()
                .map(key -> grid.getDataCommunicator().getKeyMapper().get(key))
                .collect(Collectors.toSet());
    }

    /**
     * Drag data generator. Appends drag data to row data json if generator
     * function is set by the user of this extension.
     *
     * @param item
     *            Row item for data generation.
     * @param jsonObject
     *            Row data in json format.
     */
    private void generateDragData(T item, JsonObject jsonObject) {
        Optional.ofNullable(generatorFunction)
                .ifPresent(generator -> jsonObject.put(
                        GridDragSourceState.JSONKEY_DRAG_DATA,
                        generator.apply(item)));
    }

    /**
     * Sets a generator function for customizing drag data. The function is
     * executed for each item in the Grid during data generation. Return a
     * {@link JsonObject} to be appended to the row data.
     * <p>
     * Example:
     * 
     * <pre>
     * dragSourceExtension.setDragDataGenerator(item -> {
     *     JsonObject dragData = Json.createObject();
     *     dragData.put("someKey", item.getValue());
     *     return dragData;
     * });
     * </pre>
     *
     * @param generator
     *            Function to be executed on row data generation.
     */
    public void setDragDataGenerator(
            SerializableFunction<T, JsonObject> generator) {
        generatorFunction = generator;
    }

    /**
     * Setting the data transfer text for this drag source is not supported.
     *
     * @throws UnsupportedOperationException
     *             Setting dataTransferText is not supported, since the drag
     *             data is set for each row based on the data provided by the
     *             generator.
     * @see #setDragDataGenerator(SerializableFunction)
     */
    @Override
    public void setDataTransferText(String data)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Setting dataTransferText is not supported");
    }

    /**
     * Attaches dragstart listener for the current drag source grid.
     *
     * @param listener
     *            Listener to handle the dragstart event.
     * @return Handle to be used to remove this listener.
     * @see GridDragStartEvent
     */
    public Registration addGridDragStartListener(
            GridDragStartListener<T> listener) {
        return addListener(DragSourceState.EVENT_DRAGSTART,
                GridDragStartEvent.class, listener,
                GridDragStartListener.DRAG_START_METHOD);
    }

    /**
     * Attaches dragend listener for the current drag source grid.
     *
     * @param listener
     *            Listener to handle the dragend event.
     * @return Handle to be used to remove this listener.
     * @see GridDragEndEvent
     */
    public Registration addGridDragEndListener(
            GridDragEndListener<T> listener) {
        return addListener(DragSourceState.EVENT_DRAGEND,
                GridDragEndEvent.class, listener,
                GridDragEndListener.DRAG_END_METHOD);
    }

    /**
     * Returns the generator function for customizing drag data.
     *
     * @return Drag data generator function.
     */
    public SerializableFunction<T, JsonObject> getDragDataGenerator() {
        return generatorFunction;
    }

    @Override
    public void remove() {
        super.remove();

        getParent().removeDataGenerator(dragDataGenerator);
    }

    @Override
    protected GridDragSourceState getState() {
        return (GridDragSourceState) super.getState();
    }

    @Override
    protected GridDragSourceState getState(boolean markAsDirty) {
        return (GridDragSourceState) super.getState(markAsDirty);
    }
}
