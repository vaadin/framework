package com.vaadin.shared.ui.colorpicker;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;

/**
 * Default shared state implementation for AbstractColorPicker.
 * 
 * @since 7.0.0
 */
public class ColorPickerState extends AbstractComponentState {
    {
        primaryStyleName = "v-colorpicker";
    }

    @DelegateToWidget("setOpen")
    public boolean popupVisible = false;

    @DelegateToWidget("setColor")
    public String color = null;

    public boolean showDefaultCaption;

    public boolean htmlContentAllowed;
}
