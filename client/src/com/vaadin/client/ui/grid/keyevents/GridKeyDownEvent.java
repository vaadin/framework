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
package com.vaadin.client.ui.grid.keyevents;

import com.google.gwt.dom.client.BrowserEvents;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.Grid.AbstractGridKeyEvent;
import com.vaadin.client.ui.grid.keyevents.AbstractGridKeyEventHandler.GridKeyDownHandler;

/**
 * Represents native key down event in Grid.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridKeyDownEvent extends AbstractGridKeyEvent<GridKeyDownHandler> {

    public GridKeyDownEvent(Grid<?> grid) {
        super(grid);
    }

    @Override
    protected void dispatch(GridKeyDownHandler handler) {
        super.dispatch(handler);
        if ((activeSection == GridSection.BODY && handler instanceof BodyKeyDownHandler)
                || (activeSection == GridSection.HEADER && handler instanceof HeaderKeyDownHandler)
                || (activeSection == GridSection.FOOTER && handler instanceof FooterKeyDownHandler)) {
            handler.onKeyDown(this);
        }
    }

    @Override
    protected String getBrowserEventType() {
        return BrowserEvents.KEYDOWN;
    }

}
