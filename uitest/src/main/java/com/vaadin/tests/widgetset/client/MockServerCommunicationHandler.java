/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4);
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.widgetset.client;

import com.vaadin.client.communication.MessageSender;
import com.vaadin.shared.ApplicationConstants;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

public class MockServerCommunicationHandler extends MessageSender {

    // The last token sent to the server.
    String lastCsrfTokenSent;

    @Override
    public void send(JsonObject payload) {
        JsonValue jsonValue = payload.get(ApplicationConstants.CSRF_TOKEN);
        lastCsrfTokenSent = jsonValue != null ? jsonValue.toJson() : null;

        super.send(payload);
    }
}
