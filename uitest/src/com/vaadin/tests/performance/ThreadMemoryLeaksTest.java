package com.vaadin.tests.performance;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class ThreadMemoryLeaksTest extends AbstractTestUI {

    public static class Worker {
        long value = 0;
        private TimerTask task = new TimerTask() {
            @Override
            public void run() {
                value++;
            }
        };
        private final Timer timer = new Timer(true);

        public Worker() {
            timer.scheduleAtFixedRate(task, new Date(), 1000);
        }
    }

    int workers = 0;
    Label label;

    @Override
    protected void setup(VaadinRequest request) {
        label = new Label(String.format("%d workers", workers));
        addComponent(label);
        addComponent(new Button("Add worker", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                new Worker();
                workers++;
                label.setValue(String.format("%d workers", workers));
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Inherited ThreadLocals should not leak memory. Clicking the "
                + "button starts a new thread, after which memory consumption "
                + "can be checked with visualvm";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12401;
    }
}
