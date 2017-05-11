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

import java.lang.reflect.Method;

import com.vaadin.event.ConnectorEventListener;

/**
 * Drag start listener for HTML5 drag start on a Grid row.
 *
 * @param <T>
 *            The Grid bean type.
 * @author Vaadin Ltd.
 * @see GridDragSource#addGridDragStartListener(GridDragStartListener)
 * @since 8.1
 */
@FunctionalInterface
public interface GridDragStartListener<T> extends ConnectorEventListener {

    static final Method DRAG_START_METHOD = GridDragStartListener.class
            .getDeclaredMethods()[0];

    /**
     * Invoked when the user has started dragging grid's rows.
     *
     * @param event
     *            The drag start event.
     */
    void dragStart(GridDragStartEvent<T> event);
}
