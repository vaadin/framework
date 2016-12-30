package com.vaadin.tests.elements.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelScroll extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Panel panel = new Panel();
        panel.setId("mainPanel");
        panel.setWidth("200px");
        panel.setHeight("200px");
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("500px");
        layout.setHeight("500px");
        Button btn = new Button("Button 1");
        Button btn2 = new Button("Button 2");
        layout.addComponent(btn);
        layout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        layout.addComponent(btn2);
        layout.setComponentAlignment(btn2, Alignment.BOTTOM_LEFT);
        panel.setContent(layout);
        addComponent(panel);
    }

    @Override
    protected String getTestDescription() {
        return "Test scroll left and scroll right methods of PanelElement";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14819;
    }
}
