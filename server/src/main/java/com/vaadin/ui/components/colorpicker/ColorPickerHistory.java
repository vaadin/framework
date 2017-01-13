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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

/**
 * A component that represents color selection history within a color picker.
 *
 * @since 7.0.0
 */
public class ColorPickerHistory extends CustomField<Color> {

    private static final String STYLENAME = "v-colorpicker-history";

    private static final int ROWS = 4;

    private static final int COLUMNS = 15;

    /** Temporary color history for when the component is detached. */
    private final ArrayBlockingQueue<Color> tempHistory = new ArrayBlockingQueue<>(
            ROWS * COLUMNS);

    @Override
    protected Component initContent() {
        setPrimaryStyleName(STYLENAME);

        ColorPickerGrid grid = new ColorPickerGrid(ROWS, COLUMNS);
        grid.setWidth("100%");
        grid.setPosition(0, 0);
        grid.addValueChangeListener(
                event -> fireEvent(new ValueChangeEvent<>(this,
                        event.getOldValue(), event.isUserOriginated())));

        return grid;
    }

    @Override
    protected ColorPickerGrid getContent() {
        return (ColorPickerGrid) super.getContent();
    }

    @Override
    public void attach() {
        super.attach();
        createColorHistoryIfNecessary();
    }

    private void createColorHistoryIfNecessary() {
        List<Color> tempColors = new ArrayList<>(tempHistory);
        if (getSession().getAttribute("colorPickerHistory") == null) {
            getSession().setAttribute("colorPickerHistory",
                    new ArrayBlockingQueue<Color>(ROWS * COLUMNS));
        }
        for (Color color : tempColors) {
            setValue(color);
        }
        tempHistory.clear();
    }

    @SuppressWarnings("unchecked")
    private ArrayBlockingQueue<Color> getColorHistory() {
        if (isAttached()) {
            Object colorHistory = getSession()
                    .getAttribute("colorPickerHistory");
            if (colorHistory instanceof ArrayBlockingQueue<?>) {
                return (ArrayBlockingQueue<Color>) colorHistory;
            }
        }
        return tempHistory;
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        getContent().setHeight(height);
    }

    @Override
    public Color getValue() {
        return getColorHistory().peek();
    }

    @Override
    protected void doSetValue(Color color) {

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

        List<Color> colorList = new ArrayList<>(colorHistory);

        // Invert order of colors
        Collections.reverse(colorList);

        // Move the selected color to the front of the list
        Collections.swap(colorList, colorList.indexOf(color), 0);

        // Create 2d color map
        Color[][] colors = new Color[ROWS][COLUMNS];
        iter = colorList.iterator();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (iter.hasNext()) {
                    colors[row][col] = iter.next();
                } else {
                    colors[row][col] = Color.WHITE;
                }
            }
        }

        getContent().setColorGrid(colors);
        getContent().markAsDirty();
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
}
