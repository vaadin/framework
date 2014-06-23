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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;

/**
 * A component that represents color selection preview within a color picker.
 * 
 * @since 7.0.0
 */
public class ColorPickerPreview extends CssLayout implements ColorSelector,
        ValueChangeListener {

    private static final String STYLE_DARK_COLOR = "v-textfield-dark";
    private static final String STYLE_LIGHT_COLOR = "v-textfield-light";

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

    /** The color. */
    private Color color;

    /** The field. */
    private final TextField field;

    /** The old value. */
    private String oldValue;

    private ColorPickerPreview() {
        setStyleName("v-colorpicker-preview");
        setImmediate(true);
        field = new TextField();
        field.setImmediate(true);
        field.setSizeFull();
        field.setStyleName("v-colorpicker-preview-textfield");
        field.setData(this);
        field.addValueChangeListener(this);
        addComponent(field);
    }

    /**
     * Instantiates a new color picker preview.
     */
    public ColorPickerPreview(Color color) {
        this();
        setColor(color);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;

        // Unregister listener
        field.removeValueChangeListener(this);

        String colorCSS = color.getCSS();
        field.setValue(colorCSS);

        if (field.isValid()) {
            oldValue = colorCSS;
        } else {
            field.setValue(oldValue);
        }

        // Re-register listener
        field.addValueChangeListener(this);

        // Set the text color
        field.removeStyleName(STYLE_DARK_COLOR);
        field.removeStyleName(STYLE_LIGHT_COLOR);
        if (this.color.getRed() + this.color.getGreen() + this.color.getBlue() < 3 * 128) {
            field.addStyleName(STYLE_DARK_COLOR);
        } else {
            field.addStyleName(STYLE_LIGHT_COLOR);
        }

        markAsDirty();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void addColorChangeListener(ColorChangeListener listener) {
        addListener(ColorChangeEvent.class, listener, COLOR_CHANGE_METHOD);
    }

    @Override
    public void removeColorChangeListener(ColorChangeListener listener) {
        removeListener(ColorChangeEvent.class, listener);
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        String value = (String) event.getProperty().getValue();
        try {
            if (value != null) {
                /*
                 * Description of supported formats see
                 * http://www.w3schools.com/cssref/css_colors_legal.asp
                 */
                if (value.length() == 7 && value.startsWith("#")) {
                    // CSS color format (e.g. #000000)
                    int red = Integer.parseInt(value.substring(1, 3), 16);
                    int green = Integer.parseInt(value.substring(3, 5), 16);
                    int blue = Integer.parseInt(value.substring(5, 7), 16);
                    color = new Color(red, green, blue);

                } else if (value.startsWith("rgb")) {
                    // RGB color format rgb/rgba(255,255,255,0.1)
                    String[] colors = value.substring(value.indexOf("(") + 1,
                            value.length() - 1).split(",");

                    int red = Integer.parseInt(colors[0]);
                    int green = Integer.parseInt(colors[1]);
                    int blue = Integer.parseInt(colors[2]);
                    if (colors.length > 3) {
                        int alpha = (int) (Double.parseDouble(colors[3]) * 255d);
                        color = new Color(red, green, blue, alpha);
                    } else {
                        color = new Color(red, green, blue);
                    }

                } else if (value.startsWith("hsl")) {
                    // HSL color format hsl/hsla(100,50%,50%,1.0)
                    String[] colors = value.substring(value.indexOf("(") + 1,
                            value.length() - 1).split(",");

                    int hue = Integer.parseInt(colors[0]);
                    int saturation = Integer.parseInt(colors[1]
                            .replace("%", ""));
                    int lightness = Integer
                            .parseInt(colors[2].replace("%", ""));
                    int rgb = Color.HSLtoRGB(hue, saturation, lightness);

                    if (colors.length > 3) {
                        int alpha = (int) (Double.parseDouble(colors[3]) * 255d);
                        color = new Color(rgb);
                        color.setAlpha(alpha);
                    } else {
                        color = new Color(rgb);
                    }
                }

                oldValue = value;
                fireEvent(new ColorChangeEvent((Component) field.getData(),
                        color));
            }

        } catch (NumberFormatException nfe) {
            // Revert value
            field.setValue(oldValue);
        }
    }

    /**
     * Called when the component is refreshing
     */
    @Override
    protected String getCss(Component c) {
        return "background: " + color.getCSS();
    }
}
