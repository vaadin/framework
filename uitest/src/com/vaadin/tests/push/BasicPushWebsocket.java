package com.vaadin.tests.push;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;

@Push(transport = Transport.WEBSOCKET)
public class BasicPushWebsocket extends BasicPush {

    @Override
    public void init(VaadinRequest request) {
        super.init(request);
        // Don't use fallback so we can easier detect if websocket fails
        getPushConfiguration().setFallbackTransport(Transport.WEBSOCKET);
    }

    public static class BasicPushWebsocketTest extends BasicPushTest {
        @Parameters
        public static Collection<DesiredCapabilities> getBrowserForTest() {
            return getWebsocketBrowsers();
        }
    }

}
