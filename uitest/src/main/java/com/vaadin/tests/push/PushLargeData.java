package com.vaadin.tests.push;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.TextField;

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
        b.addClickListener(event -> {
            Integer pushSize = (Integer) dataSize.getConvertedValue();
            Integer pushInterval = (Integer) interval.getConvertedValue();
            Integer pushDuration = (Integer) duration.getConvertedValue();
            PushRunnable r = new PushRunnable(getUI(), pushSize, pushInterval,
                    pushDuration);
            executor.execute(r);
            log.log("Starting push, size: " + pushSize + ", interval: "
                    + pushInterval + "ms, duration: " + pushDuration + "ms");
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
        private final UI ui;

        public PushRunnable(UI ui, Integer size, Integer interval,
                Integer duration) {
            this.size = size;
            this.interval = interval;
            this.duration = duration;
            this.ui = ui;
        }

        @Override
        public void run() {
            final long endTime = System.currentTimeMillis() + duration;
            final String data = LoremIpsum.get(size);
            int packageIndex = 1;
            while (System.currentTimeMillis() < endTime) {
                final int idx = packageIndex++;
                ui.access(() -> {
                    PushLargeData pushUi = (PushLargeData) ui;
                    // Using description as it is not rendered to the DOM
                    // immediately
                    pushUi.getDataLabel().setDescription(
                            System.currentTimeMillis() + ": " + data);
                    pushUi.log("Package " + idx + " pushed");
                });
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    return;
                }
            }
            ui.access(() -> {
                PushLargeData pushUi = (PushLargeData) ui;
                pushUi.log("Push complete");
            });

        }
    }
}
