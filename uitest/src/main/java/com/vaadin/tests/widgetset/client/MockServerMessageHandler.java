/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import com.vaadin.client.ValueMap;
import com.vaadin.client.communication.MessageHandler;
import com.vaadin.shared.ApplicationConstants;

public class MockServerMessageHandler extends MessageHandler {

    // The last token received from the server.
    protected String lastCsrfTokenReceiver;

    @Override
    public void handleJSON(ValueMap json) {
        lastCsrfTokenReceiver = json
                .getString(ApplicationConstants.UIDL_SECURITY_TOKEN_ID);

        super.handleJSON(json);
    }

}
