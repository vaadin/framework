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
package com.vaadin.tests.widgetset.client;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.tests.widgetset.server.csrf.ui.CsrfTokenDisabled;

/**
 * Mock ApplicationConnection for several issues where we need to hack it.
 * 
 * @author Vaadin Ltd
 */
public class MockApplicationConnection extends ApplicationConnection {

    public MockApplicationConnection() {
        super();
        messageHandler = new MockServerMessageHandler();
        messageHandler.setConnection(this);
        messageSender = new MockServerCommunicationHandler();
        messageSender.setConnection(this);
    }

    @Override
    public MockServerMessageHandler getMessageHandler() {
        return (MockServerMessageHandler) super.getMessageHandler();
    }

    @Override
    public MockServerCommunicationHandler getMessageSender() {
        return (MockServerCommunicationHandler) super
                .getMessageSender();
    }

    /**
     * Provide the last token received from the server. <br/>
     * We added this to test the change done on CSRF token.
     * 
     * @see CsrfTokenDisabled
     */
    public String getLastCsrfTokenReceiver() {
        return getMessageHandler().lastCsrfTokenReceiver;
    }

    /**
     * Provide the last token sent to the server. <br/>
     * We added this to test the change done on CSRF token.
     * 
     * @see CsrfTokenDisabled
     */
    public String getLastCsrfTokenSent() {
        return getMessageSender().lastCsrfTokenSent;
    }

}
