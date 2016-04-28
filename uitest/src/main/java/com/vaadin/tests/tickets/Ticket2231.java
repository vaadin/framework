package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2231 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
        layout.setSizeUndefined();
        layout.setMargin(false);
        layout.setStyleName("borders");
        Label l = new Label("Margin-label");

        l.setStyleName("ticket2231");

        layout.addComponent(l);

        for (int i = 0; i < 5; i++) {
            l = new Label("This is a label with border");
            l.setStyleName("ticket2231-border");
            if (i == 2) {
                l.setWidth("100%");
                l.setValue("100% wide");
            } else if (i == 4) {
                l.setWidth("20em");
                l.setValue("20em wide");
            }
            // l.addStyleName("ticket2231");
            layout.addComponent(l);
        }
    }
}
