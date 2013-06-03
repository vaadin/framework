package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.shared.ui.ui.Transport;

@Push(transport = Transport.STREAMING)
public class BasicPushStreaming extends BasicPush {
    @Override
    protected void setup(com.vaadin.server.VaadinRequest request) {
        addComponent(new PushConfigurator(this));
    }
}
