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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.data.HasValue;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.TextField;

/**
 * A component that represents color selection preview within a color picker.
 *
 * @since 7.0.0
 */
public class ColorPickerPreview extends CssLayout implements HasValue<Color> {
    private static final Logger getLogger() {
        return Logger.getLogger(ColorPickerPreview.class.getName());
    }

    private static final String STYLE_DARK_COLOR = "v-textfield-dark";
    private static final String STYLE_LIGHT_COLOR = "v-textfield-light";

    /** The color. */
    private Color color;

    /** The field. */
    private final TextField field;

    /** The old value. */
    private String oldValue;
    private Registration valueChangeListenerRegistration = null;

    private boolean readOnly;

    private ColorPickerPreview() {
        setStyleName("v-colorpicker-preview");
        field = new TextField();
        field.setSizeFull();
        field.setStyleName("v-colorpicker-preview-textfield");
        field.setData(this);
        valueChangeListenerRegistration = field
                .addValueChangeListener(this::valueChange);
        addComponent(field);
    }

    /**
     * Instantiates a new color picker preview.
     */
    public ColorPickerPreview(Color color) {
        this();
        setValue(color);
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
        Objects.requireNonNull(color, "color cannot be null");
        this.color = color;

        // Unregister listener
        valueChangeListenerRegistration.remove();

        String colorCSS = color.getCSS();
        field.setValue(colorCSS);
        field.setComponentError(null);

        oldValue = colorCSS;

        // Re-register listener
        valueChangeListenerRegistration = field
                .addValueChangeListener(this::valueChange);

        // Set the text color
        field.removeStyleName(STYLE_DARK_COLOR);
        field.removeStyleName(STYLE_LIGHT_COLOR);
        if (this.color.getRed() + this.color.getGreen()
                + this.color.getBlue() < 3 * 128) {
            field.addStyleName(STYLE_DARK_COLOR);
        } else {
            field.addStyleName(STYLE_LIGHT_COLOR);
        }

        markAsDirty();
    }

    @Override
    public Color getValue() {
        return color;
    }

    @Override
    public Registration addValueChangeListener(
            ValueChangeListener<Color> listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        return addListener(ValueChangeEvent.class, listener,
                ValueChangeListener.VALUE_CHANGE_METHOD);
    }

    private void valueChange(ValueChangeEvent<String> event) {
        String value = event.getValue();
        Color oldColor = color;
        if (value != null) {
            try {
                value = value.trim();
                ErrorMessage errorMessage = null;
                /*
                 * Description of supported formats see
                 * http://www.w3schools.com/cssref/css_colors_legal.asp
                 */
                if (HEX_PATTERN.matcher(value).matches()) {
                    Matcher m = HEX_PATTERN.matcher(value);
                    m.matches();
                    color = getHexPatternColor(m);
                } else if (RGB_PATTERN.matcher(value).matches()) {
                    Matcher m = RGB_PATTERN.matcher(value);
                    m.matches();
                    color = getRGBPatternColor(m);
                } else if (RGBA_PATTERN.matcher(value).matches()) {
                    Matcher m = RGBA_PATTERN.matcher(value);
                    m.matches();
                    color = getRGBPatternColor(m);
                    int alpha = (int) (Double.parseDouble(m.group("alpha"))
                            * 255d);
                    color.setAlpha(alpha);
                } else if (HSL_PATTERN.matcher(value).matches()) {
                    Matcher m = HSL_PATTERN.matcher(value);
                    m.matches();
                    color = getHSLPatternColor(m);
                    oldValue = value;
                } else if (HSLA_PATTERN.matcher(value).matches()) {
                    Matcher m = HSLA_PATTERN.matcher(value);
                    m.matches();
                    color = getHSLPatternColor(m);
                    int alpha = (int) (Double.parseDouble(m.group("alpha"))
                            * 255d);
                    color.setAlpha(alpha);
                } else {
                    errorMessage = new UserError(
                            value + "  does not match any accepted formats",
                            ContentMode.TEXT, ErrorLevel.WARNING);
                }

                field.setComponentError(errorMessage);
                oldValue = value;
                fireEvent(new ValueChangeEvent<>(this, oldColor,
                        event.isUserOriginated()));
            } catch (NumberFormatException e) {
                // Pattern matching ensures the validity of
                // the input, this should never happen
                getLogger().log(Level.WARNING,
                        "Parsing color from input '" + value + "' failed.");
                field.setComponentError(new UserError(
                        "Parsing color from input '" + value + "' failed.",
                        ContentMode.TEXT, ErrorLevel.ERROR));
            }
        }

    }

    @Override
    protected String getCss(Component c) {
        return "background: " + color.getCSS();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean visible) {
        field.setRequiredIndicatorVisible(visible);
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return field.isRequiredIndicatorVisible();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        updateColorComponents();
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    private void updateColorComponents() {
        iterator().forEachRemaining(this::updateColorComponents);
    }

    private void updateColorComponents(Component component) {
        if (component instanceof HasValue<?>) {
            ((HasValue<?>) component).setReadOnly(isReadOnly());
        }
        if (component instanceof HasComponents) {
            for (Component c : (HasComponents) component) {
                updateColorComponents(c);
            }
        }
    }

    private Color getHexPatternColor(Matcher m) {
        int red = Integer.parseInt(m.group("red"), 16);
        int green = Integer.parseInt(m.group("green"), 16);
        int blue = Integer.parseInt(m.group("blue"), 16);
        return new Color(red, green, blue);
    }

    private Color getRGBPatternColor(Matcher m) {
        int red = Integer.parseInt(m.group("red"));
        int green = Integer.parseInt(m.group("green"));
        int blue = Integer.parseInt(m.group("blue"));
        return new Color(red, green, blue);
    }

    private Color getHSLPatternColor(Matcher m) {
        int hue = Integer.parseInt(m.group("hue"));
        int saturation = Integer.parseInt(m.group("saturation"));
        int light = Integer.parseInt(m.group("light"));
        int rgb = Color.HSLtoRGB(hue, saturation, light);
        return new Color(rgb);
    }

    /**
     * Case-insensitive {@link Pattern} with regular expression matching the
     * default hexadecimal color presentation pattern:<br>
     * '#' followed by six <code>[\da-fA-F]</code> characters.
     * <p>
     * Pattern contains named groups <code>red</code>, <code>green</code>, and
     * <code>blue</code>, which represent the individual values.
     */
    protected static final Pattern HEX_PATTERN = Pattern.compile(
            "(?i)^#\\s*(?<red>[\\da-f]{2})(?<green>[\\da-f]{2})(?<blue>[\\da-f]{2}"
                    + ")\\s*$");
    /**
     * Case-insensitive {@link Pattern} with regular expression matching common
     * RGB color presentation patterns:<br>
     * 'rgb' followed by three [0-255] number values. Values can be separated
     * with either comma or whitespace.
     * <p>
     * Pattern contains named groups <code>red</code>, <code>green</code>, and
     * <code>blue</code>, which represent the individual values.
     */
    protected static final Pattern RGB_PATTERN = Pattern.compile(
            "(?i)^rgb\\(\\s*(?<red>[01]?\\d{1,2}|2[0-4]\\d|25[0-5])(?:\\s*[,+|\\"
                    + "s+]\\s*)(?<green>[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?:\\s*[,"
                    + "+|\\s+]\\s*)(?<blue>[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\s*\\"
                    + ")$");
    /**
     * Case-insensitive {@link Pattern} with regular expression matching common
     * RGBA presentation patterns:<br>
     * 'rgba' followed by three [0-255] values and one [0.0-1.0] value. Values
     * can be separated with either comma or whitespace. The only accepted
     * decimal marker is point ('.').
     * <p>
     * Pattern contains named groups <code>red</code>, <code>green</code>,
     * <code>blue</code>, and <code>alpha</code>, which represent the individual
     * values.
     */
    protected static final Pattern RGBA_PATTERN = Pattern.compile(
            "(?i)^rgba\\(\\s*(?<red>[01]?\\d{1,2}|2[0-4]\\d|25[0-5])(?:\\s*[,+|"
                    + "\\s+]\\s*)(?<green>[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?:\\s"
                    + "*[,+|\\s+]\\s*)(?<blue>[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?"
                    + ":\\s*[,+|\\s+]\\s*)(?<alpha>0(?:\\.\\d{1,2})?|0?(?:\\.\\"
                    + "d{1,2})|1(?:\\.0{1,2})?)\\s*\\)$");

    /**
     * Case-insensitive {@link Pattern} with regular expression matching common
     * HSL presentation patterns:<br>
     * 'hsl' followed by one [0-360] value and two [0-100] percentage value.
     * Values can be separated with either comma or whitespace. The percent sign
     * ('%') is optional.
     * <p>
     * Pattern contains named groups <code>hue</code>,<code>saturation</code>,
     * and <code>light</code>, which represent the individual values.
     */
    protected static final Pattern HSL_PATTERN = Pattern.compile(
            "(?i)hsl\\(\\s*(?<hue>[12]?\\d{1,2}|3[0-5]\\d|360)(?:\\s*[,+|\\s+]"
                    + "\\s*)(?<saturation>\\d{1,2}|100)(?:\\s*%?\\s*[,+|\\s+]\\"
                    + "s*)(?<light>\\d{1,2}|100)(?:\\s*%?\\s*)\\)$");

    /**
     * Case-insensitive {@link Pattern} with regular expression matching common
     * HSLA presentation patterns:<br>
     * 'hsla' followed by one [0-360] value, two [0-100] percentage values, and
     * one [0.0-1.0] value. Values can be separated with either comma or
     * whitespace. The percent sign ('%') is optional. The only accepted decimal
     * marker is point ('.').
     * <p>
     * Pattern contains named groups <code>hue</code>,<code>saturation</code>,
     * <code>light</code>, and <code>alpha</code>, which represent the
     * individual values.
     */
    protected static final Pattern HSLA_PATTERN = Pattern.compile(
            "(?i)hsla\\(\\s*(?<hue>[12]?\\d{1,2}|3[0-5]\\d|360)(?:\\s*[,+|\\s+"
                    + "]\\s*)(?<saturation>\\d{1,2}|100)(?:\\s*%?\\s*[,+|\\s+]\\s*"
                    + ")(?<light>\\d{1,2}|100)(?:\\s*%?[,+|\\s+]\\s*)(?<alpha>"
                    + "0(?:\\.\\d{1,2})?|0?(?:\\.\\d{1,2})|1(?:\\.0{1,2})?)"
                    + "\\s*\\)$");
}