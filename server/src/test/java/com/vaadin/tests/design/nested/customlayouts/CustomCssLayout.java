package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * @author Vaadin Ltd
 */
public class CustomCssLayout extends CssLayout {
    public CustomCssLayout() {
        this.addComponent(new Label());
    }
}
