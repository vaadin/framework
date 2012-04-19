/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Information needed by the framework to send an RPC method invocation from the
 * client to the server or vice versa.
 * 
 * @since 7.0
 */
public class MethodInvocation implements Serializable {

    private final String connectorId;
    private final String interfaceName;
    private final String methodName;
    private Object[] parameters;

    public MethodInvocation(String connectorId, String interfaceName,
            String methodName) {
        this.connectorId = connectorId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
    }

    public MethodInvocation(String connectorId, String interfaceName,
            String methodName, Object[] parameters) {
        this(connectorId, interfaceName, methodName);
        setParameters(parameters);
    }

    public String getConnectorId() {
        return connectorId;
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

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return connectorId + ":" + interfaceName + "." + methodName + "("
                + Arrays.toString(parameters) + ")";
    }

}