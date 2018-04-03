package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Vaadin Ltd
 */
public class CustomVerticalLayout extends VerticalLayout {
    public CustomVerticalLayout() {
        this.addComponent(new Label());
    }
}
