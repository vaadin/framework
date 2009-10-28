package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket2271 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {

        VerticalLayout ol = new VerticalLayout();
        ol.setWidth(null);

        ComboBox cb = new ComboBox("Asiakas");
        cb.setWidth("100%");

        Button b = new Button("View CSV-tiedostoon");

        ol.addComponent(cb);
        ol.addComponent(b);

        layout.addComponent(ol);
    }
}
