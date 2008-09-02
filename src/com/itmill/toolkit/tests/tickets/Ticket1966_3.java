package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket1966_3 extends Application {

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
        p.setWidth("300");
        p.setHeight("300");
        p.getLayout().setSizeFull();

        TextField tf = new TextField("Long caption, longer than 100 pixels");
        tf.setWidth("100");

        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);

        tf = new TextField("Short caption");
        tf.setWidth("100");

        tf.setComponentError(new UserError("error message"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);

        tf = new TextField("Short caption");
        tf.setComponentError(new UserError("error message"));
        tf.setIcon(new ThemeResource("icons/16/calendar.png"));
        tf.setWidth("100");

        tf.setComponentError(new UserError("error message"));
        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);

        tf = new TextField();
        tf.setValue("No caption");
        tf.setWidth("100");

        ol.addComponent(tf);
        ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_RIGHT,
                OrderedLayout.ALIGNMENT_TOP);

        layout.addComponent(p);
    }
}
