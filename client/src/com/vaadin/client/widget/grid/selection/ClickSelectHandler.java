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
package com.vaadin.client.widget.grid.selection;

import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.widget.grid.events.BodyClickHandler;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widgets.Grid;

/**
 * Generic class to perform selections when clicking on cells in body of Grid.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ClickSelectHandler<T> {

    private Grid<T> grid;
    private HandlerRegistration clickHandler;
    private boolean deselectAllowed = true;

    private class RowClickHandler implements BodyClickHandler {

        @Override
        public void onClick(GridClickEvent event) {
            T row = (T) event.getTargetCell().getRow();
            if (!grid.isSelected(row)) {
                grid.select(row);
            } else if (deselectAllowed) {
                grid.deselect(row);
            }
        }
    }

    /**
     * Constructor for ClickSelectHandler. This constructor will add all
     * necessary handlers for selecting rows by clicking cells.
     * 
     * @param grid
     *            grid to attach to
     */
    public ClickSelectHandler(Grid<T> grid) {
        this.grid = grid;
        clickHandler = grid.addBodyClickHandler(new RowClickHandler());
    }

    /**
     * Clean up function for removing all now obsolete handlers.
     */
    public void removeHandler() {
        clickHandler.removeHandler();
    }

    /**
     * Sets whether clicking the currently selected row should deselect the row.
     * 
     * @param deselectAllowed
     *            <code>true</code> to allow deselecting the selected row;
     *            otherwise <code>false</code>
     */
    public void setDeselectAllowed(boolean deselectAllowed) {
        this.deselectAllowed = deselectAllowed;
    }
}
