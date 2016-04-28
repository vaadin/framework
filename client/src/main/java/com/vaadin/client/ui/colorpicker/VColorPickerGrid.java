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
package com.vaadin.client.ui.colorpicker;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * Client side implementation for ColorPickerGrid.
 * 
 * @since 7.0.0
 * 
 */
public class VColorPickerGrid extends AbsolutePanel implements ClickHandler,
        HasClickHandlers {

    private int rows = 1;
    private int columns = 1;

    private Grid grid;

    private boolean gridLoaded = false;

    private int selectedX;
    private int selectedY;

    /**
     * Instantiates the client side component for a color picker grid.
     */
    public VColorPickerGrid() {
        super();

        this.add(createGrid(), 0, 0);
    }

    /**
     * Creates a grid according to the current row and column count information.
     * 
     * @return grid
     */
    private Grid createGrid() {
        grid = new Grid(rows, columns);
        grid.setWidth("100%");
        grid.setHeight("100%");
        grid.addClickHandler(this);
        return grid;
    }

    /**
     * Updates the row and column count and creates a new grid based on them.
     * The new grid replaces the old grid if one existed.
     * 
     * @param rowCount
     * @param columnCount
     */
    protected void updateGrid(int rowCount, int columnCount) {
        rows = rowCount;
        columns = columnCount;
        this.remove(grid);
        this.add(createGrid(), 0, 0);
    }

    /**
     * Updates the changed colors within the grid based on the given x- and
     * y-coordinates. Nothing happens if any of the parameters is null or the
     * parameter lengths don't match.
     * 
     * @param changedColor
     * @param changedX
     * @param changedY
     */
    protected void updateColor(String[] changedColor, String[] changedX,
            String[] changedY) {
        if (changedColor != null && changedX != null && changedY != null) {
            if (changedColor.length == changedX.length
                    && changedX.length == changedY.length) {
                for (int c = 0; c < changedColor.length; c++) {
                    Element element = grid.getCellFormatter().getElement(
                            Integer.parseInt(changedX[c]),
                            Integer.parseInt(changedY[c]));
                    element.getStyle().setProperty("background",
                            changedColor[c]);
                }
            }

            gridLoaded = true;
        }
    }

    /**
     * Returns currently selected x-coordinate of the grid.
     */
    protected int getSelectedX() {
        return selectedX;
    }

    /**
     * Returns currently selected y-coordinate of the grid.
     */
    protected int getSelectedY() {
        return selectedY;
    }

    /**
     * Returns true if the colors have been successfully updated at least once,
     * false otherwise.
     */
    protected boolean isGridLoaded() {
        return gridLoaded;
    }

    @Override
    public void onClick(ClickEvent event) {
        Cell cell = grid.getCellForEvent(event);
        if (cell == null) {
            return;
        }

        selectedY = cell.getRowIndex();
        selectedX = cell.getCellIndex();
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

}
