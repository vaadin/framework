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
import com.vaadin.client.widget.grid.events.AbstractGridMouseEventHandler.GridDoubleClickHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.AbstractGridMouseEvent;
import com.vaadin.client.widgets.Grid.Section;

/**
 * Represents native mouse double click event in Grid.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridDoubleClickEvent extends
        AbstractGridMouseEvent<GridDoubleClickHandler> {

    public GridDoubleClickEvent(Grid<?> grid, CellReference<?> targetCell) {
        super(grid, targetCell);
    }

    @Override
    protected String getBrowserEventType() {
        return BrowserEvents.DBLCLICK;
    }

    @Override
    protected void doDispatch(GridDoubleClickHandler handler, Section section) {
        if ((section == Section.BODY && handler instanceof BodyDoubleClickHandler)
                || (section == Section.HEADER && handler instanceof HeaderDoubleClickHandler)
                || (section == Section.FOOTER && handler instanceof FooterDoubleClickHandler)) {
            handler.onDoubleClick(this);
        }
    }

}
