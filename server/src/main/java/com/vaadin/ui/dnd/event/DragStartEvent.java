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
package com.vaadin.ui.dnd.event;

import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.dnd.DragSourceExtension;

/**
 * HTML5 drag start event.
 *
 * @param <T>
 *         Type of the component that is dragged.
 * @author Vaadin Ltd
 * @see DragSourceExtension#addDragStartListener(DragStartListener)
 * @since 8.1
 */
public class DragStartEvent<T extends AbstractComponent> extends
        Component.Event {
    private final EffectAllowed effectAllowed;

    /**
     * Creates a drag start event.
     *
     * @param source
     *         Component that is dragged.
     * @param effectAllowed
     *         Allowed effects from {@code DataTransfer.effectAllowed} object.
     */
    public DragStartEvent(T source, EffectAllowed effectAllowed) {
        super(source);

        this.effectAllowed = effectAllowed;
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
