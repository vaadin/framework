package com.vaadin.tests.widgetset.client;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.tests.widgetset.server.csrf.ui.CsrfTokenDisabled;

/**
 * Mock ApplicationConnection for several issues where we need to hack it.
 *
 * @since
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
        return (MockServerCommunicationHandler) super.getMessageSender();
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
