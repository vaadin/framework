package com.vaadin.tests.components.colorpicker;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractColorPicker;
import com.vaadin.ui.Button;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.Label;

public class ColorPickerUserColorChange extends AbstractTestUI {

    public static final String value = "User originated: ";

    @Override
    protected void setup(VaadinRequest request) {
        AbstractColorPicker colorpicker = new ColorPicker("ColorPicker",
                Color.GREEN);
        Label isUserOriginated = new Label(value);
        isUserOriginated.setId("labelValue");
        colorpicker.setPosition(250, 0);
        colorpicker.setId("clp");
        colorpicker.setRGBVisibility(true);
        colorpicker.setHSVVisibility(true);
        colorpicker.setTextfieldVisibility(true);
        colorpicker.setSwatchesVisibility(true);
        colorpicker.addValueChangeListener(event -> {
            isUserOriginated.setValue(value + event.isUserOriginated());
        });
        addComponent(isUserOriginated);
        addComponent(colorpicker);
        Button changeColor = new Button("Change Color", e -> {
            colorpicker.setValue(Color.YELLOW);
        });
        changeColor.setId("changeColor");
        addComponent(changeColor);
    }
}
