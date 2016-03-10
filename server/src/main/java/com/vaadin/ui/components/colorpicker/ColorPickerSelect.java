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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

/**
 * A component that represents color selection swatches within a color picker.
 * 
 * @since 7.0.0
 */
public class ColorPickerSelect extends CustomComponent implements
        ColorSelector, ValueChangeListener {

    /** The range. */
    private final ComboBox range;

    /** The grid. */
    private final ColorPickerGrid grid;

    /**
     * The Enum ColorRangePropertyId.
     */
    private enum ColorRangePropertyId {
        ALL("All colors"), RED("Red colors"), GREEN("Green colors"), BLUE(
                "Blue colors");

        /** The caption. */
        private String caption;

        /**
         * Instantiates a new color range property id.
         * 
         * @param caption
         *            the caption
         */
        ColorRangePropertyId(String caption) {
            this.caption = caption;
        }

        @Override
        public String toString() {
            return caption;
        }
    }

    /**
     * Instantiates a new color picker select.
     * 
     * @param rows
     *            the rows
     * @param columns
     *            the columns
     */
    public ColorPickerSelect() {

        VerticalLayout layout = new VerticalLayout();
        setCompositionRoot(layout);

        setStyleName("colorselect");
        setWidth("100%");

        range = new ComboBox();
        range.setImmediate(true);
        range.setImmediate(true);
        range.setNullSelectionAllowed(false);
        range.setNewItemsAllowed(false);
        range.setWidth("100%");
        range.addValueChangeListener(this);

        for (ColorRangePropertyId id : ColorRangePropertyId.values()) {
            range.addItem(id);
        }
        range.select(ColorRangePropertyId.ALL);

        layout.addComponent(range);

        grid = new ColorPickerGrid(createAllColors(14, 10));
        grid.setWidth("100%");

        layout.addComponent(grid);
    }

    /**
     * Creates the all colors.
     * 
     * @param rows
     *            the rows
     * @param columns
     *            the columns
     * 
     * @return the color[][]
     */
    private Color[][] createAllColors(int rows, int columns) {
        Color[][] colors = new Color[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                // Create the color grid by varying the saturation and value
                if (row < (rows - 1)) {
                    // Calculate new hue value
                    float hue = ((float) col / (float) columns);
                    float saturation = 1f;
                    float value = 1f;

                    // For the upper half use value=1 and variable
                    // saturation
                    if (row < (rows / 2)) {
                        saturation = ((row + 1f) / (rows / 2f));
                    } else {
                        value = 1f - ((row - (rows / 2f)) / (rows / 2f));
                    }

                    colors[row][col] = new Color(Color.HSVtoRGB(hue,
                            saturation, value));
                }

                // The last row should have the black&white gradient
                else {
                    float hue = 0f;
                    float saturation = 0f;
                    float value = 1f - ((float) col / (float) columns);

                    colors[row][col] = new Color(Color.HSVtoRGB(hue,
                            saturation, value));
                }
            }
        }

        return colors;
    }

    /**
     * Creates the color.
     * 
     * @param color
     *            the color
     * @param rows
     *            the rows
     * @param columns
     *            the columns
     * 
     * @return the color[][]
     */
    private Color[][] createColors(Color color, int rows, int columns) {
        Color[][] colors = new Color[rows][columns];

        float[] hsv = color.getHSV();

        float hue = hsv[0];
        float saturation = 1f;
        float value = 1f;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                int index = row * columns + col;
                saturation = 1f;
                value = 1f;

                if (index <= ((rows * columns) / 2)) {
                    saturation = index
                            / (((float) rows * (float) columns) / 2f);
                } else {
                    index -= ((rows * columns) / 2);
                    value = 1f - index
                            / (((float) rows * (float) columns) / 2f);
                }

                colors[row][col] = new Color(Color.HSVtoRGB(hue, saturation,
                        value));
            }
        }

        return colors;
    }

    @Override
    public Color getColor() {
        return grid.getColor();
    }

    @Override
    public void setColor(Color color) {
        grid.getColor();
    }

    @Override
    public void addColorChangeListener(ColorChangeListener listener) {
        grid.addColorChangeListener(listener);
    }

    @Override
    public void removeColorChangeListener(ColorChangeListener listener) {
        grid.removeColorChangeListener(listener);
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        if (grid == null) {
            return;
        }

        if (event.getProperty().getValue() == ColorRangePropertyId.ALL) {
            grid.setColorGrid(createAllColors(14, 10));
        } else if (event.getProperty().getValue() == ColorRangePropertyId.RED) {
            grid.setColorGrid(createColors(new Color(0xFF, 0, 0), 14, 10));
        } else if (event.getProperty().getValue() == ColorRangePropertyId.GREEN) {
            grid.setColorGrid(createColors(new Color(0, 0xFF, 0), 14, 10));
        } else if (event.getProperty().getValue() == ColorRangePropertyId.BLUE) {
            grid.setColorGrid(createColors(new Color(0, 0, 0xFF), 14, 10));
        }
    }
}
