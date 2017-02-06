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

import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.Component;

/**
 * Server side dragstart event. Fired when an HTML5 dragstart happens on the
 * client.
 *
 * @param <T>
 *         Type of the component that is dragged.
 * @see DragSourceExtension#addDragStartListener(DragStartListener)
 */
public class DragStartEvent<T extends Component> extends Component.Event {
    private final Map<String, String> data;
    private final EffectAllowed effectAllowed;

    /**
     * Creates a server side dragstart event.
     *
     * @param source
     *         Component that is dragged.
     * @param types
     *         List of data types from {@code DataTransfer.types} object.
     * @param data
     *         Map containing all types and corresponding data from the {@code
     *         DataTransfer} object.
     * @param effectAllowed
     *         Allowed effects from {@code DataTransfer.effectAllowed} object.
     */
    public DragStartEvent(T source, List<String> types,
            Map<String, String> data, EffectAllowed effectAllowed) {
        super(source);

        // Create a linked map that preserves the order of types
        this.data = new LinkedHashMap<>();
        types.forEach(type -> this.data.put(type, data.get(type)));

        this.effectAllowed = effectAllowed;
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
     * Returns the {@code effectAllowed} parameter of this event.
     *
     * @return This event's {@code effectAllowed} parameter.
     */
    public EffectAllowed getEffectAllowed() {
        return effectAllowed;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getComponent() {
        return (T) super.getComponent();
    }
}
