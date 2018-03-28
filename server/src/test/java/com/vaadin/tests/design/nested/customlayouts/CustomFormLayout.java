package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

/**
 * @author Vaadin Ltd
 */
public class CustomFormLayout extends FormLayout {
    public CustomFormLayout() {
        this.addComponent(new Label());
    }
}
