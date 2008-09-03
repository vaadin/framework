package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2007 extends Application {

    int childs = 0;

    @Override
    public void init() {

        final Window main = new Window("Main window for #2007");
        setMainWindow(main);
        main.addComponent(new Button("Open another (non-main) window",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Window c = new Window("Non-main browser window "
                                + (++childs));
                        addWindow(c);
                        main.open(new ExternalResource(c.getURL()), "_new");
                    }
                }));
    }
}
