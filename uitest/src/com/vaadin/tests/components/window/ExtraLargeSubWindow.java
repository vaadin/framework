package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Window;

public class ExtraLargeSubWindow extends TestBase {

    @Override
    protected void setup() {
        Window w = new Window("full sized window");
        w.setWidth("2000px");
        w.setHeight("2000px");
        w.getContent().setSizeFull();
        NativeButton b = new NativeButton("A large button");
        b.setSizeFull();
        w.addComponent(b);

        getMainWindow().addWindow(w);
    }

    @Override
    protected String getDescription() {
        return "A 100%x100% sub window should not produce scrollbars in the main view or in the sub window. The button inside the sub window is 100%x100%, as is the layout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3407;
    }

}
