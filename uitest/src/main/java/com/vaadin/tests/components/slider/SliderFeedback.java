package com.vaadin.tests.components.slider;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Slider;

public class SliderFeedback extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Slider slider = new Slider(0, 5);
        slider.setWidth(800, Unit.PIXELS);
        slider.setMin(0);
        slider.setMax(1e12);
        addComponent(slider);
    }

    @Override
    protected String getTestDescription() {
        return "Slider feedback popup should display the correct value";
    }

    @Override
    protected Integer getTicketNumber() {
        return 18192;
    }

}
