package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;

/**
 * @author Vaadin Ltd
 */
public class CustomAbsoluteLayout extends AbsoluteLayout {
    public CustomAbsoluteLayout() {
        this.addComponent(new Label());
    }
}
