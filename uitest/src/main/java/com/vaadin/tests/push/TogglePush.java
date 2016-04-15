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
package com.vaadin.tests.push;

import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;

public class TogglePush extends AbstractTestUI {
    private final Label counterLabel = new Label();
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        updateCounter();
        addComponent(counterLabel);

        getPushConfiguration()
                .setPushMode(
                        "disabled".equals(request.getParameter("push")) ? PushMode.DISABLED
                                : PushMode.AUTOMATIC);

        CheckBox pushSetting = new CheckBox("Push enabled");
        pushSetting.setValue(Boolean.valueOf(getPushConfiguration()
                .getPushMode().isEnabled()));
        pushSetting.setImmediate(true);
        pushSetting.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() == Boolean.TRUE) {
                    getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
                } else {
                    getPushConfiguration().setPushMode(PushMode.DISABLED);
                }
            }
        });
        addComponent(pushSetting);

        addComponent(new Button("Update counter now",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        updateCounter();
                    }
                }));

        addComponent(new Button("Update counter in 1 sec",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                access(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateCounter();
                                    }
                                });
                            }
                        }, 1000);
                    }
                }));
    }

    public void updateCounter() {
        counterLabel.setValue("Counter has been updated " + counter++
                + " times");
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
