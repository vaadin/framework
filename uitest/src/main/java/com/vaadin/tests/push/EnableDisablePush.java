/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

                    access(new Runnable() {
                        @Override
                        public void run() {
                            log.log("Counter = " + c++);
                            if (c == 3) {
                                log.log("Disabling polling, enabling push");
                                getPushConfiguration()
                                        .setPushMode(PushMode.AUTOMATIC);
                                setPollInterval(-1);
                                log.log("Polling disabled, push enabled");
                            }
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

        addComponent(new Button("Disable push", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                log.log("Disabling push");
                getPushConfiguration().setPushMode(PushMode.DISABLED);
                log.log("Push disabled");
            }
        }));

        addComponent(new Button("Enable push", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                log.log("Enabling push");
                getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
                log.log("Push enabled");
            }
        }));

        addComponent(new Button("Disable polling", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                log.log("Disabling poll");
                setPollInterval(-1);
                log.log("Poll disabled");
            }
        }));

        addComponent(new Button("Enable polling", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                log.log("Enabling poll");
                setPollInterval(1000);
                log.log("Poll enabled");
            }
        }));

        addComponent(
                new Button("Disable push, re-enable from background thread",
                        new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                log.log("Disabling push, enabling polling");
                                getPushConfiguration()
                                        .setPushMode(PushMode.DISABLED);
                                setPollInterval(1000);
                                timer.schedule(new CounterTask(), new Date());
                                log.log("Push disabled, polling enabled");
                            }
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
