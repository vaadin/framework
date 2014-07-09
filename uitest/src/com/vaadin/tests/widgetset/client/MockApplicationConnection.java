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

import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ValueMap;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.tests.widgetset.server.csrf.ui.CsrfTokenDisabled;

/**
 * Mock ApplicationConnection for several issues where we need to hack it.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class MockApplicationConnection extends ApplicationConnection {

    private static final Logger LOGGER = Logger
            .getLogger(MockApplicationConnection.class.getName());

    // The last token received from the server.
    private String lastCsrfTokenReceiver;

    // The last token sent to the server.
    private String lastCsrfTokenSent;

    /**
     * Provide the last token received from the server. <br/>
     * We added this to test the change done on CSRF token.
     * 
     * @see CsrfTokenDisabled
     */
    public String getLastCsrfTokenReceiver() {
        return lastCsrfTokenReceiver;
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
    protected void handleUIDLMessage(Date start, String jsonText, ValueMap json) {
        lastCsrfTokenReceiver = json
                .getString(ApplicationConstants.UIDL_SECURITY_TOKEN_ID);

        super.handleUIDLMessage(start, jsonText, json);
    }

    @Override
    protected void doUidlRequest(String uri, JSONObject payload) {
        JSONValue jsonValue = payload.get(ApplicationConstants.CSRF_TOKEN);
        lastCsrfTokenSent = jsonValue != null ? jsonValue.toString() : null;

        super.doUidlRequest(uri, payload);
    }

}
