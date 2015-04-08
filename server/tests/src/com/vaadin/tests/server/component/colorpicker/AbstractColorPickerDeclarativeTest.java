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
package com.vaadin.tests.server.component.colorpicker;

import org.junit.Test;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractColorPicker;
import com.vaadin.ui.AbstractColorPicker.PopupStyle;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.ColorPickerArea;

public class AbstractColorPickerDeclarativeTest extends
        DeclarativeTestBase<AbstractColorPicker> {

    @Test
    public void testAllAbstractColorPickerFeatures() {
        String design = "<v-color-picker color='#fafafa' default-caption-enabled='true' position='100,100'"
                + " popup-style='simple' rgb-visibility='false' hsv-visibility='false'"
                + " history-visibility=false textfield-visibility=false />";
        ColorPicker colorPicker = new ColorPicker();
        int colorInt = Integer.parseInt("fafafa", 16);
        colorPicker.setColor(new Color(colorInt));
        colorPicker.setDefaultCaptionEnabled(true);
        colorPicker.setPosition(100, 100);
        colorPicker.setPopupStyle(PopupStyle.POPUP_SIMPLE);
        colorPicker.setRGBVisibility(false);
        colorPicker.setHSVVisibility(false);
        colorPicker.setSwatchesVisibility(true);
        colorPicker.setHistoryVisibility(false);
        colorPicker.setTextfieldVisibility(false);

        testWrite(design, colorPicker);
        testRead(design, colorPicker);
    }

    @Test
    public void testEmptyColorPicker() {
        String design = "<v-color-picker />";
        ColorPicker colorPicker = new ColorPicker();
        testRead(design, colorPicker);
        testWrite(design, colorPicker);
    }

    @Test
    public void testAllAbstractColorPickerAreaFeatures() {
        String design = "<v-color-picker-area color='#fafafa' default-caption-enabled='true' position='100,100'"
                + " popup-style='simple' rgb-visibility='false' hsv-visibility='false'"
                + " history-visibility=false textfield-visibility=false />";
        AbstractColorPicker colorPicker = new ColorPickerArea();
        int colorInt = Integer.parseInt("fafafa", 16);
        colorPicker.setColor(new Color(colorInt));
        colorPicker.setDefaultCaptionEnabled(true);
        colorPicker.setPosition(100, 100);
        colorPicker.setPopupStyle(PopupStyle.POPUP_SIMPLE);
        colorPicker.setRGBVisibility(false);
        colorPicker.setHSVVisibility(false);
        colorPicker.setSwatchesVisibility(true);
        colorPicker.setHistoryVisibility(false);
        colorPicker.setTextfieldVisibility(false);

        testWrite(design, colorPicker);
        testRead(design, colorPicker);
    }

    @Test
    public void testEmptyColorPickerArea() {
        String design = "<v-color-picker-area />";
        AbstractColorPicker colorPicker = new ColorPickerArea();
        testRead(design, colorPicker);
        testWrite(design, colorPicker);
    }
}
