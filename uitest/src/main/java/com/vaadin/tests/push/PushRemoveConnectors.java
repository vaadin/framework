package com.vaadin.tests.push;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.SerializationUtils;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@Push
public class PushRemoveConnectors extends AbstractTestUIWithLog {

    private transient final ScheduledExecutorService threadPool = Executors
            .newScheduledThreadPool(5);
    static final String START = "start";
    static final String STOP = "stop";
    private AbstractOrderedLayout verticalLayout;
    private transient ScheduledFuture<?> task = null;

    @Override
    protected void setup(VaadinRequest request) {
        final CheckBox pollingEnabled = new CheckBox("Polling enabled");
        pollingEnabled.addValueChangeListener(event -> setPollInterval(
                pollingEnabled.getValue() ? 1000 : -1));

        Button start = new Button("start");
        start.setId(START);
        start.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                task = threadPool.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        access(new Runnable() {
                            @Override
                            public void run() {
                                populate();
                                log("Serialized session size: "
                                        + getSessionSize());
                            }
                        });
                    }
                }, 1, 1, TimeUnit.SECONDS);
            }
        });
        Button stop = new Button("stop");
        stop.setId(STOP);
        stop.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (task != null) {
                    task.cancel(true);
                    task = null;
                }

            }
        });
        verticalLayout = new HorizontalLayout();
        populate();
        addComponents(pollingEnabled, start, stop, verticalLayout);
    }

    private void populate() {
        verticalLayout.removeAllComponents();
        for (int i = 0; i < 500; i++) {
            Label l = new Label(".");
            l.setSizeUndefined();
            verticalLayout.addComponent(l);
        }
    }

    private int getSessionSize() {
        return SerializationUtils.serialize(getSession()).length;
    }
}
