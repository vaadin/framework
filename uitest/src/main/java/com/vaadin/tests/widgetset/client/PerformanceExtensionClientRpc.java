package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.communication.ClientRpc;

public interface PerformanceExtensionClientRpc extends ClientRpc {
    public void start();

    public void stop();
}