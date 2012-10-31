package com.vaadin.tests.tickets;

import com.vaadin.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

public class Ticket1465ModalNotification extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow(
                "ButtonPanel containing a table test");
        setMainWindow(mainWin);

        final Window modal = new Window("Modal window");
        modal.setModal(true);

        Button b = new Button("click to show notification",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification.show(
                                "Try clicking the button in main window!",
                                Notification.TYPE_ERROR_MESSAGE);

                    }
                });
        modal.addComponent(b);

        b = new Button("click to warning notification",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification.show(
                                "Try clicking the button in main window!",
                                Notification.TYPE_WARNING_MESSAGE);
                    }
                });
        modal.addComponent(b);

        b = new Button("click to Humanized notification",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification.show(
                                "Try clicking the button in main window!",
                                Notification.TYPE_HUMANIZED_MESSAGE);
                    }
                });
        modal.addComponent(b);

        b = new Button("click to test modality!", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                mainWin.addComponent(new Label("clicked"));

            }
        });

        modal.addComponent(b);

        mainWin.addWindow(modal);

        b = new Button("click to test modality!", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                mainWin.addComponent(new Label("clicked"));

            }
        });

        mainWin.addComponent(b);

    }
}