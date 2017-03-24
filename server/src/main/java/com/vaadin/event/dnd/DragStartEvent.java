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

import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * Server side dragstart event. Fired when an HTML5 dragstart happens on the
 * client.
 *
 * @param <T>
 *         Type of the component that is dragged.
 * @author Vaadin Ltd
 * @see DragSourceExtension#addDragStartListener(DragStartListener)
 * @since 8.1
 */
public class DragStartEvent<T extends AbstractComponent> extends
        Component.Event {
    private final String dataTransferText;
    private final EffectAllowed effectAllowed;

    /**
     * Creates a server side dragstart event.
     *
     * @param source
     *         Component that is dragged.
     * @param dataTransferText
     *         Data of type {@code "text"} from the {@code DataTransfer}
     *         object.
     * @param effectAllowed
     *         Allowed effects from {@code DataTransfer.effectAllowed} object.
     */
    public DragStartEvent(T source, String dataTransferText,
            EffectAllowed effectAllowed) {
        super(source);

        this.dataTransferText = dataTransferText;

        this.effectAllowed = effectAllowed;
    }

    /**
     * Get data of type {@code "text"} from the client side {@code DataTransfer}
     * object.
     *
     * @return Data of type {@code "text"} if exists in the client side {@code
     * DataTransfer} object, otherwise {@literal null}.
     */
    public String getDataTransferText() {
        return dataTransferText;
    }

    /**
     * Returns the {@code effectAllowed} parameter of this event.
     *
     * @return This event's {@code effectAllowed} parameter.
     */
    public EffectAllowed getEffectAllowed() {
        return effectAllowed;
    }

    /**
     * Returns the drag source component where the dragstart event occurred.
     *
     * @return Component which is dragged.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getComponent() {
        return (T) super.getComponent();
    }
}
