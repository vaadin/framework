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
package com.vaadin.ui.components.colorpicker;

import java.awt.Point;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.colorpicker.ColorPickerGridServerRpc;
import com.vaadin.shared.ui.colorpicker.ColorPickerGridState;
import com.vaadin.ui.AbstractComponent;

/**
 * A component that represents a color selection grid within a color picker.
 * 
 * @since 7.0.0
 */
public class ColorPickerGrid extends AbstractComponent implements ColorSelector {

    private static final String STYLENAME = "v-colorpicker-grid";

    private static final Method COLOR_CHANGE_METHOD;
    static {
        try {
            COLOR_CHANGE_METHOD = ColorChangeListener.class.getDeclaredMethod(
                    "colorChanged", new Class[] { ColorChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in ColorPicker");
        }
    }

    private ColorPickerGridServerRpc rpc = new ColorPickerGridServerRpc() {

        @Override
        public void select(int x, int y) {
            ColorPickerGrid.this.x = x;
            ColorPickerGrid.this.y = y;

            fireColorChanged(colorGrid[y][x]);
        }

        @Override
        public void refresh() {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    changedColors.put(new Point(row, col), colorGrid[row][col]);
                }
            }
            sendChangedColors();
            markAsDirty();
        }
    };

    /** The x-coordinate. */
    private int x = 0;

    /** The y-coordinate. */
    private int y = 0;

    /** The rows. */
    private int rows;

    /** The columns. */
    private int columns;

    /** The color grid. */
    private Color[][] colorGrid = new Color[1][1];

    /** The changed colors. */
    private final Map<Point, Color> changedColors = new HashMap<Point, Color>();

    /**
     * Instantiates a new color picker grid.
     */
    public ColorPickerGrid() {
        registerRpc(rpc);
        setPrimaryStyleName(STYLENAME);
        setColorGrid(new Color[1][1]);
        setColor(Color.WHITE);
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
        registerRpc(rpc);
        setPrimaryStyleName(STYLENAME);
        setColorGrid(new Color[rows][columns]);
        setColor(Color.WHITE);
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
        this.columns = columns;
        getState().columnCount = columns;
    }

    private void setRowCount(int rows) {
        this.rows = rows;
        getState().rowCount = rows;
    }

    private void sendChangedColors() {
        if (!changedColors.isEmpty()) {
            String[] colors = new String[changedColors.size()];
            String[] XCoords = new String[changedColors.size()];
            String[] YCoords = new String[changedColors.size()];
            int counter = 0;
            for (Point p : changedColors.keySet()) {
                Color c = changedColors.get(p);
                if (c == null) {
                    continue;
                }

                String color = c.getCSS();

                colors[counter] = color;
                XCoords[counter] = String.valueOf((int) p.getX());
                YCoords[counter] = String.valueOf((int) p.getY());
                counter++;
            }
            getState().changedColor = colors;
            getState().changedX = XCoords;
            getState().changedY = YCoords;

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

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                changedColors.put(new Point(row, col), colorGrid[row][col]);
            }
        }
        sendChangedColors();

        markAsDirty();
    }

    /**
     * Adds a color change listener
     * 
     * @param listener
     *            The color change listener
     */
    @Override
    public void addColorChangeListener(ColorChangeListener listener) {
        addListener(ColorChangeEvent.class, listener, COLOR_CHANGE_METHOD);
    }

    @Override
    public Color getColor() {
        return colorGrid[x][y];
    }

    /**
     * Removes a color change listener
     * 
     * @param listener
     *            The listener
     */
    @Override
    public void removeColorChangeListener(ColorChangeListener listener) {
        removeListener(ColorChangeEvent.class, listener);
    }

    @Override
    public void setColor(Color color) {
        colorGrid[x][y] = color;
        changedColors.put(new Point(x, y), color);
        sendChangedColors();
        markAsDirty();
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
        if (x >= 0 && x < columns && y >= 0 && y < rows) {
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
     * Notifies the listeners that a color change has occurred
     * 
     * @param color
     *            The color which it changed to
     */
    public void fireColorChanged(Color color) {
        fireEvent(new ColorChangeEvent(this, color));
    }

    @Override
    protected ColorPickerGridState getState() {
        return (ColorPickerGridState) super.getState();
    }
}
