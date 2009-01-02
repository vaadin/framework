package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2271 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {

        VerticalLayout ol = new VerticalLayout();
        ol.setWidth(null);

        ComboBox cb = new ComboBox("Asiakas");
        cb.setWidth("100%");

        Button b = new Button("View CSV-tiedostoon");

        ol.addComponent(cb);
        ol.addComponent(b);

        layout.addComponent(ol);
    }
}
