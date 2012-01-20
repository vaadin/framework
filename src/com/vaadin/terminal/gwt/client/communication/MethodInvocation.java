package com.vaadin.terminal.gwt.client.communication;

/**
 * Information needed by the framework to send an RPC method invocation from the
 * client to the server or vice versa.
 * 
 * @since 7.0
 */
public class MethodInvocation {

    private final String paintableId;
    private final String methodName;
    private final Object[] parameters;

    public MethodInvocation(String paintableId, String methodName,
            Object[] parameters) {
        this.paintableId = paintableId;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getPaintableId() {
        return paintableId;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }
}