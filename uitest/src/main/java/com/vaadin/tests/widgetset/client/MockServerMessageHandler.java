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
