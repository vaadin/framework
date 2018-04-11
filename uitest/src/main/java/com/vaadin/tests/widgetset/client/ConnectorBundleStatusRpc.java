package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.communication.ClientRpc;

public interface ConnectorBundleStatusRpc extends ClientRpc {
    public void updateStatus();
}
