package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class Ticket2042 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(1, 2);
        layout.setHeight("2000px");
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
