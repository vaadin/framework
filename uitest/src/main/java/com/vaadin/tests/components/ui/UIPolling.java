package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.Log;
import com.vaadin.v7.data.util.MethodProperty;
import com.vaadin.v7.ui.TextField;

public class UIPolling extends AbstractTestUIWithLog {

    protected static final long SLEEP_TIME = 500;

    private class BackgroundLogger extends Thread {

        @Override
        public void run() {
            int i = 0;
            while (true) {
                i++;
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                }
                final int iteration = i;
                access(() -> log
                        .log((iteration * SLEEP_TIME) + "ms has passed"));
            }
        }
    }

    private BackgroundLogger logger = null;

    @Override
    protected void setup(VaadinRequest request) {
        log = new Log(20);
        log.setNumberLogRows(true);
        TextField pollingInterval = new TextField("Poll interval",
                new MethodProperty<Integer>(this, "pollInterval"));
        pollingInterval.setImmediate(true);
        pollingInterval.setValue("-1");
        pollingInterval.addValueChangeListener(event -> {
            if (logger != null) {
                logger.stop();
                logger = null;
            }
            if (getPollInterval() >= 0) {
                logger = new BackgroundLogger();
                logger.start();
            }
        });
        addComponent(pollingInterval);

    }

    @Override
    protected String getTestDescription() {
        return "Tests the polling feature of UI. Set the polling interval using the text field. Enabling polling will at the same time start a background thread which logs every 500ms";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11495;
    }

}
