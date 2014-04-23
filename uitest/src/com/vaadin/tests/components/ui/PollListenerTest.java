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

import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class PollListenerTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Label statusLabel = new Label("Default Label");
        addComponent(statusLabel);

        setPollInterval(2000);
        addPollListener(new PollListener() {
            @Override
            public void poll(PollEvent event) {
                setPollInterval(-1);
                statusLabel.setValue(event.getClass().getSimpleName()
                        + " received");
                removePollListener(this);
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Polling should fire a PollEvent on the server-side";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12466;
    }

}
