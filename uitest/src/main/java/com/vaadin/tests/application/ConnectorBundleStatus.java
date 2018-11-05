package com.vaadin.tests.application;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.ConnectorBundleStatusDisplay;
import com.vaadin.ui.Button;
import com.vaadin.ui.RichTextArea;

@Widgetset(TestingWidgetSet.NAME)
public class ConnectorBundleStatus extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ConnectorBundleStatusDisplay statusDisplay = new ConnectorBundleStatusDisplay();
        statusDisplay.setId("bundleStatus");

        Button refreshButton = new Button("Refresh status",
                event -> statusDisplay.updateStatus());
        refreshButton.setId("refresh");

        Button rtaButton = new Button("Add RichTextArea (in the lazy bundle)",
                event -> addComponent(new RichTextArea()));
        rtaButton.setId("rta");

        addComponents(statusDisplay, refreshButton, rtaButton);
    }

}
