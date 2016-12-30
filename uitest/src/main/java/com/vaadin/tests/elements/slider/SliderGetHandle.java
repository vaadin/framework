package com.vaadin.tests.elements.slider;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Slider;

public class SliderGetHandle extends AbstractTestUI {

    public static final double INITIAL_VALUE = 10.0;

    @Override
    protected void setup(VaadinRequest request) {
        Slider sl1 = new Slider();
        Slider sl2 = new Slider();
        sl2.setValue(INITIAL_VALUE);
        sl1.setWidth("50px");
        sl2.setWidth("50px");
        addComponent(sl1);
        addComponent(sl2);
    }

}
