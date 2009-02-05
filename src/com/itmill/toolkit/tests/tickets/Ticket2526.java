package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2526 extends Application {

    @Override
    public void init() {
        final Window main = new Window();
        setMainWindow(main);
        Button b = new Button("Add windows");
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                main.addWindow(new Window());
            }
        });
        main.addComponent(b);
    }
}
