package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public class Ticket1904 extends LegacyApplication {

    @Override
    public void init() {
        setMainWindow(new LegacyWindow("#1904"));
        setTheme("tests-tickets");

        addOL("defaults", null, false);
        addOL("l5,r10,t20,b40,vs20,hs40", "ticket1904", false);
        addOL("l5,r10,t20,b40,vs20,hs40", "ticket1904", true);
    }

    private void addOL(String descr, String style, boolean horizontal) {
        AbstractOrderedLayout ol;
        if (horizontal) {
            ol = new HorizontalLayout();
        } else {
            ol = new VerticalLayout();
        }
        ol.setMargin(true);
        ol.setSpacing(true);
        if (style != null) {
            ol.setStyleName(style);
        }
        ol.addComponent(new Label(descr));
        for (int i = 0; i < 3; i++) {
            Button b = new Button("Row " + (i + 1));
            if (!horizontal) {
                b.setWidth("500px");
            }
            ol.addComponent(b);
        }
        getMainWindow().addComponent(ol);
    }

}
