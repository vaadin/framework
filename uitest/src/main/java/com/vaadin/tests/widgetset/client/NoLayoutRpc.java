package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.communication.ClientRpc;

public interface NoLayoutRpc extends ClientRpc {

    @NoLayout
    public void doRpc();

}
