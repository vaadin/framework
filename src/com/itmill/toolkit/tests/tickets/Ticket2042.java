package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;
import com.itmill.toolkit.ui.Window.Notification;

public class Ticket2042 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(1, 2);
        layout.setHeight("2000");
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        layout.addComponent(new Label("abc"));
        layout.addComponent(new Button("B", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                Notification n = new Notification("Test");
                getMainWindow().showNotification(n);
            }

        }));

        layout.addComponent(new Label("abc"));
    }
}
