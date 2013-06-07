package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.Window;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.CommunicationErrorHandler;
import com.vaadin.client.communication.AtmospherePushConnection;
import com.vaadin.shared.ui.ui.UIState.PushConfigurationState;

public class TestingPushConnection extends AtmospherePushConnection {

    private String transport;

    @Override
    public void init(ApplicationConnection connection,
            PushConfigurationState pushConfiguration,
            CommunicationErrorHandler errorHandler) {
        super.init(connection, pushConfiguration, errorHandler);
        transport = Window.Location.getParameter("transport");
    }

    /*
     * Force transport
     */
    @Override
    protected AtmosphereConfiguration createConfig() {
        AtmosphereConfiguration conf = super.createConfig();
        if (transport != null) {
            conf.setTransport(transport);
            conf.setFallbackTransport(transport);
        }
        return conf;
    }

}
