/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
