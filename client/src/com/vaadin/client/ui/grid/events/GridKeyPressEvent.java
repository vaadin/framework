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
package com.vaadin.client.ui.grid.events;

import com.google.gwt.dom.client.BrowserEvents;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.Grid.AbstractGridKeyEvent;
import com.vaadin.client.ui.grid.events.AbstractGridKeyEventHandler.GridKeyPressHandler;

/**
 * Represents native key press event in Grid.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridKeyPressEvent extends
        AbstractGridKeyEvent<GridKeyPressHandler> {

    public GridKeyPressEvent(Grid<?> grid) {
        super(grid);
    }

    @Override
    protected void doDispatch(GridKeyPressHandler handler, GridSection section) {
        if ((section == GridSection.BODY && handler instanceof BodyKeyPressHandler)
                || (section == GridSection.HEADER && handler instanceof HeaderKeyPressHandler)
                || (section == GridSection.FOOTER && handler instanceof FooterKeyPressHandler)) {
            handler.onKeyPress(this);
        }
    }

    @Override
    protected String getBrowserEventType() {
        return BrowserEvents.KEYPRESS;
    }

    /**
     * Gets the char code for this event.
     * 
     * @return the char code
     */
    public char getCharCode() {
        return (char) getUnicodeCharCode();
    }

    /**
     * Gets the Unicode char code (code point) for this event.
     * 
     * @return the Unicode char code
     */
    public int getUnicodeCharCode() {
        return getNativeEvent().getCharCode();
    }

    @Override
    public String toDebugString() {
        return super.toDebugString() + "[" + getCharCode() + "]";
    }
}