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
package com.vaadin.ui.components.colorpicker;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.colorpicker.ColorPickerGridServerRpc;
import com.vaadin.shared.ui.colorpicker.ColorPickerGridState;
import com.vaadin.ui.AbstractField;

/**
 * A component that represents a color selection grid within a color picker.
 *
 * @since 7.0.0
 */
public class ColorPickerGrid extends AbstractField<Color> {

    private static final String STYLENAME = "v-colorpicker-grid";

    private ColorPickerGridServerRpc rpc = new ColorPickerGridServerRpc() {

        @Override
        public void select(int x, int y) {
            Color oldValue = colorGrid[x][y];
            ColorPickerGrid.this.x = x;
            ColorPickerGrid.this.y = y;
            fireEvent(new ValueChangeEvent<>(ColorPickerGrid.this, oldValue,
                    true));
        }

        @Override
        public void refresh() {
            for (int row = 0; row < getRows(); row++) {
                for (int col = 0; col < getColumns(); col++) {
                    changedColors.put(new Point(row, col), colorGrid[row][col]);
                }
            }
            sendChangedColors();
            markAsDirty();
        }
    };

    /** The selected x coordinate. */
    private int x = 0;

    /** The selected y coordinate. */
    private int y = 0;

    private Color[][] colorGrid;

    private final Map<Point, Color> changedColors = new HashMap<>();

    /**
     * Instantiates a new color picker grid.
     */
    public ColorPickerGrid() {
        this(1, 1);
    }

    /**
     * Instantiates a new color picker grid.
     *
     * @param rows
     *            the rows
     * @param columns
     *            the columns
     */
    public ColorPickerGrid(int rows, int columns) {
        this(new Color[rows][columns]);
        setValue(Color.WHITE);
    }

    /**
     * Instantiates a new color picker grid.
     *
     * @param colors
     *            the colors
     */
    public ColorPickerGrid(Color[][] colors) {
        registerRpc(rpc);
        setPrimaryStyleName(STYLENAME);
        setColorGrid(colors);
    }

    private void setColumnCount(int columns) {
        getState().columnCount = columns;
    }

    private void setRowCount(int rows) {
        getState().rowCount = rows;
    }

    private void sendChangedColors() {
        if (!changedColors.isEmpty()) {
            String[] colors = new String[changedColors.size()];
            String[] xCoords = new String[changedColors.size()];
            String[] yCoords = new String[changedColors.size()];
            int counter = 0;
            for (Point p : changedColors.keySet()) {
                Color c = changedColors.get(p);
                if (c == null) {
                    continue;
                }

                String color = c.getCSS();

                colors[counter] = color;
                xCoords[counter] = String.valueOf((int) p.getX());
                yCoords[counter] = String.valueOf((int) p.getY());
                counter++;
            }
            getState().changedColor = colors;
            getState().changedX = xCoords;
            getState().changedY = yCoords;

            changedColors.clear();
        }
    }

    /**
     * Sets the color grid.
     *
     * @param colors
     *            the new color grid
     */
    public void setColorGrid(Color[][] colors) {
        setRowCount(colors.length);
        setColumnCount(colors[0].length);
        colorGrid = colors;

        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                changedColors.put(new Point(row, col), colorGrid[row][col]);
            }
        }
        sendChangedColors();
    }

    /**
     * Sets the position.
     *
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void setPosition(int x, int y) {
        if (x >= 0 && x < getColumns() && y >= 0 && y < getRows()) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public int[] getPosition() {
        return new int[] { x, y };
    }

    /**
     * Sets the value of this object. If the new value is not equal to
     * {@code getValue()}, fires a {@link ValueChangeEvent}. Throws
     * {@code NullPointerException} if the value is null.
     *
     * @param color
     *            the new value, not {@code null}
     * @throws NullPointerException
     *             if {@code color} is {@code null}
     */
    @Override
    public void setValue(Color color) {
        Objects.requireNonNull(color, "value cannot be null");
        super.setValue(color);
    }

    @Override
    public Color getValue() {
        return colorGrid[x][y];
    }

    @Override
    protected void doSetValue(Color color) {
        colorGrid[x][y] = color;
        changedColors.put(new Point(x, y), color);
        sendChangedColors();
    }

    @Override
    protected ColorPickerGridState getState() {
        return (ColorPickerGridState) super.getState();
    }

    @Override
    protected ColorPickerGridState getState(boolean markAsDirty) {
        return (ColorPickerGridState) super.getState(markAsDirty);
    }

    private int getColumns() {
        return getState(false).columnCount;
    }

    private int getRows() {
        return getState(false).rowCount;
    }
}
