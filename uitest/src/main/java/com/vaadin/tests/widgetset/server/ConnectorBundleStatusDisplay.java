package com.vaadin.tests.widgetset.server;

import com.vaadin.tests.widgetset.client.ConnectorBundleStatusRpc;
import com.vaadin.ui.AbstractComponent;

public class ConnectorBundleStatusDisplay extends AbstractComponent {

    public ConnectorBundleStatusDisplay() {
        setCaption("Loaded bundles");
    }

    public void updateStatus() {
        getRpcProxy(ConnectorBundleStatusRpc.class).updateStatus();
    }
}
