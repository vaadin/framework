package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class UITabIndex extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Set tabIndex to -1", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTabIndex(-1);
            }
        });
        addButton("Set tabIndex to 0", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTabIndex(0);
            }
        });
        addButton("Set tabIndex to 1", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTabIndex(1);
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Tests tab index handling for UI";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11129;
    }

}
