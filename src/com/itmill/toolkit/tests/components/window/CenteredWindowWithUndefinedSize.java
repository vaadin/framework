package com.itmill.toolkit.tests.components.window;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

public class CenteredWindowWithUndefinedSize extends TestBase {

    @Override
    protected String getDescription() {
        return "The centered sub-window with undefined height and a 100% high layout should be rendered in the center of the screen and not in the top-left corner.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2702;
    }

    @Override
    protected void setup() {
        Window centered = new Window("A window");
        centered.setSizeUndefined();
        centered.getLayout().setSizeFull();
        centered.center();

        Label l = new Label("This window should be centered");
        l.setSizeUndefined();
        centered.addComponent(l);

        getMainWindow().addWindow(centered);

    }
}
