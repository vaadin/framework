package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket1465ModalNotification extends Application {

    @Override
    public void init() {

        final Window mainWin = new Window("ButtonPanel containing a table test");
        setMainWindow(mainWin);

        final Window modal = new Window("Modal window");
        modal.setModal(true);

        Button b = new Button("click to show notification",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        modal.showNotification(
                                "Try clicking the button in main window!",
                                Window.Notification.TYPE_ERROR_MESSAGE);

                    }
                });
        modal.addComponent(b);

        b = new Button("click to warning notification",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        modal.showNotification(
                                "Try clicking the button in main window!",
                                Window.Notification.TYPE_WARNING_MESSAGE);
                    }
                });
        modal.addComponent(b);

        b = new Button("click to Humanized notification",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        modal.showNotification(
                                "Try clicking the button in main window!",
                                Window.Notification.TYPE_HUMANIZED_MESSAGE);
                    }
                });
        modal.addComponent(b);

        b = new Button("click to test modality!", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                mainWin.addComponent(new Label("clicked"));

            }
        });

        modal.addComponent(b);

        mainWin.addWindow(modal);

        b = new Button("click to test modality!", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                mainWin.addComponent(new Label("clicked"));

            }
        });

        mainWin.addComponent(b);

    }
}