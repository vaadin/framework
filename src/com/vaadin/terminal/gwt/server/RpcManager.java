/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

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
     * @version @VERSION@
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
