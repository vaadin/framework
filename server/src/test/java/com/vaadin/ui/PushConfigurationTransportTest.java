package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;

/**
 * @author Vaadin Ltd
 */
public class PushConfigurationTransportTest {
    @Test
    public void testTransportModes() throws Exception {
        UI ui = new UI() {

            @Override
            protected void init(VaadinRequest request) {
                // TODO Auto-generated method stub

            }

        };
        for (Transport transport : Transport.values()) {
            ui.getPushConfiguration().setTransport(transport);
            assertEquals(ui.getPushConfiguration().getTransport(), transport);

            if (transport == Transport.WEBSOCKET_XHR) {
                assertTrue(ui
                        .getState().pushConfiguration.alwaysUseXhrForServerRequests);
            } else {
                assertFalse(ui
                        .getState().pushConfiguration.alwaysUseXhrForServerRequests);
            }
        }

    }
}
