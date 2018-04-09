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
