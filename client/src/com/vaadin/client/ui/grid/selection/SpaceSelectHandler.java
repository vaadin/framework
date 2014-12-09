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
package com.vaadin.client.ui.grid.selection;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.DataAvailableEvent;
import com.vaadin.client.ui.grid.DataAvailableHandler;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.events.BodyKeyDownHandler;
import com.vaadin.client.ui.grid.events.BodyKeyUpHandler;
import com.vaadin.client.ui.grid.events.GridKeyDownEvent;
import com.vaadin.client.ui.grid.events.GridKeyUpEvent;
import com.vaadin.shared.ui.grid.ScrollDestination;

/**
 * Generic class to perform selections when pressing space key.
 * 
 * @since
 * @author Vaadin Ltd
 * @param <T>
 *            row data type
 */
public class SpaceSelectHandler<T> {

    /**
     * Handler for space key down events in Grid Body
     */
    private class SpaceKeyDownHandler implements BodyKeyDownHandler {
        private HandlerRegistration scrollHandler = null;

        @Override
        public void onKeyDown(GridKeyDownEvent event) {
            if (event.getNativeKeyCode() != KeyCodes.KEY_SPACE || spaceDown) {
                return;
            }

            // Prevent space page scrolling
            event.getNativeEvent().preventDefault();

            spaceDown = true;
            Cell focused = event.getFocusedCell();
            final int rowIndex = focused.getRow();

            if (scrollHandler != null) {
                scrollHandler.removeHandler();
                scrollHandler = null;
            }

            scrollHandler = grid
                    .addDataAvailableHandler(new DataAvailableHandler() {

                        @Override
                        public void onDataAvailable(
                                DataAvailableEvent dataAvailableEvent) {
                            if (dataAvailableEvent.getAvailableRows().contains(
                                    rowIndex)) {
                                setSelected(grid, rowIndex);
                                scrollHandler.removeHandler();
                                scrollHandler = null;
                            }
                        }
                    });
            grid.scrollToRow(rowIndex, ScrollDestination.ANY);
        }

        protected void setSelected(Grid<T> grid, int rowIndex) {
            T row = grid.getDataSource().getRow(rowIndex);

            if (grid.isSelected(row)) {
                grid.deselect(row);
            } else {
                grid.select(row);
            }
        }
    }

    private boolean spaceDown = false;
    private Grid<T> grid;
    private HandlerRegistration spaceUpHandler;
    private HandlerRegistration spaceDownHandler;

    /**
     * Constructor for SpaceSelectHandler. This constructor will add all
     * necessary handlers for selecting rows with space.
     * 
     * @param grid
     *            grid to attach to
     */
    public SpaceSelectHandler(Grid<T> grid) {
        this.grid = grid;
        spaceDownHandler = grid
                .addBodyKeyDownHandler(new SpaceKeyDownHandler());
        spaceUpHandler = grid.addBodyKeyUpHandler(new BodyKeyUpHandler() {

            @Override
            public void onKeyUp(GridKeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_SPACE) {
                    spaceDown = false;
                }
            }
        });
    }

    /**
     * Clean up function for removing all now obsolete handlers.
     */
    public void removeHandler() {
        spaceDownHandler.removeHandler();
        spaceUpHandler.removeHandler();
    }
}