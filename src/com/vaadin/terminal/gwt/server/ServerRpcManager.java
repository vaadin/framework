package com.vaadin.terminal.gwt.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.gwt.client.communication.MethodInvocation;

/**
 * Server side RPC manager that handles RPC calls coming from the client.
 * 
 * Each {@link RpcTarget} (typically a {@link Paintable}) should have its own
 * instance of {@link ServerRpcManager} if it wants to receive RPC calls from
 * the client.
 * 
 * @since 7.0
 */
public class ServerRpcManager implements RpcManager {

    private final RpcTarget target;
    private final Object implementation;

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
     *            RPC call target (normally a {@link Paintable})
     * @param implementation
     *            RPC interface implementation for the target
     */
    public ServerRpcManager(RpcTarget target, Object implementation) {
        this.target = target;
        this.implementation = implementation;
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
        RpcManager manager = target.getRpcManager();
        if (manager != null) {
            manager.applyInvocation(invocation);
        } else {
            throw new RuntimeException(
                    "RPC call to a target without an RPC manager.");
        }
    }

    /**
     * Returns the RPC target of this RPC manager instance.
     * 
     * @return RpcTarget, typically a {@link Paintable}
     */
    public RpcTarget getTarget() {
        return target;
    }

    /**
     * Returns the RPC interface implementation for the RPC target.
     * 
     * @return RPC interface implementation
     */
    protected Object getImplementation() {
        return implementation;
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
        Object[] arguments = invocation.getParameters();

        Method method = findInvocationMethod(implementation.getClass(),
                methodName, arguments.length);
        if (method == null) {
            throw new RuntimeException(implementation + " does not contain "
                    + methodName + " with " + arguments.length + " parameters");
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

    private static Method findInvocationMethod(Class<?> targetType,
            String methodName, int parameterCount) {
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

    private static Method doFindInvocationMethod(Class<?> targetType,
            String methodName, int parameterCount) {
        Class<?>[] interfaces = targetType.getInterfaces();
        for (Class<?> iface : interfaces) {
            Method[] methods = iface.getMethods();
            for (Method method : methods) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (method.getName().equals(methodName)
                        && parameterTypes.length == parameterCount) {
                    return method;
                }
            }
        }
        return null;
    }

}
