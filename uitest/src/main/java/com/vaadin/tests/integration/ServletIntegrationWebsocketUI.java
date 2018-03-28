package com.vaadin.tests.integration;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.shared.ui.ui.UIState.PushConfigurationState;

/**
 * Server test which uses websockets
 *
 * @since 7.1
 * @author Vaadin Ltd
 */
@Push(transport = Transport.WEBSOCKET)
public class ServletIntegrationWebsocketUI extends ServletIntegrationUI {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.tests.integration.IntegrationTestUI#init(com.vaadin.server
     * .VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest request) {
        super.init(request);
        // Ensure no fallback is used
        getPushConfiguration().setParameter(
                PushConfigurationState.FALLBACK_TRANSPORT_PARAM, "none");

    }

}
