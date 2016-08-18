/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.rpclogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ServerRpcMethodInvocation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.LegacyChangeVariablesInvocation;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class RPCLoggerUI extends AbstractTestUIWithLog implements ErrorHandler {

    private List<Action> lastActions = new ArrayList<Action>();

    public static class Action {
        public Action(ClientConnector connector, MethodInvocation invocation) {
            target = connector;
            this.invocation = invocation;
        }

        private MethodInvocation invocation;
        private ClientConnector target;
    }

    @Override
    protected int getLogSize() {
        return 10;
    }

    @Override
    protected void setup(VaadinRequest request) {
        setErrorHandler(this);
        addComponent(new Button("Do something"));
        ListSelect s = new ListSelect();
        s.setMultiSelect(true);
        s.addItem("foo");
        s.addItem("bar");
        addComponent(s);

        addComponent(new Button("Action, which will fail", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                throw new RuntimeException("Something went wrong");
            }
        }));
    }

    public void recordInvocation(ClientConnector connector,
            LegacyChangeVariablesInvocation legacyInvocation) {
        addAction(new Action(connector, legacyInvocation));
    }

    public void recordInvocation(ClientConnector connector,
            ServerRpcMethodInvocation invocation) {
        addAction(new Action(connector, invocation));
    }

    private void addAction(Action action) {
        while (lastActions.size() >= 5) {
            lastActions.remove(0);
        }
        lastActions.add(action);
    }

    public String formatAction(ClientConnector connector,
            LegacyChangeVariablesInvocation legacyInvocation) {
        String connectorIdentifier = getConnectorIdentifier(connector);
        Map<String, Object> changes = legacyInvocation.getVariableChanges();
        String rpcInfo = "";
        for (String key : changes.keySet()) {
            Object value = changes.get(key);
            rpcInfo += key + ": " + formatValue(value);
        }
        return "Legacy RPC " + rpcInfo + " for " + connectorIdentifier;

    }

    public String formatAction(ClientConnector connector,
            ServerRpcMethodInvocation invocation) {
        String connectorIdentifier = getConnectorIdentifier(connector);
        String rpcInfo = invocation.getInterfaceName() + "."
                + invocation.getMethodName() + " (";
        for (Object o : invocation.getParameters()) {
            rpcInfo += formatValue(o);
            rpcInfo += ",";
        }
        rpcInfo = rpcInfo.substring(0, rpcInfo.length() - 2) + ")";
        return "RPC " + rpcInfo + " for " + connectorIdentifier;

    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Object[]) {
            String formatted = "";
            for (Object o : ((Object[]) value)) {
                formatted += formatValue(o) + ",";
            }
            return formatted;
        } else {
            return value.toString();
        }
    }

    private String getConnectorIdentifier(ClientConnector connector) {
        String connectorIdentifier = connector.getClass().getSimpleName();
        if (connector instanceof AbstractComponent) {
            String caption = ((AbstractComponent) connector).getCaption();
            if (caption != null) {
                connectorIdentifier += " - " + caption;
            }
        }
        return "'" + connectorIdentifier + "'";
    }

    @Override
    public void error(com.vaadin.server.ErrorEvent event) {
        String msg = "";

        for (int i = 0; i < lastActions.size(); i++) {
            Action action = lastActions.get(i);
            if (action.invocation instanceof ServerRpcMethodInvocation) {
                msg += "\n" + (i + 1) + " " + formatAction(action.target,
                        (ServerRpcMethodInvocation) action.invocation);
            } else {
                msg += "\n" + (i + 1) + " " + formatAction(action.target,
                        (LegacyChangeVariablesInvocation) action.invocation);
            }
        }

        msg += "\n";
        msg += "\n";
        msg += "This error should not really be shown but logged for later analysis.";
        Notification.show(
                "Something went wrong. Actions leading up to this error were:",
                msg, Type.ERROR_MESSAGE);
        // log(msg);
    }
}
