package com.vaadin.tests.push;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

public class PushFromInit extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        new Thread() {
            @Override
            public void run() {
                access(new Runnable() {
                    @Override
                    public void run() {
                        log("Logged from background thread started in init");
                    }
                });
            }
        }.start();
        log("Logged in init");
        addComponent(new Button("Sync"));
    }

    @Override
    protected String getTestDescription() {
        return "Pusing something to a newly created UI should not cause race conditions";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11529);
    }

}
