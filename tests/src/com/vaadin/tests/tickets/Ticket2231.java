package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;

public class Ticket2231 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
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
