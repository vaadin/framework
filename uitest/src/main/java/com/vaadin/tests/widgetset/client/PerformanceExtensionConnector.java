package com.vaadin.tests.widgetset.client;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.MessageHandler;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.PerformanceExtension;

@Connect(PerformanceExtension.class)
public class PerformanceExtensionConnector extends AbstractExtensionConnector {
    private MessageHandler messageHandler;
    private int startProcessingTime = 0;

    @Override
    protected void extend(ServerConnector target) {
        messageHandler = getConnection().getMessageHandler();
        registerRpc(PerformanceExtensionClientRpc.class,
                new PerformanceExtensionClientRpc() {

                    @Override
                    public void start() {
                        startProcessingTime = getTotalProcessingTime(
                                messageHandler);
                    }

                    @Override
                    public void stop() {
                        getRpcProxy(PerformanceExtensionServerRpc.class)
                                .total(getTotalProcessingTime(messageHandler)
                                        - startProcessingTime);
                        startProcessingTime = 0;
                    }
                });
    }

    private native int getTotalProcessingTime(MessageHandler handler)
    /*-{
    return handler.@com.vaadin.client.communication.MessageHandler::totalProcessingTime;
    }-*/;

    public interface PerformanceExtensionServerRpc extends ServerRpc {
        public void total(int total);
    }
}
