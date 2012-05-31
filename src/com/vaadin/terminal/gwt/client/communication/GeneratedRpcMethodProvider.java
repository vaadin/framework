/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

import java.util.Collection;

/**
 * Provides runtime data about client side RPC calls received from the server to
 * the client-side code.
 * 
 * A GWT generator is used to create an implementation of this class at
 * run-time.
 * 
 * @since 7.0
 */
public interface GeneratedRpcMethodProvider {

    public Collection<RpcMethod> getGeneratedRpcMethods();
}
