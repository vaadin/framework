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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.CustomComponent;

/**
 * A component that represents color selection history within a color picker.
 * 
 * @since 7.0.0
 */
public class ColorPickerHistory extends CustomComponent implements
        ColorSelector, ColorChangeListener {

    private static final String STYLENAME = "v-colorpicker-history";

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

    /** The rows. */
    private static final int rows = 4;

    /** The columns. */
    private static final int columns = 15;

    /** Temporary color history for when the component is detached. */
    private ArrayBlockingQueue<Color> tempHistory = new ArrayBlockingQueue<Color>(
            rows * columns);

    /** The grid. */
    private final ColorPickerGrid grid;

    /**
     * Instantiates a new color picker history.
     */
    public ColorPickerHistory() {
        setPrimaryStyleName(STYLENAME);

        grid = new ColorPickerGrid(rows, columns);
        grid.setWidth("100%");
        grid.setPosition(0, 0);
        grid.addColorChangeListener(this);

        setCompositionRoot(grid);
    }

    @Override
    public void attach() {
        super.attach();
        createColorHistoryIfNecessary();
    }

    private void createColorHistoryIfNecessary() {
        List<Color> tempColors = new ArrayList<Color>(tempHistory);
        if (getSession().getAttribute("colorPickerHistory") == null) {
            getSession().setAttribute("colorPickerHistory",
                    new ArrayBlockingQueue<Color>(rows * columns));
        }
        for (Color color : tempColors) {
            setColor(color);
        }
        tempHistory.clear();
    }

    @SuppressWarnings("unchecked")
    private ArrayBlockingQueue<Color> getColorHistory() {
        if (isAttached()) {
            Object colorHistory = getSession().getAttribute(
                    "colorPickerHistory");
            if (colorHistory instanceof ArrayBlockingQueue<?>) {
                return (ArrayBlockingQueue<Color>) colorHistory;
            }
        }
        return tempHistory;
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        grid.setHeight(height);
    }

    @Override
    public void setColor(Color color) {

        ArrayBlockingQueue<Color> colorHistory = getColorHistory();

        // Check that the color does not already exist
        boolean exists = false;
        Iterator<Color> iter = colorHistory.iterator();
        while (iter.hasNext()) {
            if (color.equals(iter.next())) {
                exists = true;
                break;
            }
        }

        // If the color does not exist then add it
        if (!exists) {
            if (!colorHistory.offer(color)) {
                colorHistory.poll();
                colorHistory.offer(color);
            }
        }

        List<Color> colorList = new ArrayList<Color>(colorHistory);

        // Invert order of colors
        Collections.reverse(colorList);

        // Move the selected color to the front of the list
        Collections.swap(colorList, colorList.indexOf(color), 0);

        // Create 2d color map
        Color[][] colors = new Color[rows][columns];
        iter = colorList.iterator();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (iter.hasNext()) {
                    colors[row][col] = iter.next();
                } else {
                    colors[row][col] = Color.WHITE;
                }
            }
        }

        grid.setColorGrid(colors);
        grid.markAsDirty();
    }

    @Override
    public Color getColor() {
        return getColorHistory().peek();
    }

    /**
     * Gets the history.
     * 
     * @return the history
     */
    public List<Color> getHistory() {
        ArrayBlockingQueue<Color> colorHistory = getColorHistory();
        Color[] array = colorHistory.toArray(new Color[colorHistory.size()]);
        return Collections.unmodifiableList(Arrays.asList(array));
    }

    /**
     * Checks if the history contains given color.
     * 
     * @param c
     *            the color
     * 
     * @return true, if successful
     */
    public boolean hasColor(Color c) {
        return getColorHistory().contains(c);
    }

    /**
     * Adds a color change listener
     * 
     * @param listener
     *            The listener
     */
    @Override
    public void addColorChangeListener(ColorChangeListener listener) {
        addListener(ColorChangeEvent.class, listener, COLOR_CHANGE_METHOD);
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
    public void colorChanged(ColorChangeEvent event) {
        fireEvent(new ColorChangeEvent(this, event.getColor()));
    }
}
