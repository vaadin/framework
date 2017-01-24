/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.AbstractColorPicker;
import com.vaadin.ui.AbstractColorPicker.PopupStyle;

/**
 * Abstract test class which contains tests for declarative format for
 * properties that are common for AbstractColorPicker.
 * <p>
 * It's an abstract so it's not supposed to be run as is. Instead each
 * declarative test for a real component should extend it and implement abstract
 * methods to be able to test the common properties. Components specific
 * properties should be tested additionally in the subclasses implementations.
 *
 * @author Vaadin Ltd
 *
 */
public abstract class AbstractColorPickerDeclarativeTest<T extends AbstractColorPicker>
        extends AbstractFieldDeclarativeTest<T, Color> {

    @Test
    public void testAllAbstractColorPickerFeatures()
            throws InstantiationException, IllegalAccessException {
        boolean defaultCaption = true;
        PopupStyle popupStyle = PopupStyle.POPUP_SIMPLE;
        int x = 79;
        int y = 91;
        boolean rgbVisibility = false;
        boolean hsvVisibility = false;
        boolean swatchesVisibility = true;
        boolean historyVisibility = false;
        boolean textFieldVisibility = false;
        String design = String.format(
                "<%s default-caption-enabled position='%s,%s'"
                        + " popup-style='%s' rgb-visibility='%s' hsv-visibility='%s' "
                        + "history-visibility='%s' textfield-visibility='%s'/>",
                getComponentTag(), x, y, popupStyle.toString(), rgbVisibility,
                hsvVisibility, historyVisibility, textFieldVisibility);

        T colorPicker = getComponentClass().newInstance();

        colorPicker.setDefaultCaptionEnabled(defaultCaption);
        colorPicker.setPosition(x, y);
        colorPicker.setPopupStyle(popupStyle);
        colorPicker.setRGBVisibility(rgbVisibility);
        colorPicker.setHSVVisibility(hsvVisibility);
        colorPicker.setSwatchesVisibility(swatchesVisibility);
        colorPicker.setHistoryVisibility(historyVisibility);
        colorPicker.setTextfieldVisibility(textFieldVisibility);

        testWrite(design, colorPicker);
        testRead(design, colorPicker);
    }

    @Override
    public void valueDeserialization()
            throws InstantiationException, IllegalAccessException {
        String rgb = "fafafa";
        String design = String.format("<%s color='#%s'/>", getComponentTag(),
                rgb);

        T colorPicker = getComponentClass().newInstance();
        int colorInt = Integer.parseInt(rgb, 16);

        colorPicker.setValue(new Color(colorInt));

        testWrite(design, colorPicker);
        testRead(design, colorPicker);
    }

    @Override
    public void readOnlyValue()
            throws InstantiationException, IllegalAccessException {
        String rgb = "fafafa";
        String design = String.format("<%s color='#%s' readonly/>",
                getComponentTag(), rgb);

        T colorPicker = getComponentClass().newInstance();
        int colorInt = Integer.parseInt(rgb, 16);

        colorPicker.setValue(new Color(colorInt));
        colorPicker.setReadOnly(true);

        testWrite(design, colorPicker);
        testRead(design, colorPicker);
    }

}
