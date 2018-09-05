package com.vaadin.tests.push;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.UIDetachedException;

public class EnableDisablePush extends AbstractReindeerTestUI {

    private int c = 0;

    private Log log = new Log(15);

    private final Timer timer = new Timer(true);

    private final class CounterTask extends TimerTask {

        @Override
        public void run() {

            try {
                while (true) {
                    TimeUnit.MILLISECONDS.sleep(500);

                    access(() -> {
                        log.log("Counter = " + c++);
                        if (c == 3) {
                            log.log("Disabling polling, enabling push");
                            getPushConfiguration()
                                    .setPushMode(PushMode.AUTOMATIC);
                            setPollInterval(-1);
                            log.log("Polling disabled, push enabled");
                        }
                    });
                    if (c == 3) {
                        return;
                    }
                }
            } catch (InterruptedException e) {
            } catch (UIDetachedException e) {
            }
        }
    }

    @Override
    protected void setup(VaadinRequest request) {

        getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
        log.log("Push enabled");

        addComponent(new Button("Disable push", event -> {
            log.log("Disabling push");
            getPushConfiguration().setPushMode(PushMode.DISABLED);
            log.log("Push disabled");
        }));

        addComponent(new Button("Enable push", event -> {
            log.log("Enabling push");
            getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
            log.log("Push enabled");
        }));

        addComponent(new Button("Disable polling", even -> {
            log.log("Disabling poll");
            setPollInterval(-1);
            log.log("Poll disabled");
        }));

        addComponent(new Button("Enable polling", event -> {
            log.log("Enabling poll");
            setPollInterval(1000);
            log.log("Poll enabled");
        }));

        addComponent(new Button(
                "Disable push, re-enable from background thread", event -> {
                    log.log("Disabling push, enabling polling");
                    getPushConfiguration().setPushMode(PushMode.DISABLED);
                    setPollInterval(1000);
                    timer.schedule(new CounterTask(), new Date());
                    log.log("Push disabled, polling enabled");
                }));

        addComponent(log);
    }

    @Override
    protected String getTestDescription() {
        return "Test dynamically enablind and disabling push";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12226;
    }
}
