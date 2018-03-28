package com.vaadin.tests.server.component.colorpicker;

import com.vaadin.ui.ColorPickerArea;

/**
 * Declarative test for ColorPickerArea. Provides only information about
 * ColorPickerArea class. All tests are in the superclass.
 *
 * @author Vaadin Ltd
 *
 */
public class ColorPickerAreaDeclarativeTest
        extends AbstractColorPickerDeclarativeTest<ColorPickerArea> {

    @Override
    protected String getComponentTag() {
        return "vaadin-color-picker-area";
    }

    @Override
    protected Class<ColorPickerArea> getComponentClass() {
        return ColorPickerArea.class;
    }

}
