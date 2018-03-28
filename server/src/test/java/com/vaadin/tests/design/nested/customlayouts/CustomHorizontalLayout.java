package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * @author Vaadin Ltd
 */
public class CustomHorizontalLayout extends HorizontalLayout {
    public CustomHorizontalLayout() {
        this.addComponent(new Label());
    }
}
