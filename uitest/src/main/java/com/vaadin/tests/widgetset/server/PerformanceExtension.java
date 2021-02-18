package com.vaadin.tests.widgetset.server;

import com.vaadin.server.AbstractExtension;
import com.vaadin.tests.widgetset.client.PerformanceExtensionClientRpc;
import com.vaadin.tests.widgetset.client.PerformanceExtensionConnector;
import com.vaadin.ui.Label;

public class PerformanceExtension extends AbstractExtension {
    private Label label;

    private PerformanceExtension() {
        registerRpc(
                new PerformanceExtensionConnector.PerformanceExtensionServerRpc() {
                    @Override
                    public void total(int total) {
                        label.setValue("Total: " + total);
                    }
                });
    }

    public static PerformanceExtension wrap(Label label) {
        PerformanceExtension extension = new PerformanceExtension();
        extension.label = label;
        extension.extend(label);
        return extension;
    }

    public void start() {
        getRpcProxy(
                PerformanceExtensionClientRpc.class)
                        .start();
    }

    public void stop() {
        getRpcProxy(
                PerformanceExtensionClientRpc.class)
                        .stop();
    }
}
