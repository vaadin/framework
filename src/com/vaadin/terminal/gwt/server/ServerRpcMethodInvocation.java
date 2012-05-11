/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.vaadin.terminal.gwt.client.communication.MethodInvocation;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public class ServerRpcMethodInvocation extends MethodInvocation {

    private static final Map<String, Method> invocationMethodCache = new ConcurrentHashMap<String, Method>(
            128, 0.75f, 1);

    private final Method method;

    private Class<? extends ServerRpc> interfaceClass;

    public ServerRpcMethodInvocation(String connectorId, String interfaceName,
            String methodName, int parameterCount) {
        super(connectorId, interfaceName, methodName);

        interfaceClass = findClass();
        method = findInvocationMethod(interfaceClass, methodName,
                parameterCount);
    }

    private Class<? extends ServerRpc> findClass() {
        try {
            Class<?> rpcInterface = Class.forName(getInterfaceName());
            if (!ServerRpc.class.isAssignableFrom(rpcInterface)) {
                throw new IllegalArgumentException("The interface "
                        + getInterfaceName() + "is not a server RPC interface.");
            }
            return (Class<? extends ServerRpc>) rpcInterface;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("The server RPC interface "
                    + getInterfaceName() + " could not be found", e);
        } finally {

        }
    }

    public Class<? extends ServerRpc> getInterfaceClass() {
        return interfaceClass;
    }

    public Method getMethod() {
        return method;
    }

    /**
     * Tries to find the method from the cache or alternatively by invoking
     * {@link #doFindInvocationMethod(Class, String, int)} and updating the
     * cache.
     * 
     * @param targetType
     * @param methodName
     * @param parameterCount
     * @return
     */
    private Method findInvocationMethod(Class<?> targetType, String methodName,
            int parameterCount) {
        // TODO currently only using method name and number of parameters as the
        // signature
        String signature = targetType.getName() + "." + methodName + "("
                + parameterCount;
        Method invocationMethod = invocationMethodCache.get(signature);

        if (invocationMethod == null) {
            invocationMethod = doFindInvocationMethod(targetType, methodName,
                    parameterCount);

            if (invocationMethod != null) {
                invocationMethodCache.put(signature, invocationMethod);
            }
        }

        return invocationMethod;
    }

    /**
     * Tries to find the method from the class by looping through available
     * methods.
     * 
     * @param targetType
     * @param methodName
     * @param parameterCount
     * @return
     */
    private Method doFindInvocationMethod(Class<?> targetType,
            String methodName, int parameterCount) {
        Method[] methods = targetType.getMethods();
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (method.getName().equals(methodName)
                    && parameterTypes.length == parameterCount) {
                return method;
            }
        }
        return null;
    }

}
