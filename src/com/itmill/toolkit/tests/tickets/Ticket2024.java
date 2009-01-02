package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket2024 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(2, 2);
        layout.setHeight("100%");
        layout.setWidth("700");
        w.getLayout().setSizeFull();
        w.getLayout().setHeight("2000");
        w.getLayout().addComponent(layout);

        layout.addComponent(new Label(
                "This should NOT get stuck when scrolling down"));
        layout
                .addComponent(new TextField(
                        "This should not get stuck either..."));

        OrderedLayout ol = new OrderedLayout();
        ol.setHeight("1000");
        ol.setWidth("200");
        w.getLayout().addComponent(ol);
        ol.addComponent(new Label("Just a label to enable the scrollbar"));

    }
}
