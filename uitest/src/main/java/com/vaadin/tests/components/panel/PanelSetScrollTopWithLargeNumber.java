package com.vaadin.tests.components.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelSetScrollTopWithLargeNumber extends AbstractTestUI {

    Panel panel = new Panel();

    @Override
    public String getDescription() {
        return "Click the button to scroll down " + Integer.MAX_VALUE
                + " pixels";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1149;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        panel.setHeight("500px");
        String s = "";
        for (int i = 0; i < 10000; i++) {
            s += i + "<br />";
        }
        Label label = new Label(s, ContentMode.HTML);
        layout.addComponent(label);
        panel.setContent(layout);
        panel.setScrollTop(Integer.MAX_VALUE);
        addComponent(panel);
    }

}
