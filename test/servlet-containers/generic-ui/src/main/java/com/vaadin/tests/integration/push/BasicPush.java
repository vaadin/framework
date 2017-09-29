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
package com.vaadin.tests.integration.push;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import java.util.Timer;
import java.util.TimerTask;

@Push
public class BasicPush extends AbstractTestUI {

    public static final String CLIENT_COUNTER_ID = "clientCounter";

    public static final String STOP_TIMER_ID = "stopTimer";

    public static final String START_TIMER_ID = "startTimer";

    public static final String SERVER_COUNTER_ID = "serverCounter";

    public static final String INCREMENT_BUTTON_ID = "incrementCounter";

    private int clientCounter = 0;
    private int serverCounter = 0;
    private final Timer timer = new Timer(true);

    private TimerTask task;

    @Override
    protected void setup(VaadinRequest request) {
        getReconnectDialogConfiguration().setDialogModal(false);
        spacer();

        /*
         * Client initiated push.
         */
        Label lbl = new Label("0");
        lbl.setCaption("Client counter (click 'increment' to update):");
        lbl.setId(CLIENT_COUNTER_ID);
        addComponent(lbl);

        Button incrementButton = new Button("Increment",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        clientCounter++;
                        lbl.setValue(String.valueOf(clientCounter));
                    }
                });
        incrementButton.setId(INCREMENT_BUTTON_ID);
        addComponent(incrementButton);

        spacer();

        /*
         * Server initiated push.
         */
        Label serverCounterLabel = new Label("0");
        serverCounterLabel.setCaption(
                "Server counter (updates each 3s by server thread) :");
        serverCounterLabel.setId(SERVER_COUNTER_ID);
        addComponent(serverCounterLabel);

        Button startTimer = new Button("Start timer", (ClickListener) event -> {
            serverCounter = 0;
            serverCounterLabel.setValue(String.valueOf(serverCounter));
            if (task != null) {
                task.cancel();
            }
            task = new TimerTask() {

                @Override
                public void run() {
                    access(() -> {
                        serverCounter++;
                        serverCounterLabel
                                .setValue(String.valueOf(serverCounter));
                    });
                }
            };
            timer.scheduleAtFixedRate(task, 3000, 3000);
        });
        startTimer.setId(START_TIMER_ID);
        addComponent(startTimer);

        Button stopTimer = new Button("Stop timer", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (task != null) {
                    task.cancel();
                    task = null;
                }
            }
        });
        stopTimer.setId(STOP_TIMER_ID);
        addComponent(stopTimer);
    }

    @Override
    protected String getTestDescription() {
        return "This test tests the very basic operations of push. "
                + "It tests that client initiated changes are "
                + "recieved back to the client as well as server "
                + "initiated changes are correctly updated to the client.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11494;
    }

    private void spacer() {
        addComponent(new Label("<hr/>", ContentMode.HTML));
    }

    @Override
    public void attach() {
        super.attach();
    }

    @Override
    public void detach() {
        super.detach();
        timer.cancel();
    }
}
