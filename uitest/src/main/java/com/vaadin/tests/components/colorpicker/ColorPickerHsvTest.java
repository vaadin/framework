package com.vaadin.tests.components.colorpicker;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ColorPickerArea;

/**
 * Tests the HSV tab slider values when initially opening the tab.
 */
public class ColorPickerHsvTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ColorPickerArea colorpicker = new ColorPickerArea();
        colorpicker.setValue(new Color(Integer.parseInt("00b4f0", 16)));
        colorpicker.setDefaultCaptionEnabled(false);
        colorpicker.setId("colorpicker");
        addComponent(colorpicker);
    }

    @Override
    protected String getTestDescription() {
        return "Tests the slider values when initially opening the HSV tab.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7863;
    }

}
