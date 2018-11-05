package com.vaadin.tests.widgetset.client;

import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.metadata.ConnectorBundleLoader;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.ConnectorBundleStatusDisplay;

@Connect(ConnectorBundleStatusDisplay.class)
public class ConnectorBundleStatusDisplayConnector
        extends AbstractComponentConnector {
    @Override
    public Label getWidget() {
        return (Label) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();
        registerRpc(ConnectorBundleStatusRpc.class,
                () -> ConnectorBundleStatusDisplayConnector.this
                        .updateStatus());

        updateStatus();
    }

    private void updateStatus() {
        List<String> bundles = ConnectorBundleLoader.get().getLoadedBundles();
        getWidget().setText(bundles.toString());
    }
}
