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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataGenerator;
import com.vaadin.server.SerializableFunction;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.GridDragSourceRpc;
import com.vaadin.shared.ui.grid.GridDragSourceState;
import com.vaadin.ui.Grid;
import com.vaadin.ui.dnd.DragSourceExtension;

import elemental.json.Json;
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
     * Collection of drag data generator functions. Functions are executed for
     * each row and results are stored under their corresponding key.
     */
    private final Map<String, SerializableFunction<T, String>> generatorFunctions;

    /**
     * Default drag data generator for Grid. It creates a list of row values
     * separated by a tabulator character ({@code \t}).
     * <pre>
     *      "column1_value\tcolumn2_value\t ... columnN_value"
     * </pre>
     */
    private final SerializableFunction<T, String> defaultGridGenerator = item -> {
        StringBuilder generatedValue = new StringBuilder();
        getParent().getColumns().forEach(column -> {
            generatedValue.append("\t");    // Tab separated values
            generatedValue.append(column.getValueProvider().apply(item));
        });
        return generatedValue.substring(1);
    };

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
        target.getDataCommunicator().addDataGenerator(dragDataGenerator);

        generatorFunctions = new HashMap<>();

        // Set default generator function for "text" parameter
        generatorFunctions
                .put(DragSourceState.DATA_TYPE_TEXT, defaultGridGenerator);
    }

    @Override
    protected void registerDragSourceRpc() {
        registerRpc(new GridDragSourceRpc() {
            @Override
            public void dragStart(List<String> draggedItemKeys) {

                GridDragStartEvent<T> event = new GridDragStartEvent<>(
                        getParent(), getState(false).effectAllowed,
                        getDraggedItems(getParent(), draggedItemKeys));

                fireEvent(event);
            }

            @Override
            public void dragEnd(DropEffect dropEffect,
                    List<String> draggedItemKeys) {

                GridDragEndEvent<T> event = new GridDragEndEvent<>(getParent(),
                        dropEffect,
                        getDraggedItems(getParent(), draggedItemKeys));

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
     * function(s) are set by the user of this extension.
     *
     * @param item
     *            Row item for data generation.
     * @param jsonObject
     *            Row data in json format.
     */
    private void generateDragData(T item, JsonObject jsonObject) {
        JsonObject generatedValues = Json.createObject();

        generatorFunctions.forEach((type, generator) -> {
            generatedValues.put(type, generator.apply(item));
        });

        jsonObject.put(GridDragSourceState.JSONKEY_DRAG_DATA, generatedValues);
    }

    /**
     * Sets a generator function for customizing drag data. The generated value
     * will be accessible using the same {@code type} as the generator is set
     * here. The function is executed for each item in the Grid during data
     * generation. Return a {@link String} to be appended to the row as {@code
     * type} data.
     * <p>
     * Example, building a JSON object that contains the item's values:
     * <pre>
     *     dragSourceExtension.setDragDataGenerator("application/json", item ->
     * {
     *         StringBuilder builder = new StringBuilder();
     *         builder.append("{");
     *         getParent().getColumns().forEach(column -> {
     *             builder.append("\"" + column.getCaption() + "\"");
     *             builder.append(":");
     *             builder.append("\"" + column.getValueProvider().apply(item) + "\"");
     *             builder.append(",");
     *         });
     *         builder.setLength(builder.length() - 1); // Remove last comma
     *         builder.append("}");
     *         return builder.toString();
     *     }
     * </pre>
     *
     * @param type
     *         Type of the generated data. The generated value will be
     *         accessible during drop using this type.
     * @param generator
     *         Function to be executed on row data generation.
     */
    public void setDragDataGenerator(String type, SerializableFunction<T, String> generator) {
        generatorFunctions.put(type, generator);
    }

    /**
     * Remove the generator function set for the given type.
     *
     * @param type
     *         Type of the generator to be removed.
     */
    public void clearDragDataGenerator(String type) {
        generatorFunctions.remove(type);
    }

    /**
     * Returns the drag data generator function for the given type.
     *
     * @param type
     *         Type of the generated data.
     * @return Drag data generator function for the given type.
     */
    public SerializableFunction<T, String> getDragDataGenerator(String type) {
        return generatorFunctions.get(type);
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

    @Override
    public void remove() {
        super.remove();

        getParent().getDataCommunicator()
                .removeDataGenerator(dragDataGenerator);
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
