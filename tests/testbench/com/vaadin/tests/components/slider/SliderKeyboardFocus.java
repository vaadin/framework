package com.vaadin.tests.components.slider;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Slider;

public class SliderKeyboardFocus extends TestBase {

    @Override
    protected void setup() {
        Slider slider = new Slider("The slider", 0, 100);
        slider.setWidth("300px");
        addComponent(slider);

    }

    @Override
    protected String getDescription() {
        return "Tests keyboard navigation with the slider";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5329;
    }

}
