/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Internal class for keeping track of pending server to client method
 * invocations for a Connector.
 * 
 * @since 7.0
 */
public class ClientMethodInvocation implements Serializable,
        Comparable<ClientMethodInvocation> {
    private final ClientConnector connector;
    private final String interfaceName;
    private final String methodName;
    private final Object[] parameters;
    private Class<?>[] parameterTypes;

    // used for sorting calls between different connectors in the same Root
    private final long sequenceNumber;
    // TODO may cause problems when clustering etc.
    private static long counter = 0;

    public ClientMethodInvocation(ClientConnector connector,
            String interfaceName, Method method, Object[] parameters) {
        this.connector = connector;
        this.interfaceName = interfaceName;
        methodName = method.getName();
        parameterTypes = method.getParameterTypes();
        this.parameters = (null != parameters) ? parameters : new Object[0];
        sequenceNumber = ++counter;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public ClientConnector getConnector() {
        return connector;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    protected long getSequenceNumber() {
        return sequenceNumber;
    }

    public int compareTo(ClientMethodInvocation o) {
        if (null == o) {
            return 0;
        }
        return Long.signum(getSequenceNumber() - o.getSequenceNumber());
    }
}