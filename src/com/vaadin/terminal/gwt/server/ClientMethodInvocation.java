/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.Serializable;

import com.vaadin.terminal.Paintable;

/**
 * Internal class for keeping track of pending server to client method
 * invocations for a Paintable.
 * 
 * @since 7.0
 */
public class ClientMethodInvocation implements Serializable,
        Comparable<ClientMethodInvocation> {
    private final Paintable paintable;
    private final String interfaceName;
    private final String methodName;
    private final Object[] parameters;

    // used for sorting calls between different Paintables in the same Root
    private final long sequenceNumber;
    // TODO may cause problems when clustering etc.
    private static long counter = 0;

    public ClientMethodInvocation(Paintable paintable, String interfaceName,
            String methodName, Object[] parameters) {
        this.paintable = paintable;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameters = (null != parameters) ? parameters : new Object[0];
        sequenceNumber = ++counter;
    }

    public Paintable getPaintable() {
        return paintable;
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