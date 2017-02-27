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

import java.util.Optional;

import com.vaadin.data.provider.DataGenerator;
import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.server.SerializableFunction;
import com.vaadin.shared.ui.grid.GridDragSourceExtensionState;

import elemental.json.JsonObject;

/**
 * Makes a Grid's rows draggable for HTML5 drag and drop functionality.
 *
 * @param <T>
 *         The Grid bean type.
 * @author Vaadin Ltd.
 * @since
 */
public class GridDragSourceExtension<T> extends DragSourceExtension<Grid<T>> {

    /**
     * Drag data generator that appends drag data to each row.
     */
    private DataGenerator<T> dragDataGenerator;

    /**
     * Drag data generator callback that is executed for each row.
     */
    private SerializableFunction<T, JsonObject> generatorCallback;

    /**
     * Extends a Grid and makes it's rows draggable.
     *
     * @param target
     *         Grid to be extended.
     */
    public GridDragSourceExtension(Grid<T> target) {
        super(target);

        // Create drag data generator
        dragDataGenerator = this::generateDragData;

        // Add drag data generator to Grid
        target.addDataGenerator(dragDataGenerator);
    }

    /**
     * Drag data generator. Appends drag data to row data json if generator
     * callback is set by the user of this extension.
     *
     * @param item
     *         Row item for data generation.
     * @param jsonObject
     *         Row data in json format.
     */
    private void generateDragData(T item, JsonObject jsonObject) {
        Optional.ofNullable(generatorCallback).ifPresent(callback -> jsonObject
                .put(GridDragSourceExtensionState.JSONKEY_DRAG_DATA,
                        callback.apply(item)));
    }

    /**
     * Sets a callback function for customizing drag data. The function is
     * executed for each items in the Grid during data generation. Return a
     * {@link JsonObject} to be appended to the row data.
     * <p>
     * Example:
     * <pre>
     *     dragSourceExtension.setDragDataGeneratorCallback(item -> {
     *         JsonObject dragData = Json.createObject();
     *         dragData.put("someKey", item.getValue());
     *         return dragData;
     *     });
     * </pre>
     *
     * @param callback
     *         Function to be executed on row data generation.
     */
    public void setDragDataGeneratorCallback(
            SerializableFunction<T, JsonObject> callback) {
        generatorCallback = callback;
    }


    @Override
    public void remove() {
        super.remove();

        getParent().removeDataGenerator(dragDataGenerator);
    }

    @Override
    protected GridDragSourceExtensionState getState() {
        return (GridDragSourceExtensionState) super.getState();
    }

    @Override
    protected GridDragSourceExtensionState getState(boolean markAsDirty) {
        return (GridDragSourceExtensionState) super.getState(markAsDirty);
    }
}
