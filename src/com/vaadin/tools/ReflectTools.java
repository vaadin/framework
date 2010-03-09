/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.tools;

import java.lang.reflect.Method;

/**
 * An util class with helpers for reflection operations. Used internally by
 * Vaadin and should not be used by application developers. Subject to change at
 * any time.
 * 
 * @since 6.2
 */
public class ReflectTools {
    /**
     * Locates the method in the given class. Returns null if the method is not
     * found. This method never throws exceptions. Errors in locating methods
     * are considered serious problems and are output to standard error.
     * 
     * @param cls
     *            Class that contains the method
     * @param methodName
     *            The name of the method
     * @param parameterTypes
     *            The parameter types for the method.
     * @return A reference to the method
     */
    public static Method findMethod(Class<?> cls, String methodName,
            Class<?>... parameterTypes) {
        try {
            return cls.getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception e) {
            // Print the stack trace as
            e.printStackTrace(System.err);
        }
        return null;
    }
}
