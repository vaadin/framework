package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.shared.ui.ui.Transport;

@Push(transport = Transport.WEBSOCKET)
public class BasicPushWebsocket extends BasicPush {

    public static class BasicPushWebsocketTest extends BasicPushTest {

    }

}
