/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.widget.grid.events;

import com.google.gwt.event.shared.EventHandler;
import com.vaadin.client.widgets.Grid.AbstractGridKeyEvent;

/**
 * Base interface of all handlers for {@link AbstractGridKeyEvent}s.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public abstract interface AbstractGridKeyEventHandler extends EventHandler {

    /**
     * Handler for Grid key down events.
     */
    public abstract interface GridKeyDownHandler
            extends AbstractGridKeyEventHandler {
        /**
         * Perform actions that should happen when a key down event is triggered
         * within a Grid.
         *
         * @param event
         *            the key down event
         */
        public void onKeyDown(GridKeyDownEvent event);
    }

    /**
     * Handler for Grid key up events.
     */
    public abstract interface GridKeyUpHandler
            extends AbstractGridKeyEventHandler {
        /**
         * Perform actions that should happen when a key up event is triggered
         * within a Grid.
         *
         * @param event
         *            the key up event
         */
        public void onKeyUp(GridKeyUpEvent event);
    }

    /**
     * Handler for Grid key press events.
     */
    public abstract interface GridKeyPressHandler
            extends AbstractGridKeyEventHandler {
        /**
         * Perform actions that should happen when a key press event is
         * triggered within a Grid.
         *
         * @param event
         *            the key press event
         */
        public void onKeyPress(GridKeyPressEvent event);
    }

}
