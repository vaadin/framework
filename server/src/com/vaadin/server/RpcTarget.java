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

package com.vaadin.server;

import java.io.Serializable;

/**
 * Marker interface for server side classes that can receive RPC calls.
 * 
 * This plays a role similar to that of {@link VariableOwner}.
 * 
 * @since 7.0
 */
public interface RpcTarget extends Serializable {
    /**
     * Returns the RPC manager instance to use when receiving calls for an RPC
     * interface.
     * 
     * @param rpcInterface
     *            interface for which the call was made
     * @return RpcManager or null if none found for the interface
     */
    public RpcManager getRpcManager(Class<?> rpcInterface);
}
