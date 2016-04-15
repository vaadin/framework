package com.vaadin.tests.tickets;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public class Ticket2303 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow("main window");

        String customlayout = "<div location=\"test\"></div>";
        CustomLayout cl = null;
        try {
            cl = new CustomLayout(new ByteArrayInputStream(
                    customlayout.getBytes()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cl.setWidth("100%");
        w.setContent(cl);

        // VerticalLayout ol = new VerticalLayout();
        // w.setContent(ol);
        VerticalLayout hugeLayout = new VerticalLayout();
        hugeLayout.setMargin(true);
        hugeLayout.setSpacing(true);
        for (int i = 0; i < 30; i++) {
            hugeLayout.addComponent(new Label("huge " + i));
        }
        cl.addComponent(hugeLayout, "test");
        // ol.addComponent(hugeLayout);
        setMainWindow(w);
    }

}
