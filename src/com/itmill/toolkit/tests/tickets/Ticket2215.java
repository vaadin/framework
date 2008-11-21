package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;

public class Ticket2215 extends Application {

    @Override
    public void init() {
        setMainWindow(new Window());

        OrderedLayout ol = new OrderedLayout();
        Panel p = new Panel("Test");
        p.addComponent(new Label("Panel1"));
        p.setHeight("500px");
        p.setWidth("500px");
        p.setStyleName(Panel.STYLE_LIGHT);
        ol.addComponent(p);
        ol.addComponent(new Label("NextComponent"));

        getMainWindow().addComponent(ol);

    }

}
