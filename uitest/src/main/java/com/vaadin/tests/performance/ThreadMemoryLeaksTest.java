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
package com.vaadin.tests.performance;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class ThreadMemoryLeaksTest extends AbstractReindeerTestUI {

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
