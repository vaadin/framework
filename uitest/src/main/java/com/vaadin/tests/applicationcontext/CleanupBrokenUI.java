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
package com.vaadin.tests.applicationcontext;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

/**
 * Tests that UI is cleaned from session despite any errors that happen in
 * detach.
 * 
 * @author Vaadin Ltd
 */
public class CleanupBrokenUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        logUIs();
        addComponent(new Label("Label with broken detach") {
            @Override
            public void detach() {
                throw new IllegalStateException(
                        "Detach does not work for this component");
            }
        });

        addComponent(new Button("Ping", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("pong");
            }
        }));

    }

    private void logUIs() {
        log("UIs in session: " + getSession().getUIs().size());
    }

    @Override
    protected String getTestDescription() {
        return "Open the page as http://localhost:8888/run/CleanupBrokenUI, then refresh the page to get a new UI. On refresh there should be an IllegalStateException in the server log but pressing 'ping' after this should log no further messages and the old ui should no longer be in the VaadinSession";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16651;
    }
}
