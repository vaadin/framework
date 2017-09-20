package com.vaadin.tests.components.colorpicker;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.components.colorpicker.ColorPickerGrid;

/**
 * Tests index handling in ColorPickerGrid.
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class ColorPickerGridUI extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ColorPickerGrid colorPickerGrid = new ColorPickerGrid(
                new Color[][] { { Color.RED, Color.GREEN, Color.BLUE } });
        colorPickerGrid.setWidth("300px");
        colorPickerGrid.setHeight("100px");
        addComponent(colorPickerGrid);
    }

    @Override
    protected String getTestDescription() {
        return "Click on the second color and check that no exceptions are thrown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9018;
    }

}
