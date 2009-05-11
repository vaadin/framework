package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;

public class Ticket1444 extends Application {

    @Override
    public void init() {

        final Window mainWin = new Window(
                "Test app to break layout fuction in IE7");
        setMainWindow(mainWin);

        OrderedLayout ol = new OrderedLayout();
        ol.setHeight("250px");
        ol.setWidth("500px");

        Label red = new Label(
                "<div style='background:red;width:100%;height:100%;'>??</div>",
                Label.CONTENT_XHTML);
        red.setSizeFull();

        ol.addComponent(red);
        mainWin.addComponent(ol);

    }
}