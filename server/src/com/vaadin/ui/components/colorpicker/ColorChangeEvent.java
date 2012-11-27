package com.vaadin.ui.components.colorpicker;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

/**
 * The color changed event which is passed to the listeners when a color change
 * occurs.
 * 
 * @since 7.0.0
 */
public class ColorChangeEvent extends Event {
    private final Color color;

    public ColorChangeEvent(Component source, Color color) {
        super(source);

        this.color = color;
    }

    /**
     * Returns the new color.
     */
    public Color getColor() {
        return color;
    }
}