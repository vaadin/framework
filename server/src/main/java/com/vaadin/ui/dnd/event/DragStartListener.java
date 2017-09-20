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

import java.lang.reflect.Method;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.dnd.DragSourceExtension;

/**
 * Interface to be implemented when creating a dragstart listener on a drag
 * source for HTML5 drag and drop.
 *
 * @param <T>
 *         Type of draggable component.
 * @author Vaadin Ltd
 * @see DragSourceExtension#addDragStartListener(DragStartListener)
 * @since 8.1
 */
@FunctionalInterface
public interface DragStartListener<T extends AbstractComponent> extends
        ConnectorEventListener {
    static final Method DRAGSTART_METHOD = DragStartListener.class
            .getDeclaredMethods()[0];

    /**
     * Called when dragstart event is fired.
     *
     * @param event
     *         Server side dragstart event.
     */
    void dragStart(DragStartEvent<T> event);
}
