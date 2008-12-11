package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.Window;

public class Ticket2323 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        Window subWindow = new Window("");
        subWindow.setSizeUndefined();
        subWindow.getLayout().setSizeUndefined();
        subWindow.center();
        subWindow.addComponent(new RichTextArea());
        w.addWindow(subWindow);
    }

}
