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

/**
 * 
 */
package com.vaadin.tests.push;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public abstract class PushLargeData extends AbstractTestUIWithLog {

    // 200KB
    static final int DEFAULT_SIZE_BYTES = 200 * 1000;

    // Every other second
    static final int DEFAULT_DELAY_MS = 2000;

    // 3 MB is enough for streaming to reconnect
    static final int DEFAULT_DATA_TO_PUSH = 3 * 1000 * 1000;

    static final int DEFAULT_DURATION_MS = DEFAULT_DATA_TO_PUSH
            / DEFAULT_SIZE_BYTES * DEFAULT_DELAY_MS;

    private Label dataLabel = new Label();

    private final ExecutorService executor = Executors
            .newSingleThreadExecutor();

    protected TextField dataSize;

    protected TextField interval;

    protected TextField duration;

    @Override
    protected void setup(VaadinRequest request) {
        dataLabel.setSizeUndefined();
        dataSize = new TextField("Data size");
        dataSize.setConverter(Integer.class);
        interval = new TextField("Interval (ms)");
        interval.setConverter(Integer.class);
        duration = new TextField("Duration (ms)");
        duration.setConverter(Integer.class);

        dataSize.setValue(DEFAULT_SIZE_BYTES + "");
        interval.setValue(DEFAULT_DELAY_MS + "");
        duration.setValue(DEFAULT_DURATION_MS + "");

        addComponent(dataSize);
        addComponent(interval);
        addComponent(duration);

        Button b = new Button("Start pushing");
        b.setId("startButton");
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Integer pushSize = (Integer) dataSize.getConvertedValue();
                Integer pushInterval = (Integer) interval.getConvertedValue();
                Integer pushDuration = (Integer) duration.getConvertedValue();
                PushRunnable r = new PushRunnable(pushSize, pushInterval,
                        pushDuration);
                executor.execute(r);
                log.log("Starting push, size: " + pushSize + ", interval: "
                        + pushInterval + "ms, duration: " + pushDuration + "ms");
            }
        });
        addComponent(b);
        addComponent(dataLabel);
    }

    public Label getDataLabel() {
        return dataLabel;
    }

    @Override
    protected String getTestDescription() {
        return "Tests that pushing large amounts of data do not cause problems";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12567;
    }

    public static class PushRunnable implements Runnable {

        private Integer size;
        private Integer interval;
        private Integer duration;

        public PushRunnable(Integer size, Integer interval, Integer duration) {
            this.size = size;
            this.interval = interval;
            this.duration = duration;
        }

        @Override
        public void run() {
            final long endTime = System.currentTimeMillis() + duration;
            final String data = LoremIpsum.get(size);
            int packageIndex = 1;
            while (System.currentTimeMillis() < endTime) {
                final int idx = packageIndex++;
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        PushLargeData ui = (PushLargeData) UI.getCurrent();
                        // Using description as it is not rendered to the DOM
                        // immediately
                        ui.getDataLabel().setDescription(
                                System.currentTimeMillis() + ": " + data);
                        ui.log("Package " + idx + " pushed");
                    }
                });
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    return;
                }
            }
            UI.getCurrent().access(new Runnable() {
                @Override
                public void run() {
                    PushLargeData ui = (PushLargeData) UI.getCurrent();
                    ui.log("Push complete");
                }
            });

        }
    }
}
