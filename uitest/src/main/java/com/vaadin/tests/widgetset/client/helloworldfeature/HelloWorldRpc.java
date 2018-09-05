package com.vaadin.tests.widgetset.client.helloworldfeature;

import com.vaadin.shared.communication.ServerRpc;

public interface HelloWorldRpc extends ServerRpc {
    public void onMessageSent(String message);
}
