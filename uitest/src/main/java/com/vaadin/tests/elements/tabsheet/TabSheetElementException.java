package com.vaadin.tests.elements.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TabSheet;

/**
 * this UI is used for testing that an exception occurs when TestBench attempts
 * to open a tab that does not exist.
 */
public class TabSheetElementException extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet ts = new TabSheet();
        for (int i = 1; i <= 5; i++) {
            ts.addTab(new CssLayout(), "Tab " + i);
        }
        addComponent(ts);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that an exception is thrown when TestBench attempts to"
                + " click a tab that does not exist.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13734;
    }
}
