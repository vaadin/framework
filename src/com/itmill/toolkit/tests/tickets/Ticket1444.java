package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;

public class Ticket1444 extends Application {

    public void init() {

        final Window mainWin = new Window(
                "Test app to break label render fuction in IE7");
        setMainWindow(mainWin);

        OrderedLayout ol = new OrderedLayout();
        ol.setHeight("250px");
        ol.setWidth("500px");

        Panel p = new Panel();
        p.setSizeFull();
        p.getLayout().setMargin(false);
        p.getLayout().setSizeFull();

        Label red = new Label(
                "<div style='background:red;width:100%;height:100%;'>I should use whole panel content area.</div>",
                Label.CONTENT_XHTML);
        red.setSizeFull();

        p.addComponent(red);
        ol.addComponent(p);
        mainWin.addComponent(ol);

    }
}