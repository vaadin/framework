/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.communication.MethodInvocation;

/**
 * Server side RPC manager that handles RPC calls coming from the client.
 * 
 * Each {@link RpcTarget} (typically a {@link ClientConnector}) should have its
 * own instance of {@link ServerRpcManager} if it wants to receive RPC calls
 * from the client.
 * 
 * @since 7.0
 */
public class ServerRpcManager<T> implements RpcManager {

    private final T implementation;
    private final Class<T> rpcInterface;

    private static final Map<String, Method> invocationMethodCache = new ConcurrentHashMap<String, Method>(
            128, 0.75f, 1);

    private static final Map<Class<?>, Class<?>> boxedTypes = new HashMap<Class<?>, Class<?>>();
    static {
        try {
            Class<?>[] boxClasses = new Class<?>[] { Boolean.class, Byte.class,
                    Short.class, Character.class, Integer.class, Long.class,
                    Float.class, Double.class };
            for (Class<?> boxClass : boxClasses) {
                Field typeField = boxClass.getField("TYPE");
                Class<?> primitiveType = (Class<?>) typeField.get(boxClass);
                boxedTypes.put(primitiveType, boxClass);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a RPC manager for an RPC target.
     * 
     * @param target
     *            RPC call target (normally a {@link Connector})
     * @param implementation
     *            RPC interface implementation for the target
     * @param rpcInterface
     *            RPC interface type
     */
    public ServerRpcManager(T implementation, Class<T> rpcInterface) {
        this.implementation = implementation;
        this.rpcInterface = rpcInterface;
    }

    /**
     * Invoke a method in a server side RPC target class. This method is to be
     * used by the RPC framework and unit testing tools only.
     * 
     * @param target
     *            non-null target of the RPC call
     * @param invocation
     *            method invocation to perform
     */
    public static void applyInvocation(RpcTarget target,
            MethodInvocation invocation) {
        try {
            Class<?> rpcInterfaceClass = Class.forName(invocation
                    .getInterfaceName());
            RpcManager manager = target.getRpcManager(rpcInterfaceClass);
            if (manager != null) {
                manager.applyInvocation(invocation);
            } else {
                getLogger()
                        .log(Level.WARNING,
                                "RPC call received for RpcTarget "
                                        + target.getClass().getName()
                                        + " ("
                                        + invocation.getConnectorId()
                                        + ") but the target has not registered any RPC interfaces");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class for RPC interface "
                    + invocation.getInterfaceName() + " of the target "
                    + target + " could not be found.");
        }
    }

    /**
     * Returns the RPC interface implementation for the RPC target.
     * 
     * @return RPC interface implementation
     */
    protected T getImplementation() {
        return implementation;
    }

    /**
     * Returns the RPC interface type managed by this RPC manager instance.
     * 
     * @return RPC interface type
     */
    protected Class<T> getRpcInterface() {
        return rpcInterface;
    }

    /**
     * Invoke a method in a server side RPC target class. This method is to be
     * used by the RPC framework and unit testing tools only.
     * 
     * @param invocation
     *            method invocation to perform
     */
    public void applyInvocation(MethodInvocation invocation) {
        String methodName = invocation.getMethodName();
        // here, we already know that the interface is an rpcInterface
        Object[] arguments = invocation.getParameters();

        Method method = findInvocationMethod(rpcInterface, methodName,
                arguments.length);
        if (method == null) {
            throw new RuntimeException(implementation + " does not contain "
                    + rpcInterface.getName() + "." + methodName + " with "
                    + arguments.length + " parameters");
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < args.length; i++) {
            // no conversion needed for basic cases
            // Class<?> type = parameterTypes[i];
            // if (type.isPrimitive()) {
            // type = boxedTypes.get(type);
            // }
            args[i] = arguments[i];
        }
        try {
            method.invoke(implementation, args);
        } catch (Exception e) {
            throw new RuntimeException(methodName, e);
        }
    }

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

    private static Logger getLogger() {
        return Logger.getLogger(ServerRpcManager.class.getName());
    }

}
