package com.vaadin.ui.components.colorpicker;

import java.io.Serializable;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * An interface for a color selector.
 * 
 * @since 7.0.0
 */
public interface ColorSelector extends Serializable, HasColorChangeListener {

    /**
     * Sets the color.
     * 
     * @param color
     *            the new color
     */
    public void setColor(Color color);

    /**
     * Gets the color.
     * 
     * @return the color
     */
    public Color getColor();
}