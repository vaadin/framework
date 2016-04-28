/*
 * Copyright 2000-2014 Vaadin Ltd.
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

    public abstract interface GridKeyDownHandler extends
            AbstractGridKeyEventHandler {
        public void onKeyDown(GridKeyDownEvent event);
    }

    public abstract interface GridKeyUpHandler extends
            AbstractGridKeyEventHandler {
        public void onKeyUp(GridKeyUpEvent event);
    }

    public abstract interface GridKeyPressHandler extends
            AbstractGridKeyEventHandler {
        public void onKeyPress(GridKeyPressEvent event);
    }

}
