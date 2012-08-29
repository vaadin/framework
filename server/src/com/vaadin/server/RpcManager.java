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
 * Server side RPC manager that can invoke methods based on RPC calls received
 * from the client.
 * 
 * @since 7.0
 */
public interface RpcManager extends Serializable {
    public void applyInvocation(ServerRpcMethodInvocation invocation)
            throws RpcInvocationException;

    /**
     * Wrapper exception for exceptions which occur during invocation of an RPC
     * call
     * 
     * @author Vaadin Ltd
     * @since 7.0
     * 
     */
    public static class RpcInvocationException extends Exception {

        public RpcInvocationException() {
            super();
        }

        public RpcInvocationException(String message, Throwable cause) {
            super(message, cause);
        }

        public RpcInvocationException(String message) {
            super(message);
        }

        public RpcInvocationException(Throwable cause) {
            super(cause);
        }

    }

}
