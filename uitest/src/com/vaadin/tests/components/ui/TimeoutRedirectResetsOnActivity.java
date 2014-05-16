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

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class TimeoutRedirectResetsOnActivity extends AbstractTestUI {

    private int maxActiveInterval = 15;
    private int timeoutOverhead = 15;

    @Override
    protected void setup(VaadinRequest request) {
        setupTimeout(request);

        Label startedLabel = new Label();
        startedLabel.setValue(String.valueOf(System.currentTimeMillis()));
        startedLabel.setId("startedTime");

        Label originalLabel = new Label();
        originalLabel.setId("originalExpireTime");
        originalLabel.setValue(String.valueOf(getExpireTime()));

        final Label expiresLabel = new Label();
        expiresLabel.setId("actualExpireTime");

        Button button = new Button("Reset", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                expiresLabel.setValue(String.valueOf(getExpireTime()));
            }

        });
        button.setId("reset");

        addComponent(button);

        addComponent(startedLabel);
        addComponent(originalLabel);
        addComponent(expiresLabel);
    }

    private long getExpireTime() {
        return System.currentTimeMillis()
                + (maxActiveInterval + timeoutOverhead) * 1000;
    }

    private void setupTimeout(VaadinRequest request) {
        request.getService().setSystemMessagesProvider(
                new SystemMessagesProvider() {
                    @Override
                    public SystemMessages getSystemMessages(
                            SystemMessagesInfo systemMessagesInfo) {
                        CustomizedSystemMessages msgs = new CustomizedSystemMessages();
                        msgs.setSessionExpiredMessage(null);
                        msgs.setSessionExpiredCaption(null);
                        msgs.setSessionExpiredNotificationEnabled(true);
                        msgs.setSessionExpiredURL("http://example.com/");
                        return msgs;
                    }
                });
        /*
         * NOTE: in practice, this means a timeout after 25 seconds, because of
         * implementation details in
         * com.vaadin.server.communication.MetadataWriter
         */
        getSession().getSession().setMaxInactiveInterval(maxActiveInterval);
    }

    @Override
    protected String getTestDescription() {
        return "The timeout redirect timer should reset if there's activity between the client and server.";
    }

    @Override
    @SuppressWarnings("boxing")
    protected Integer getTicketNumber() {
        return 12446;
    }

}
