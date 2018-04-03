package com.vaadin.tests.design.nested.customlayouts;

import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;

/**
 * @author Vaadin Ltd
 */
public class CustomHorizontalSplitPanel extends HorizontalSplitPanel {
    public CustomHorizontalSplitPanel() {
        addComponent(new Label());
    }
}
