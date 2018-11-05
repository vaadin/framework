package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * @author Vaadin Ltd
 */
public class CustomVerticalSplitPanel extends VerticalSplitPanel {
    public CustomVerticalSplitPanel() {
        addComponent(new Label());
    }
}
