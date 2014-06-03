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
