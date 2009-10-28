package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * With IE7 extra scrollbars appear in content area all though content fits
 * properly. Scrollbars will disappear if "shaking" content a bit, like
 * selecting tests in area.
 */
public class Ticket2901 extends Application {

    @Override
    public void init() {

        final Window mainWin = new Window("Test app to break layout in IE6");
        setMainWindow(mainWin);

        SplitPanel sp = new SplitPanel();

        VerticalLayout l = new VerticalLayout();
        for (int i = 0; i < 100; i++) {
            l.addComponent(new Label("Label" + i));
        }
        sp.setFirstComponent(l);

        mainWin.setContent(sp);

    }
}