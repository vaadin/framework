package com.vaadin.tests.widgetset.client.helloworldfeature;

import com.vaadin.shared.communication.ClientRpc;

public interface GreetAgainRpc extends ClientRpc {

    public void greetAgain();

}
