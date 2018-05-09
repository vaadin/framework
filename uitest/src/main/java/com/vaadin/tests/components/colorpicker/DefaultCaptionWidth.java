package com.vaadin.tests.components.colorpicker;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ColorPicker;

/**
 * Test for color picker with default caption.
 *
 * @author Vaadin Ltd
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class DefaultCaptionWidth extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final ColorPicker colorPicker = new ColorPicker();
        addComponent(colorPicker);
        colorPicker.setDefaultCaptionEnabled(true);

        Button setWidth = new Button("Set explicit width", event -> {
            colorPicker.setCaption(null);
            colorPicker.setWidth("150px");
        });
        setWidth.addStyleName("set-width");
        addComponent(setWidth);

        Button setCaption = new Button("Set explicit caption", event -> {
            colorPicker.setCaption("caption");
            colorPicker.setWidthUndefined();
        });
        setCaption.addStyleName("set-caption");
        addComponent(setCaption);
    }

    @Override
    protected String getTestDescription() {
        return "Color picker with default caption enabled should get appropriate style";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17140;
    }
}
