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

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.colorpicker.ColorPickerGradientServerRpc;
import com.vaadin.shared.ui.colorpicker.ColorPickerGradientState;
import com.vaadin.ui.AbstractColorPicker.Coordinates2Color;
import com.vaadin.ui.AbstractComponent;

/**
 * A component that represents a color gradient within a color picker.
 * 
 * @since 7.0.0
 */
public class ColorPickerGradient extends AbstractComponent implements
        ColorSelector {

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

    private ColorPickerGradientServerRpc rpc = new ColorPickerGradientServerRpc() {

        @Override
        public void select(int cursorX, int cursorY) {
            x = cursorX;
            y = cursorY;
            color = converter.calculate(x, y);

            fireColorChanged(color);
        }
    };

    /** The converter. */
    private Coordinates2Color converter;

    /** The foreground color. */
    private Color color;

    /** The x-coordinate. */
    private int x = 0;

    /** The y-coordinate. */
    private int y = 0;

    private ColorPickerGradient() {
        registerRpc(rpc);
        // width and height must be set here instead of in theme, otherwise
        // coordinate calculations fail
        getState().width = "220px";
        getState().height = "220px";
    }

    /**
     * Instantiates a new color picker gradient.
     * 
     * @param id
     *            the id
     * @param converter
     *            the converter
     */
    public ColorPickerGradient(String id, Coordinates2Color converter) {
        this();
        addStyleName(id);
        this.converter = converter;
    }

    @Override
    public void setColor(Color c) {
        color = c;

        int[] coords = converter.calculate(c);
        x = coords[0];
        y = coords[1];

        getState().cursorX = x;
        getState().cursorY = y;

    }

    @Override
    public void addColorChangeListener(ColorChangeListener listener) {
        addListener(ColorChangeEvent.class, listener, COLOR_CHANGE_METHOD);
    }

    @Override
    public void removeColorChangeListener(ColorChangeListener listener) {
        removeListener(ColorChangeEvent.class, listener);
    }

    /**
     * Sets the background color.
     * 
     * @param color
     *            the new background color
     */
    public void setBackgroundColor(Color color) {
        getState().bgColor = color.getCSS();
    }

    @Override
    public Color getColor() {
        return color;
    }

    /**
     * Notifies the listeners that the color has changed
     * 
     * @param color
     *            The color which it changed to
     */
    public void fireColorChanged(Color color) {
        fireEvent(new ColorChangeEvent(this, color));
    }

    @Override
    protected ColorPickerGradientState getState() {
        return (ColorPickerGradientState) super.getState();
    }
}
