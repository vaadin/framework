/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.Objects;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.colorpicker.ColorPickerGradientServerRpc;
import com.vaadin.shared.ui.colorpicker.ColorPickerGradientState;
import com.vaadin.ui.AbstractColorPicker.Coordinates2Color;
import com.vaadin.ui.AbstractField;

/**
 * A component that represents a color gradient within a color picker.
 *
 * @since 7.0.0
 */
public class ColorPickerGradient extends AbstractField<Color> {

    /** The converter. */
    private Coordinates2Color converter;

    /** The foreground color. */
    private Color color;

    private ColorPickerGradientServerRpc rpc = (cursorX,
            cursorY) -> setValue(converter.calculate(cursorX, cursorY), true);

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

    /**
     * Sets the value of this object. If the new value is not equal to
     * {@code getValue()}, fires a {@link ValueChangeEvent}. Throws
     * {@code NullPointerException} if the value is null.
     *
     * @param color
     *            the new color, not {@code null}
     * @throws NullPointerException
     *             if {@code color} is {@code null}
     */
    @Override
    public void setValue(Color color) {
        Objects.requireNonNull(color, "value must not be null");
        super.setValue(color);
    }

    @Override
    public Color getValue() {
        return color;
    }

    @Override
    protected void doSetValue(Color color) {
        this.color = color;
        int[] coords = converter.calculate(color);
        getState().cursorX = coords[0];
        getState().cursorY = coords[1];
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
    protected ColorPickerGradientState getState() {
        return (ColorPickerGradientState) super.getState();
    }

    @Override
    protected ColorPickerGradientState getState(boolean markAsDirty) {
        return (ColorPickerGradientState) super.getState(markAsDirty);
    }
}
