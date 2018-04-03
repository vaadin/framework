package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.MediaControl;
import com.vaadin.tests.widgetset.server.ClientRpcClassComponent;
import com.vaadin.v7.client.ui.label.LabelConnector;

@Connect(ClientRpcClassComponent.class)
public class ClientRpcClassConnector extends LabelConnector {

    @Override
    protected void init() {
        super.init();
        registerRpc(MediaControl.class, getWidget());
    }

    @Override
    public ClientRpcClassWidget getWidget() {
        return (ClientRpcClassWidget) super.getWidget();
    }
}
