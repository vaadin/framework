package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket1966_3 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        OrderedLayout ol = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
        Panel p = new Panel(ol);
        p.setWidth("300px");
        p.setHeight("300px");
        p.getLayout().setSizeFull();

        TextField tf = new TextField("Long caption, longer than 100 pixels");
        tf.setWidth("100px");

        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);

        tf = new TextField("Short caption");
        tf.setWidth("100px");

        tf.setComponentError(new UserError("error message"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);

        tf = new TextField("Short caption");
        tf.setComponentError(new UserError("error message"));
        tf.setIcon(new ThemeResource("icons/16/calendar.png"));
        tf.setWidth("100px");

        tf.setComponentError(new UserError("error message"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);

        tf = new TextField();
        tf.setValue("No caption");
        tf.setWidth("100px");

        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);

        layout.addComponent(p);
    }
}
