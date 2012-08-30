package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Window;

public class FullSizedWindow extends TestBase {

    @Override
    protected void setup() {
        Window w = new Window("full sized window");
        w.setSizeFull();
        w.getContent().setSizeFull();
        NativeButton b = new NativeButton("A large button");
        b.setSizeFull();
        w.getContent().addComponent(b);
        getMainWindow().addWindow(w);
        setTheme("runo");
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
