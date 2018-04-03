package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

/**
 * @author Vaadin Ltd
 */
public class CustomTabSheet extends TabSheet {
    public CustomTabSheet() {
        addComponent(new Label());
    }
}
