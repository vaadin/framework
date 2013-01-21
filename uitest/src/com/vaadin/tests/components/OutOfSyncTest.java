package com.vaadin.tests.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;

public class OutOfSyncTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Click me after 1s to be out of sync");
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("This code will never be reached");
            }
        });
        setContent(b);
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Remove button but prevent repaint -> causes out of sync
                // issues
                getSession().lock();
                try {
                    setContent(null);
                    getConnectorTracker().markClean(OutOfSyncTest.this);
                } finally {
                    getSession().unlock();
                }
            }
        });
        t.start();
    }

    @Override
    protected String getTestDescription() {
        return "Click the button after 1s when it has been removed server side (causing synchronization problems)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10780;
    }

}
