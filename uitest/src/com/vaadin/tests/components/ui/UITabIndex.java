package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class UITabIndex extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button b;

        b = new Button("Set tabIndex to -1");
        b.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTabIndex(-1);
            }
        });
        addComponent(b);
        b = new Button("Set tabIndex to 0");
        b.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTabIndex(0);
            }
        });
        addComponent(b);
        b = new Button("Set tabIndex to 1");
        b.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setTabIndex(1);
            }
        });
        addComponent(b);
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
