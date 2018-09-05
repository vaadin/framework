package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class VetoTabChange extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TabSheet ts = new TabSheet();
        ts.addSelectedTabChangeListener(event -> ts.setSelectedTab(0));

        ts.addTab(new Label("Tab 1"), "Tab 1");
        ts.addTab(new Label("Tab 2"), "Tab 2");

        addComponent(ts);
    }

    @Override
    protected String getTestDescription() {
        return "Tests the behavior when there's a listener that always changes back to the first tab.";
    }

}
