package com.vaadin.tests.widgetset.server;

import com.vaadin.tests.widgetset.client.NoLayoutRpc;
import com.vaadin.ui.AbstractComponent;

public class LayoutDetector extends AbstractComponent {

    public void doNoLayoutRpc() {
        getRpcProxy(NoLayoutRpc.class).doRpc();
    }
}
