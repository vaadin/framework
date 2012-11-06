package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Notification;

public class Ticket2042 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(1, 2);
        layout.setHeight("2000px");
        w.setContent(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        layout.addComponent(new Label("abc"));
        layout.addComponent(new Button("B", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Notification n = new Notification("Test");
                getMainWindow().showNotification(n);
            }

        }));

        layout.addComponent(new Label("abc"));
    }
}
