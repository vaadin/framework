/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Server side RPC manager that handles RPC calls coming from the client.
 *
 * Each {@link RpcTarget} (typically a {@link ClientConnector}) should have its
 * own instance of {@link ServerRpcManager} if it wants to receive RPC calls
 * from the client.
 *
 * @since 7.0
 */
public class ServerRpcManager<T extends ServerRpc> implements Serializable {

    private final T implementation;
    private final Class<T> rpcInterface;

    /**
     * Wrapper exception for exceptions which occur during invocation of an RPC
     * call.
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

    private static final Map<Class<?>, Class<?>> BOXED_TYPES = new HashMap<>();
    static {
        try {
            Class<?>[] boxClasses = new Class<?>[] { Boolean.class, Byte.class,
                    Short.class, Character.class, Integer.class, Long.class,
                    Float.class, Double.class };
            for (Class<?> boxClass : boxClasses) {
                Field typeField = boxClass.getField("TYPE");
                Class<?> primitiveType = (Class<?>) typeField.get(boxClass);
                BOXED_TYPES.put(primitiveType, boxClass);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a RPC manager for an RPC target.
     *
     * @param implementation
     *            RPC interface implementation for the target
     * @param rpcInterface
     *            RPC interface type
     */
    public ServerRpcManager(T implementation, Class<T> rpcInterface) {
        this.implementation = implementation;
        this.rpcInterface = rpcInterface;
    }

    /**
     * Invoke a method in a server side RPC target class. This method is to be
     * used by the RPC framework and unit testing tools only.
     *
     * @param target
     *            non-null target of the RPC call
     * @param invocation
     *            method invocation to perform
     * @throws RpcInvocationException
     */
    public static void applyInvocation(ClientConnector target,
            ServerRpcMethodInvocation invocation)
            throws RpcInvocationException {
        ServerRpcManager<?> manager = target
                .getRpcManager(invocation.getInterfaceName());
        if (manager != null) {
            manager.applyInvocation(invocation);
        } else {
            getLogger().log(Level.WARNING,
                    "RPC call received for RpcTarget {0} ({1}) but the target has not registered any RPC interfaces",
                    new Object[] { target.getClass().getName(),
                            invocation.getConnectorId() });
        }
    }

    /**
     * Returns the RPC interface implementation for the RPC target.
     *
     * @return RPC interface implementation
     */
    protected T getImplementation() {
        return implementation;
    }

    /**
     * Returns the RPC interface type managed by this RPC manager instance.
     *
     * @return RPC interface type
     */
    public Class<T> getRpcInterface() {
        return rpcInterface;
    }

    /**
     * Invoke a method in a server side RPC target class. This method is to be
     * used by the RPC framework and unit testing tools only.
     *
     * @param invocation
     *            method invocation to perform
     */
    public void applyInvocation(ServerRpcMethodInvocation invocation)
            throws RpcInvocationException {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getParameters();
        try {
            method.invoke(implementation, arguments);
        } catch (Exception e) {
            throw new RpcInvocationException(
                    "Unable to invoke method " + invocation.getMethodName()
                            + " in " + invocation.getInterfaceName(),
                    e);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(ServerRpcManager.class.getName());
    }

    /**
     * Returns an RPC proxy for a given client to server RPC interface for the
     * given component or extension.
     *
     * @param connector
     *            the connector for which to the RPC proxy
     * @param rpcInterface
     *            the RPC interface type
     *
     * @return a server RPC handler which can be used to invoke RPC methods
     * @since 8.0
     */
    public static <T extends ServerRpc> T getRpcProxy(ClientConnector connector,
            final Class<T> rpcInterface) {

        @SuppressWarnings("unchecked")
        ServerRpcManager<T> rpcManager = (ServerRpcManager<T>) connector
                .getRpcManager(rpcInterface.getName());
        if (rpcManager == null) {
            return null;
        }
        return rpcManager.getImplementation();
    }

}
