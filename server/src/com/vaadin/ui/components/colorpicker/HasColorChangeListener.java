package com.vaadin.ui.components.colorpicker;

public interface HasColorChangeListener {

    /**
     * Adds a {@link ColorChangeListener} to the component.
     * 
     * @param listener
     */
    void addColorChangeListener(ColorChangeListener listener);

    /**
     * Removes a {@link ColorChangeListener} from the component.
     * 
     * @param listener
     */
    void removeColorChangeListener(ColorChangeListener listener);

}