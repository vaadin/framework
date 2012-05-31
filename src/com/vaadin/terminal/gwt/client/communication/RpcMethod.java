/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

public abstract class RpcMethod {
    private String interfaceName;
    private String methodName;
    private Type[] parameterTypes;

    public RpcMethod(String interfaceName, String methodName,
            Type... parameterTypes) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    public abstract void applyInvocation(ClientRpc target, Object... parameters);

}
