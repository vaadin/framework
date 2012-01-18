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
    private final String[] parameters;

    // TODO Parameters should be an Object[]?
    public MethodInvocation(String paintableId, String methodName,
            String[] parameters) {
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

    public String[] getParameters() {
        return parameters;
    }
}