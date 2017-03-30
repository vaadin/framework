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

import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * HTML5 drag end event.
 *
 * @param <T>
 *         Type of the component that was dragged.
 * @author Vaadin Ltd
 * @see DragSourceExtension#addDragEndListener(DragEndListener)
 * @since 8.1
 */
public class DragEndEvent<T extends AbstractComponent> extends Component.Event {
    private final DropEffect dropEffect;

    /**
     * Creates a drag end event.
     *
     * @param source
     *         Component that was dragged.
     * @param dropEffect
     *         Drop effect from {@code DataTransfer.dropEffect} object.
     */
    public DragEndEvent(T source, DropEffect dropEffect) {
        super(source);

        this.dropEffect = dropEffect;
    }

    /**
     * Get drop effect of the dragend event.
     *
     * @return The {@code DataTransfer.dropEffect} parameter of the client side
     * dragend event.
     */
    public DropEffect getDropEffect() {
        return dropEffect;
    }

    /**
     * Returns the drag source component where the dragend event occurred.
     *
     * @return Component which was dragged.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getComponent() {
        return (T) super.getComponent();
    }
}
