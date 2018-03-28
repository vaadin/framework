package com.vaadin.tests.server.component.colorpicker;

import com.vaadin.ui.ColorPicker;

/**
 * Declarative test for ColorPicker. Provides only information about
 * ColorPickerArea class. All tests are in the superclass.
 *
 * @author Vaadin Ltd
 *
 */
public class ColorPickerDeclarativeTest
        extends AbstractColorPickerDeclarativeTest<ColorPicker> {

    @Override
    protected String getComponentTag() {
        return "vaadin-color-picker";
    }

    @Override
    protected Class<ColorPicker> getComponentClass() {
        return ColorPicker.class;
    }

}
