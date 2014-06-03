/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client.communication;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.metadata.InvokationHandler;
import com.vaadin.client.metadata.Method;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.shared.communication.ServerRpc;

/**
 * Class for creating proxy instances for Client to Server RPC.
 * 
 * @since 7.0
 */
public class RpcProxy {

    public static <T extends ServerRpc> T create(Class<T> rpcInterface,
            ServerConnector connector) {
        try {
            return (T) TypeData.getType(rpcInterface).createProxy(
                    new RpcInvokationHandler(rpcInterface, connector));
        } catch (NoDataException e) {
            throw new IllegalStateException("There is no information about "
                    + rpcInterface
                    + ". Did you forget to compile the widgetset?");
        }
    }

    private static final class RpcInvokationHandler implements
            InvokationHandler {
        private final Class<?> rpcInterface;
        private final ServerConnector connector;

        private RpcInvokationHandler(Class<?> rpcInterface,
                ServerConnector connector) {
            this.rpcInterface = rpcInterface;
            this.connector = connector;
        }

        @Override
        public Object invoke(Object target, Method method, Object[] params) {
            MethodInvocation invocation = new MethodInvocation(
                    connector.getConnectorId(), rpcInterface.getName(),
                    method.getName(), params);
            connector.getConnection().addMethodInvocationToQueue(invocation,
                    method.isDelayed(), method.isLastOnly());
            // No RPC iface should have a return value
            return null;
        }
    }
}
