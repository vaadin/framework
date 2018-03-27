/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import org.mockito.Mockito;

import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.AbstractColorPicker.Coordinates2Color;
import com.vaadin.ui.components.colorpicker.ColorPickerGradient;
import com.vaadin.ui.components.colorpicker.ColorPickerGrid;
import com.vaadin.ui.components.colorpicker.ColorPickerPopup;
import com.vaadin.ui.components.colorpicker.ColorPickerPreview;

/**
 * @author Vaadin Ltd
 *
 */
public class ColorPickerComponentsTest {

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_colorPickerGradientThrowsNPE() {
        ColorPickerGradient gradient = new ColorPickerGradient("foo",
                Mockito.mock(Coordinates2Color.class));
        gradient.setValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_colorPickerGridThrowsNPE() {
        ColorPickerGrid grid = new ColorPickerGrid();
        grid.setValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_colorPickerPopupThrowsNPE() {
        ColorPickerPopup popup = new ColorPickerPopup(Color.WHITE);
        popup.setValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_colorPickerPreviewThrowsNPE() {
        ColorPickerPreview preview = new ColorPickerPreview(Color.WHITE);
        preview.setValue(null);
    }
}
