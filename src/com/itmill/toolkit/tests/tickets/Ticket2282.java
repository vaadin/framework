package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.FormLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

public class Ticket2282 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        w.getLayout().setSizeUndefined();

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeUndefined();
        formLayout.setStyleName("borders");
        formLayout
                .addComponent(new Label(
                        "This should not be wider than this label + reserved error space"));
        w.addComponent(formLayout);
    }

}
