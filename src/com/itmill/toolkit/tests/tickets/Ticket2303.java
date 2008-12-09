package com.itmill.toolkit.tests.tickets;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2303 extends Application {

    @Override
    public void init() {
        Window w = new Window("main window");

        String customlayout = "<div location=\"test\"></div>";
        CustomLayout cl = null;
        try {
            cl = new CustomLayout(new ByteArrayInputStream(customlayout
                    .getBytes()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cl.setWidth("100%");
        w.setLayout(cl);

        // VerticalLayout ol = new VerticalLayout();
        // w.setLayout(ol);
        OrderedLayout hugeLayout = new OrderedLayout();
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
