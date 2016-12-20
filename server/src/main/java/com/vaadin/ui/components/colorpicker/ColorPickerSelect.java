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

import java.util.EnumSet;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.VerticalLayout;

/**
 * A component that represents color selection swatches within a color picker.
 *
 * @since 7.0.0
 */
public class ColorPickerSelect extends CustomField<Color> {

    private ComboBox<ColorRange> range;

    private ColorPickerGrid grid;

    private enum ColorRange {
        ALL("All colors"), RED("Red colors"), GREEN("Green colors"), BLUE(
                "Blue colors");

        private final String caption;

        ColorRange(String caption) {
            this.caption = caption;
        }

        @Override
        public String toString() {
            return caption;
        }
    }

    @Override
    protected Component initContent() {
        VerticalLayout layout = new VerticalLayout();

        setStyleName("colorselect");
        setWidth("100%");

        range = new ComboBox<>(null, EnumSet.allOf(ColorRange.class));
        range.setEmptySelectionAllowed(false);
        range.setWidth("100%");
        range.addValueChangeListener(this::valueChange);

        range.setValue(ColorRange.ALL);

        layout.addComponent(range);

        grid = new ColorPickerGrid(createAllColors(14, 10));
        grid.setWidth("100%");
        grid.addValueChangeListener(this::fireEvent);

        layout.addComponent(grid);

        return layout;
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
                if (row < rows - 1) {
                    // Create the color grid by varying the saturation and value

                    // Calculate new hue value
                    float hue = (float) col / (float) columns;
                    float saturation = 1f;
                    float value = 1f;

                    // For the upper half use value=1 and variable
                    // saturation
                    if (row < rows / 2) {
                        saturation = (row + 1f) / (rows / 2f);
                    } else {
                        value = 1f - (row - rows / 2f) / (rows / 2f);
                    }

                    colors[row][col] = new Color(
                            Color.HSVtoRGB(hue, saturation, value));
                } else {
                    // The last row should have the black&white gradient
                    float hue = 0f;
                    float saturation = 0f;
                    float value = 1f - (float) col / (float) columns;

                    colors[row][col] = new Color(
                            Color.HSVtoRGB(hue, saturation, value));
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

                if (index <= rows * columns / 2) {
                    saturation = index / ((float) rows * (float) columns / 2f);
                } else {
                    index -= rows * columns / 2;
                    value = 1f - index / ((float) rows * (float) columns / 2f);
                }

                colors[row][col] = new Color(
                        Color.HSVtoRGB(hue, saturation, value));
            }
        }

        return colors;
    }

    private void valueChange(ValueChangeEvent<ColorRange> event) {
        if (grid == null) {
            return;
        }

        if (event.getValue() == ColorRange.ALL) {
            grid.setColorGrid(createAllColors(14, 10));
        } else if (event.getValue() == ColorRange.RED) {
            grid.setColorGrid(createColors(new Color(0xFF, 0, 0), 14, 10));
        } else if (event.getValue() == ColorRange.GREEN) {
            grid.setColorGrid(createColors(new Color(0, 0xFF, 0), 14, 10));
        } else if (event.getValue() == ColorRange.BLUE) {
            grid.setColorGrid(createColors(new Color(0, 0, 0xFF), 14, 10));
        }
    }

    /**
     * Returns the selected value.
     * <p>
     * Value can be {@code null} if component is not yet initialized via
     * {@link #initContent()}
     *
     * @see ColorPickerSelect#initContent()
     *
     * @return the selected color, may be {@code null}
     */
    @Override
    public Color getValue() {
        if (grid == null) {
            return null;
        }
        return grid.getValue();
    }

    @Override
    protected void doSetValue(Color value) {
        grid.setValue(value);
    }
}
