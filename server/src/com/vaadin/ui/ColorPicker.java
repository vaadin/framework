package com.vaadin.ui;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * A class that defines default (button-like) implementation for a color picker
 * component.
 * 
 * @since 7.0.0
 * 
 * @see ColorPickerArea
 * 
 */
public class ColorPicker extends AbstractColorPicker {

    /**
     * Instantiates a new color picker.
     */
    public ColorPicker() {
        super();
    }

    /**
     * Instantiates a new color picker.
     * 
     * @param popupCaption
     *            caption of the color select popup
     */
    public ColorPicker(String popupCaption) {
        super(popupCaption);
    }

    /**
     * Instantiates a new color picker.
     * 
     * @param popupCaption
     *            caption of the color select popup
     * @param initialColor
     *            the initial color
     */
    public ColorPicker(String popupCaption, Color initialColor) {
        super(popupCaption, initialColor);
        setDefaultCaptionEnabled(true);
    }

    @Override
    protected void setDefaultStyles() {
        setPrimaryStyleName(STYLENAME_BUTTON);
        addStyleName(STYLENAME_DEFAULT);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        if (isDefaultCaptionEnabled()
                && ((getState().caption == null || ""
                        .equals(getState().caption)))
                && "".equals(getState().width)) {
            getState().width = "100px";
        }
    }

}
