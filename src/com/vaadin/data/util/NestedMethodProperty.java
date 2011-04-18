package com.vaadin.data.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nested accessor based property for a bean.
 * 
 * The property is specified in the dotted notation, e.g. "address.street", and
 * can contain multiple levels of nesting.
 * 
 * When accessing the property value, all intermediate getters must return
 * non-null values.
 * 
 * @see MethodProperty
 * 
 * @param <T>
 *            property type
 * 
 * @since 6.6
 */
public class NestedMethodProperty<T> extends MethodProperty<T> {

    // needed for de-serialization
    private String propertyName;

    // chain of getter methods up to but not including the last method handled
    // by the superclass
    private transient List<Method> getMethods;

    /* Special serialization to handle method references */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // getMethods is reconstructed on read based on propertyName
    }

    /* Special serialization to handle method references */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();

        // re-build getMethods: some duplicated code with builder method
        getMethods = new ArrayList<Method>();
        Class<?> propertyClass = getInstance().getClass();
        String[] simplePropertyNames = propertyName.split("\\.");
        for (int i = 0; i < simplePropertyNames.length; i++) {
            String simplePropertyName = simplePropertyNames[i].trim();
            try {
                Method getter = initGetterMethod(simplePropertyName,
                        propertyClass);
                propertyClass = getter.getReturnType();
                getMethods.add(getter);
            } catch (final java.lang.NoSuchMethodException e) {
                throw new InvalidObjectException("Bean property '"
                        + simplePropertyName + "' not found");
            }
        }
    }

    /**
     * Constructs a nested method property for a given object instance. The
     * property name is a dot separated string pointing to a nested property,
     * e.g. "manager.address.street".
     * 
     * @param <T>
     *            property type (deepest nested property)
     * @param instance
     *            top-level bean to which the property applies
     * @param propertyName
     *            dot separated nested property name
     * @return new NestedMethodProperty instance
     */
    public static <T> NestedMethodProperty<T> buildNestedMethodProperty(
            Object instance, String propertyName) {
        List<Method> getMethods = new ArrayList<Method>();

        String lastSimplePropertyName = propertyName;
        Class<?> lastClass = instance.getClass();

        // first top-level property, then go deeper in a loop
        Class<?> propertyClass = instance.getClass();
        String[] simplePropertyNames = propertyName.split("\\.");
        if (propertyName.endsWith(".") || 0 == simplePropertyNames.length) {
            throw new MethodException(null, "Invalid property name '"
                    + propertyName + "'");
        }
        for (int i = 0; i < simplePropertyNames.length; i++) {
            String simplePropertyName = simplePropertyNames[i].trim();
            if (simplePropertyName.length() > 0) {
                lastSimplePropertyName = simplePropertyName;
                lastClass = propertyClass;
                try {
                    Method getter = initGetterMethod(simplePropertyName,
                            propertyClass);
                    propertyClass = getter.getReturnType();
                    getMethods.add(getter);
                } catch (final java.lang.NoSuchMethodException e) {
                    throw new MethodException(null, "Bean property '"
                            + simplePropertyName + "' not found");
                }
            } else {
                throw new MethodException(null,
                        "Empty or invalid bean property identifier in '"
                                + propertyName + "'");
            }
        }

        // In case the get method is found, resolve the type
        Method lastGetMethod = getMethods.get(getMethods.size() - 1);
        Class<?> type = lastGetMethod.getReturnType();

        // Finds the set method
        Method setMethod = null;
        try {
            // Assure that the first letter is upper cased (it is a common
            // mistake to write firstName, not FirstName).
            if (Character.isLowerCase(lastSimplePropertyName.charAt(0))) {
                final char[] buf = lastSimplePropertyName.toCharArray();
                buf[0] = Character.toUpperCase(buf[0]);
                lastSimplePropertyName = new String(buf);
            }

            setMethod = lastClass.getMethod("set" + lastSimplePropertyName,
                    new Class[] { type });
        } catch (final NoSuchMethodException skipped) {
        }

        NestedMethodProperty<T> property = new NestedMethodProperty<T>(
                (Class<T>) convertPrimitiveType(type), instance, propertyName,
                lastGetMethod, setMethod);
        property.getMethods = getMethods;

        return property;
    }

    protected NestedMethodProperty(Class<T> type, Object instance,
            String propertyName, Method lastGetMethod, Method setMethod) {
        super(type, instance, lastGetMethod, setMethod);
        this.propertyName = propertyName;
    }

    /**
     * Gets the value stored in the Property. The value is resolved by calling
     * the specified getter method with the argument specified at instantiation.
     * 
     * @return the value of the Property
     */
    @Override
    public Object getValue() {
        try {
            Object instance = getInstance();
            for (Method m : getMethods) {
                instance = m.invoke(instance);
            }
            return instance;
        } catch (final Throwable e) {
            throw new MethodException(this, e);
        }
    }

    /**
     * Internal method to actually call the setter method of the wrapped
     * property.
     * 
     * @param value
     */
    @Override
    protected void invokeSetMethod(Object value) {
        try {
            Object instance = getInstance();
            for (int i = 0; i < getMethods.size() - 1; i++) {
                instance = getMethods.get(i).invoke(instance);
            }
            getSetMethod().invoke(instance, new Object[] { value });
        } catch (final InvocationTargetException e) {
            throw new MethodException(this, e.getTargetException());
        } catch (final Exception e) {
            throw new MethodException(this, e);
        }
    }

    /**
     * Returns an unmodifiable list of getter methods to call in sequence to get
     * the property value.
     * 
     * This API may change in future versions.
     * 
     * @return unmodifiable list of getter methods corresponding to each segment
     *         of the property name
     */
    protected List<Method> getGetMethods() {
        return Collections.unmodifiableList(getMethods);
    }

}
