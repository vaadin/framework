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
package com.vaadin.v7.client.widget.grid.events;

import com.google.gwt.dom.client.BrowserEvents;
import com.vaadin.v7.client.widget.grid.CellReference;
import com.vaadin.v7.client.widget.grid.events.AbstractGridKeyEventHandler.GridKeyPressHandler;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.client.widgets.Grid.AbstractGridKeyEvent;
import com.vaadin.v7.shared.ui.grid.GridConstants.Section;

/**
 * Represents native key press event in Grid.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridKeyPressEvent
        extends AbstractGridKeyEvent<GridKeyPressHandler> {

    public static final Type<GridKeyPressHandler> TYPE = new Type<GridKeyPressHandler>(
            BrowserEvents.KEYPRESS, new GridKeyPressEvent());

    /**
     * @since 7.7.9
     */
    public GridKeyPressEvent() {
    }

    /**
     * @deprecated This constructor's arguments are no longer used. Use the
     *             no-args constructor instead.
     */
    @Deprecated
    public GridKeyPressEvent(Grid<?> grid, CellReference<?> targetCell) {
    }

    @Override
    public Type<GridKeyPressHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void doDispatch(GridKeyPressHandler handler, Section section) {
        if ((section == Section.BODY && handler instanceof BodyKeyPressHandler)
                || (section == Section.HEADER
                        && handler instanceof HeaderKeyPressHandler)
                || (section == Section.FOOTER
                        && handler instanceof FooterKeyPressHandler)) {
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