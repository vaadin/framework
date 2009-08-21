package com.example.dialogtest;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SubwindowInvalidLayout extends TestBase {

    @Override
    protected String getDescription() {
        return "The subwindow contains an invalid layout, which analyze layouts should detect.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3096;
    }

    @Override
    protected void setup() {
        Window window = new Window("Sub window");
        window.center();

        VerticalLayout vl = new VerticalLayout();
        vl.setWidth(null);
        Button b = new Button("A 100% wide button, invalid");
        b.setWidth("100%");
        vl.addComponent(b);
        window.addComponent(vl);

        getMainWindow().addWindow(window);
    }

}
