package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * @author Vaadin Ltd
 */
public class CustomPanel extends Panel {
    public CustomPanel() {
        setContent(new Label());
    }
}
