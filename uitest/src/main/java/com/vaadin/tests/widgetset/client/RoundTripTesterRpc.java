package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;

public interface RoundTripTesterRpc extends ServerRpc, ClientRpc {
    @NoLayout
    public void ping(int nr, String payload);

    public void done();
}
