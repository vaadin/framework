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
 * A class that defines area-like implementation for a color picker component.
 * 
 * @since 7.0.0
 * 
 * @see ColorPicker
 * 
 */
public class ColorPickerArea extends AbstractColorPicker {

    /**
     * Instantiates a new color picker.
     */
    public ColorPickerArea() {
        super();
    }

    /**
     * Instantiates a new color picker.
     * 
     * @param popupCaption
     *            caption of the color select popup
     */
    public ColorPickerArea(String popupCaption) {
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
    public ColorPickerArea(String popupCaption, Color initialColor) {
        super(popupCaption, initialColor);
        setDefaultCaptionEnabled(false);
    }

    @Override
    protected void setDefaultStyles() {
        // state already has correct default
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        if ("".equals(getState().height)) {
            getState().height = "30px";
        }
        if ("".equals(getState().width)) {
            getState().width = "30px";
        }
    }

}
