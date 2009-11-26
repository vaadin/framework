package com.vaadin.tools;

import java.lang.reflect.Method;

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
     * @return A method reference or null if the method was not found
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
