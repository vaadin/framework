package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * With IE7 extra scrollbars appear in content area all though content fits
 * properly. Scrollbars will disappear if "shaking" content a bit, like
 * selecting tests in area.
 */
public class Ticket2901 extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow(
                "Test app to break layout in IE6");
        setMainWindow(mainWin);

        VerticalSplitPanel sp = new VerticalSplitPanel();

        VerticalLayout l = new VerticalLayout();
        for (int i = 0; i < 100; i++) {
            l.addComponent(new Label("Label" + i));
        }
        sp.setFirstComponent(l);

        mainWin.setContent(sp);

    }
}
