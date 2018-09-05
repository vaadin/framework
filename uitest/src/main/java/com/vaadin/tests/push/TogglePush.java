package com.vaadin.tests.push;

import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;

public class TogglePush extends AbstractReindeerTestUI {
    private final Label counterLabel = new Label();
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        updateCounter();
        addComponent(counterLabel);

        getPushConfiguration()
                .setPushMode("disabled".equals(request.getParameter("push"))
                        ? PushMode.DISABLED
                        : PushMode.AUTOMATIC);

        CheckBox pushSetting = new CheckBox("Push enabled");
        pushSetting.setValue(Boolean
                .valueOf(getPushConfiguration().getPushMode().isEnabled()));
        pushSetting.addValueChangeListener(event -> {
            if (event.getValue()) {
                getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
            } else {
                getPushConfiguration().setPushMode(PushMode.DISABLED);
            }
        });
        addComponent(pushSetting);

        addComponent(
                new Button("Update counter now", event -> updateCounter()));

        addComponent(new Button("Update counter in 1 sec", event -> {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    access(() -> updateCounter());
                }
            }, 1000);
        }));
    }

    public void updateCounter() {
        counterLabel
                .setValue("Counter has been updated " + counter++ + " times");
    }

    @Override
    protected String getTestDescription() {
        return "Basic test for enabling and disabling push on the fly.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11506);
    }

}
