package com.vaadin.tests.rpclogger;

import com.vaadin.server.communication.ServerRpcHandler;
import com.vaadin.server.communication.UidlRequestHandler;

public class LoggingUidlRequestHandler extends UidlRequestHandler {

    @Override
    protected ServerRpcHandler createRpcHandler() {
        return new LoggingServerRpcHandler();
    }

}
