/*
 * Copyright 2000-2022 Vaadin Ltd.
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
    private static final Logger LOGGER = Logger
            .getLogger(ColorPickerPreview.class.getName());

    private static final String STYLE_DARK_COLOR = "v-textfield-dark";
    private static final String STYLE_LIGHT_COLOR = "v-textfield-light";

    /** The color. */
    private Color color;

    /** The field. */
    private final TextField field;

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
    @SuppressWarnings("deprecation")
    public Registration addValueChangeListener(
            ValueChangeListener<Color> listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        return addListener(ValueChangeEvent.class, listener,
                ValueChangeListener.VALUE_CHANGE_METHOD);
    }

    private void valueChange(ValueChangeEvent<String> event) {
        ErrorMessage errorMessage = null;
        String value = event.getValue();
        value = Objects.toString(value, "").trim();
        Color oldColor = color;
        try {

            /*
             * Description of supported formats see
             * http://www.w3schools.com/cssref/css_colors_legal.asp
             */
            color = ColorUtil.stringToColor(value);

            fireEvent(new ValueChangeEvent<>(this, oldColor,
                    event.isUserOriginated()));
        } catch (NumberFormatException e) {
            // Pattern matching ensures the validity of
            // the input, this should never happen
            LOGGER.log(Level.INFO, e.getMessage());
            errorMessage = new UserError(getUserErrorText(value),
                    ContentMode.TEXT, ErrorLevel.WARNING);
        }
        field.setComponentError(errorMessage);
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

    /**
     * Get the client error message text for color input parsing error.
     *
     * @param value
     *            input which caused the error
     * @return error message text
     * @since 8.4
     */
    protected String getUserErrorText(String value) {
        return value.isEmpty() ? "Input cannot be empty"
                : "Input '".concat(value)
                        .concat("' is not in any recognized format");
    }

}
