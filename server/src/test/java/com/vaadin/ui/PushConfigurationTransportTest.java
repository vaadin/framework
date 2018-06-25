package com.vaadin.ui;

import org.junit.Assert;
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
            Assert.assertEquals(ui.getPushConfiguration().getTransport(),
                    transport);

            if (transport == Transport.WEBSOCKET_XHR) {
                Assert.assertTrue(ui
                        .getState().pushConfiguration.alwaysUseXhrForServerRequests);
            } else {
                Assert.assertFalse(ui
                        .getState().pushConfiguration.alwaysUseXhrForServerRequests);
            }
        }

    }
}
