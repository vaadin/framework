package com.vaadin.tests.components.colorpicker;

import com.vaadin.annotations.Widgetset;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractColorPicker;

import com.vaadin.ui.Button;
import com.vaadin.ui.ColorPicker;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ColorPickerValueTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        AbstractColorPicker colorpicker = new ColorPicker("ColorPicker",
                Color.GREEN);
        colorpicker.setPosition(250, 0);
        colorpicker.setId("clp");
        colorpicker.setRGBVisibility(true);
        colorpicker.setHSVVisibility(true);
        colorpicker.setTextfieldVisibility(true);
        colorpicker.setSwatchesVisibility(true);
        addComponent(colorpicker);
        addComponent(new Button("Change Color", e -> {
            colorpicker.setValue(Color.YELLOW);
        }));
    }
}
