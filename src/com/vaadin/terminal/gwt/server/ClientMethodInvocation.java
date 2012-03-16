/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.Serializable;

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

    // used for sorting calls between different Paintables in the same Root
    private final long sequenceNumber;
    // TODO may cause problems when clustering etc.
    private static long counter = 0;

    public ClientMethodInvocation(ClientConnector connector,
            String interfaceName, String methodName, Object[] parameters) {
        this.connector = connector;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameters = (null != parameters) ? parameters : new Object[0];
        sequenceNumber = ++counter;
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