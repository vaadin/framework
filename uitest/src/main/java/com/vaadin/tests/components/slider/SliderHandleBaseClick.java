package com.vaadin.tests.components.slider;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Button;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class SliderHandleBaseClick extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Slider slider = new Slider();
        slider.setWidth(500, Unit.PIXELS);
        slider.setMin(0);
        slider.setMax(15);
        slider.setValue(3.0);
        slider.setEnableClickHandler(true);
        addComponent(slider);
        addComponent(new Button("Apply/remove click action", e -> {
            slider.setEnableClickHandler(!slider.isEnableClickHandler());
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Slider should update its value, when clicked on the base";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1496;
    }

}
