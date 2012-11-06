package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket2325 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow("Testing....");
        setMainWindow(main);

        final VerticalLayout lo = new VerticalLayout();
        lo.setSizeUndefined();
        lo.setWidth("100%");
        TextArea tf = new TextArea();
        tf.setValue("The textfield should fill the window."
                + "\n - Try to resize window\n - Try to push REdo button");
        tf.setRows(10);
        tf.setWidth("100%");
        lo.addComponent(tf);
        Window subWin = new Window(
                "This window should initially be as wide as the caption", lo);
        main.addWindow(subWin);
        // subWin.setWidth("500px");
    }
}
