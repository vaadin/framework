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

import com.google.gwt.dom.client.BrowserEvents;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.events.AbstractGridMouseEventHandler.GridClickHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.AbstractGridMouseEvent;
import com.vaadin.client.widgets.Grid.Section;

/**
 * Represents native mouse click event in Grid.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridClickEvent extends AbstractGridMouseEvent<GridClickHandler> {

    public GridClickEvent(Grid<?> grid, CellReference<?> targetCell) {
        super(grid, targetCell);
    }

    @Override
    protected String getBrowserEventType() {
        return BrowserEvents.CLICK;
    }

    @Override
    protected void doDispatch(GridClickHandler handler, Section section) {
        if ((section == Section.BODY && handler instanceof BodyClickHandler)
                || (section == Section.HEADER && handler instanceof HeaderClickHandler)
                || (section == Section.FOOTER && handler instanceof FooterClickHandler)) {
            handler.onClick(this);
        }
    }
}
