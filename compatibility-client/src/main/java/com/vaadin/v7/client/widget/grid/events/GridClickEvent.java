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
import com.vaadin.v7.client.widget.grid.events.AbstractGridMouseEventHandler.GridClickHandler;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.client.widgets.Grid.AbstractGridMouseEvent;
import com.vaadin.v7.shared.ui.grid.GridConstants.Section;

/**
 * Represents native mouse click event in Grid.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridClickEvent extends AbstractGridMouseEvent<GridClickHandler> {

    public static final Type<GridClickHandler> TYPE = new Type<GridClickHandler>(
            BrowserEvents.CLICK, new GridClickEvent());

    /**
     * @since 7.7.9
     */
    public GridClickEvent() {
    }

    /**
     * @deprecated This constructor's arguments are no longer used. Use the
     *             no-args constructor instead.
     */
    @Deprecated
    public GridClickEvent(Grid<?> grid, CellReference<?> targetCell) {
    }

    @Override
    public Type<GridClickHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected String getBrowserEventType() {
        return BrowserEvents.CLICK;
    }

    @Override
    protected void doDispatch(GridClickHandler handler, Section section) {
        if ((section == Section.BODY && handler instanceof BodyClickHandler)
                || (section == Section.HEADER
                        && handler instanceof HeaderClickHandler)
                || (section == Section.FOOTER
                        && handler instanceof FooterClickHandler)) {
            handler.onClick(this);
        }
    }
}
