/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.terminal.gwt.client.Connector;

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
            ServerRPCMethodInvocation invocation) {
        RpcManager manager = target.getRpcManager(invocation
                .getInterfaceClass());
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
    public void applyInvocation(ServerRPCMethodInvocation invocation) {
        Method method = invocation.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        Object[] arguments = invocation.getParameters();
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
            throw new RuntimeException("Unable to invoke method "
                    + invocation.getMethodName() + " in "
                    + invocation.getInterfaceName(), e);
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(ServerRpcManager.class.getName());
    }

}
