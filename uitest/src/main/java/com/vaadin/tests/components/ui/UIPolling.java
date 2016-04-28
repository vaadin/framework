/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.ui;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.TextField;

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
                access(new Runnable() {
                    @Override
                    public void run() {
                        log.log((iteration * SLEEP_TIME) + "ms has passed");
                    }
                });
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
        pollingInterval.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (logger != null) {
                    logger.stop();
                    logger = null;
                }
                if (getPollInterval() >= 0) {
                    logger = new BackgroundLogger();
                    logger.start();
                }
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
