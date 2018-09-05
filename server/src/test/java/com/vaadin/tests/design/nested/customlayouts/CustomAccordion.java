package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;

/**
 * @author Vaadin Ltd
 */
public class CustomAccordion extends Accordion {
    public CustomAccordion() {
        addComponent(new Label());
    }
}
