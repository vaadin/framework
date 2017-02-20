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
package com.vaadin.event.dnd;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * Server side drop event. Fired when an HTML5 drop happens.
 *
 * @param <T>
 *         Type of the drop target component.
 * @see DropTargetExtension#addDropListener(DropListener)
 */
public class DropEvent<T extends AbstractComponent> extends Component.Event {
    private final Map<String, String> data;
    private final DropEffect dropEffect;
    private final DragSourceExtension<T> dragSource;

    /**
     * Creates a server side drop event.
     *
     * @param source
     *         Component that is dragged.
     * @param types
     *         List of data types from {@code DataTransfer.types} object.
     * @param data
     *         Map containing all types and corresponding data from the {@code
     *         DataTransfer} object.
     * @param dropEffect
     *         Drop effect from {@code DataTransfer.dropEffect} object.
     * @param dragSource
     *         Drag source extension of the component that initiated the drop
     *         event.
     */
    public DropEvent(T source, List<String> types, Map<String, String> data,
            DropEffect dropEffect, DragSourceExtension<T> dragSource) {
        super(source);

        // Create a linked map that preserves the order of types
        this.data = new LinkedHashMap<>();
        types.forEach(type -> this.data.put(type, data.get(type)));

        this.dropEffect = dropEffect;

        this.dragSource = dragSource;
    }

    /**
     * Get data from the client side {@code DataTransfer} object.
     *
     * @param format
     *         Data format, e.g. {@code text/plain} or {@code text/uri-list}.
     * @return Data for the given format if exists in the client side {@code
     * DataTransfer}, otherwise {@code null}.
     */
    public String getTransferData(String format) {
        return data != null ? data.get(format) : null;
    }

    /**
     * Get drop effect set for the current drop target.
     *
     * @return {@code dropEffect} parameter set for the current drop target.
     */
    public DropEffect getDropEffect() {
        return dropEffect;
    }

    /**
     * Get the drag source component that has just been dropped if the drag
     * source and drop target are in the same UI.
     *
     * @return Dropped drag source component.
     */
    public Optional<T> getDragSourceComponent() {
        return getDragSourceExtension().map(DragSourceExtension::getParent);
    }

    /**
     * Get the drag source extension of the component that has just been dropped
     * if the drag source and drop target are in the same UI.
     *
     * @return Drag source extension of the dropped component.
     */
    public Optional<DragSourceExtension<T>> getDragSourceExtension() {
        return Optional.ofNullable(dragSource);
    }

    /**
     * Returns the drop target component where the drop event occurred.
     *
     * @return Component on which a drag source was dropped.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getComponent() {
        return (T) super.getComponent();
    }
}
