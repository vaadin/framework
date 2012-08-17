/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.core.client.GWT;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.ServerConnector;

/**
 * Class for creating proxy instances for Client to Server RPC.
 * 
 * @since 7.0
 */
public class RpcProxy {

    private static RpcProxyCreator impl = GWT.create(RpcProxyCreator.class);

    /**
     * Create a proxy class for the given Rpc interface and assign it to the
     * given connector.
     * 
     * @param rpcInterface
     *            The rpc interface to construct a proxy for
     * @param connector
     *            The connector this proxy is connected to
     * @return A proxy class used for calling Rpc methods.
     */
    public static <T extends ServerRpc> T create(Class<T> rpcInterface,
            ServerConnector connector) {
        return impl.create(rpcInterface, connector);
    }

    public interface RpcProxyCreator {
        <T extends ServerRpc> T create(Class<T> rpcInterface,
                ServerConnector connector);
    }
}
