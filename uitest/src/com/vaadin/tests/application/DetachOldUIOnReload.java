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

package com.vaadin.tests.application;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class DetachOldUIOnReload extends AbstractTestUIWithLog {
    private static final String PERSISTENT_MESSAGES_ATTRIBUTE = DetachOldUIOnReload.class
            .getName() + ".sessionMessages";

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Label("This is UI " + getUIId()));
        addComponent(new Button("Reload page", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getPage().reload();
            }
        }));
        addComponent(new Button("Read log messages from session",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        for (String message : getSessionMessages(false)) {
                            log(message);
                        }
                    }
                }));
    }

    private List<String> getSessionMessages(boolean storeIfNeeded) {
        List<String> messages = (List<String>) getSession().getAttribute(
                PERSISTENT_MESSAGES_ATTRIBUTE);
        if (messages == null) {
            messages = new ArrayList<String>();
            if (storeIfNeeded) {
                getSession().setAttribute(PERSISTENT_MESSAGES_ATTRIBUTE,
                        messages);
            }
        }
        return messages;
    }

    private void logToSession(String message) {
        getSessionMessages(true).add(message);
    }

    @Override
    public void detach() {
        super.detach();
        logToSession("UI " + getUIId() + " has been detached");
    }

    @Override
    protected String getTestDescription() {
        return "Tests that the previous UI gets cleaned immediately when refreshing.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10338);
    }

}
