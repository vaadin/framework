package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

@Push
public class PushFromInit extends AbstractTestUIWithLog {

    public static final String LOG_DURING_INIT = "Logged from access run before init ends";
    public static final String LOG_AFTER_INIT = "Logged from background thread run after init has finished";

    @Override
    protected void setup(VaadinRequest request) {
        log("Logged in init");
        Thread t = new Thread(new RunBeforeInitEnds());
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(new RunAfterInit()).start();
        addComponent(new Button("Sync"));
    }

    class RunBeforeInitEnds implements Runnable {
        @Override
        public void run() {
            access(() -> log(LOG_DURING_INIT));
        }
    }

    class RunAfterInit implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            access(() -> log(LOG_AFTER_INIT));
        }
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
