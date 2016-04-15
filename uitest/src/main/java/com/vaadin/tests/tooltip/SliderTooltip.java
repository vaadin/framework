package com.vaadin.tests.tooltip;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Slider;

public class SliderTooltip extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        Slider slider = new Slider();
        slider.setDescription("Tooltip");
        addComponent(slider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Testing that sliders have tooltips.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14019;
    }

}
