package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket1966_3 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setContent(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        VerticalLayout ol = new VerticalLayout();
        Panel p = new Panel(ol);
        p.setWidth("300px");
        p.setHeight("300px");
        ol.setSizeFull();

        TextField tf = new TextField("Long caption, longer than 100 pixels");
        tf.setWidth("100px");

        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_RIGHT);

        tf = new TextField("Short caption");
        tf.setWidth("100px");

        tf.setComponentError(new UserError("error message"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_RIGHT);

        tf = new TextField("Short caption");
        tf.setComponentError(new UserError("error message"));
        tf.setIcon(new ThemeResource("icons/16/calendar.png"));
        tf.setWidth("100px");

        tf.setComponentError(new UserError("error message"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_RIGHT);

        tf = new TextField();
        tf.setValue("No caption");
        tf.setWidth("100px");

        ol.addComponent(tf);
        ol.setComponentAlignment(tf, Alignment.TOP_RIGHT);

        layout.addComponent(p);
    }
}
