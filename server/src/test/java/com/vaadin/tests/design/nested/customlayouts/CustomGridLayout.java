package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

/**
 * @author Vaadin Ltd
 */
public class CustomGridLayout extends GridLayout {
    public CustomGridLayout() {
        this.addComponent(new Label());
    }
}
