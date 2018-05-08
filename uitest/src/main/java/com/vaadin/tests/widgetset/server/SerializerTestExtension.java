package com.vaadin.tests.widgetset.server;

import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.tests.widgetset.client.SerializerTestRpc;
import com.vaadin.tests.widgetset.client.SerializerTestState;

public class SerializerTestExtension extends AbstractExtension {

    @Override
    public <T extends ClientRpc> T getRpcProxy(Class<T> rpcInterface) {
        return super.getRpcProxy(rpcInterface);
    }

    @Override
    public SerializerTestState getState() {
        return (SerializerTestState) super.getState();
    }

    public void registerRpc(SerializerTestRpc rpc) {
        super.registerRpc(rpc);
    }

}
