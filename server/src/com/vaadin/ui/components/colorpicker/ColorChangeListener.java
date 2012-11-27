package com.vaadin.ui.components.colorpicker;

import java.io.Serializable;

/**
 * The listener interface for receiving colorChange events. The class that is
 * interested in processing a {@link ColorChangeEvent} implements this
 * interface, and the object created with that class is registered with a
 * component using the component's <code>addColorChangeListener</code> method.
 * When the colorChange event occurs, that object's appropriate method is
 * invoked.
 * 
 * @since 7.0.0
 * 
 * @see ColorChangeEvent
 */
public interface ColorChangeListener extends Serializable {

    /**
     * Called when a new color has been selected.
     * 
     * @param event
     *            An event containing information about the color change.
     */
    void colorChanged(ColorChangeEvent event);

}