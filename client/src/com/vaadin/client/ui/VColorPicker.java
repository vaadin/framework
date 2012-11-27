package com.vaadin.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;

/**
 * Client side implementation for ColorPicker.
 * 
 * @since 7.0.0
 */
public class VColorPicker extends VButton implements ClickHandler {

    private String color = null;

    private boolean isOpen = false;

    private HTML colorIcon;

    @Override
    public void onClick(ClickEvent event) {
        super.onClick(event);

        setOpen(!isOpen);
    }

    /**
     * Set the color of the component, e.g. #ffffff
     * 
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Mark the popup opened/closed.
     * 
     * @param open
     */
    public void setOpen(boolean open) {
        isOpen = open;
    }

    /**
     * Check the popup's marked state.
     * 
     * @return true if the popup has been marked being open, false otherwise.
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Update color icon to show the currently selected color.
     */
    public void refreshColor() {
        if (color != null) {

            if (colorIcon == null) {
                colorIcon = new HTML();
                colorIcon.setStylePrimaryName("v-colorpicker-button-color");
                wrapper.insertBefore(colorIcon.getElement(), captionElement);
            }

            // Set the color
            DOM.setStyleAttribute(colorIcon.getElement(), "background", color);

        }
    }

}
