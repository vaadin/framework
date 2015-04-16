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
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.tests.widgetset.server.csrf.ui.CsrfTokenDisabled;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * Mock ApplicationConnection for several issues where we need to hack it.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class MockApplicationConnection extends ApplicationConnection {

    public MockApplicationConnection() {
        super();
        serverMessageHandler = new MockServerMessageHandler();
        serverMessageHandler.setConnection(this);
    }

    // The last token sent to the server.
    private String lastCsrfTokenSent;

    @Override
    public MockServerMessageHandler getServerMessageHandler() {
        return (MockServerMessageHandler) super.getServerMessageHandler();
    }

    /**
     * Provide the last token received from the server. <br/>
     * We added this to test the change done on CSRF token.
     * 
     * @see CsrfTokenDisabled
     */
    public String getLastCsrfTokenReceiver() {
        return getServerMessageHandler().lastCsrfTokenReceiver;
    }

    /**
     * Provide the last token sent to the server. <br/>
     * We added this to test the change done on CSRF token.
     * 
     * @see CsrfTokenDisabled
     */
    public String getLastCsrfTokenSent() {
        return lastCsrfTokenSent;
    }

    @Override
    public void doUidlRequest(String uri, JsonObject payload, boolean retry) {
        JsonValue jsonValue = payload.get(ApplicationConstants.CSRF_TOKEN);
        lastCsrfTokenSent = jsonValue != null ? jsonValue.toJson() : null;

        super.doUidlRequest(uri, payload, retry);
    }

}
