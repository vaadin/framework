package com.vaadin.tests.rpclogger;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ServerRpcMethodInvocation;
import com.vaadin.server.communication.ServerRpcHandler;
import com.vaadin.shared.communication.LegacyChangeVariablesInvocation;
import com.vaadin.ui.UI;

public class LoggingServerRpcHandler extends ServerRpcHandler {

    @Override
    protected void handleInvocation(UI ui, ClientConnector connector,
            LegacyChangeVariablesInvocation legacyInvocation) {
        ((RPCLoggerUI) ui).recordInvocation(connector, legacyInvocation);
        super.handleInvocation(ui, connector, legacyInvocation);
    }

    @Override
    protected void handleInvocation(UI ui, ClientConnector connector,
            ServerRpcMethodInvocation invocation) {
        ((RPCLoggerUI) ui).recordInvocation(connector, invocation);
        super.handleInvocation(ui, connector, invocation);
    }

}
