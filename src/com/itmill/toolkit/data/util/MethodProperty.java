/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.data.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import com.itmill.toolkit.data.Property;

/**
 * <p>
 * Proxy class for creating Properties from pairs of getter and setter methods
 * of a Bean property. An instance of this class can be thought as having been
 * attached to a field of an object. Accessing the object through the Property
 * interface directly manipulates the underlying field.
 * </p>
 * 
 * <p>
 * It's assumed that the return value returned by the getter method is
 * assignable to the type of the property, and the setter method parameter is
 * assignable to that value.
 * </p>
 * 
 * <p>
 * A valid getter method must always be available, but instance of this class
 * can be constructed with a <code>null</code> setter method in which case the
 * resulting MethodProperty is read-only.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class MethodProperty implements Property {

    /**
     * The object that includes the property the MethodProperty is bound to.
     */
    private final Object instance;

    /**
     * Argument arrays for the getter and setter methods.
     */
    private Object[] setArgs, getArgs;

    /**
     * Is the MethodProperty read-only?
     */
    private boolean readOnly;

    /**
     * The getter and setter methods.
     */
    private Method setMethod, getMethod;

    /**
     * Index of the new value in the argument list for the setter method. If the
     * setter method requires several parameters, this index tells which one is
     * the actual value to change.
     */
    private int setArgumentIndex;

    /**
     * Type of the property.
     */
    private Class type;

    /**
     * List of listeners who are interested in the read-only status changes of
     * the MethodProperty
     */
    private LinkedList readOnlyStatusChangeListeners = null;

    /**
     * <p>
     * Creates a new instance of <code>MethodProperty</code> from a named bean
     * property. This constructor takes an object and the name of a bean
     * property and initializes itself with the accessor methods for the
     * property.
     * </p>
     * <p>
     * The getter method of a <code>MethodProperty</code> instantiated with
     * this constructor will be called with no arguments, and the setter method
     * with only the new value as the sole argument.
     * </p>
     * 
     * <p>
     * If the setter method is unavailable, the resulting
     * <code>MethodProperty</code> will be read-only, otherwise it will be
     * read-write.
     * </p>
     * 
     * <p>
     * Method names are constucted from the bean property by adding
     * get/is/are/set prefix and capitalising the first character in the name of
     * the given bean property.
     * </p>
     * 
     * @param instance
     *                the object that includes the property.
     * @param beanPropertyName
     *                the name of the property to bind to.
     */
    public MethodProperty(Object instance, String beanPropertyName) {

        final Class beanClass = instance.getClass();

        // Assure that the first letter is upper cased (it is a common
        // mistake to write firstName, not FirstName).
        if (Character.isLowerCase(beanPropertyName.charAt(0))) {
            final char[] buf = beanPropertyName.toCharArray();
            buf[0] = Character.toUpperCase(buf[0]);
            beanPropertyName = new String(buf);
        }

        // Find the get method
        getMethod = null;
        try {
            getMethod = beanClass.getMethod("get" + beanPropertyName,
                    new Class[] {});
        } catch (final java.lang.NoSuchMethodException ignored) {
            try {
                getMethod = beanClass.getMethod("is" + beanPropertyName,
                        new Class[] {});
            } catch (final java.lang.NoSuchMethodException ignoredAsWell) {
                try {
                    getMethod = beanClass.getMethod("are" + beanPropertyName,
                            new Class[] {});
                } catch (final java.lang.NoSuchMethodException e) {
                    throw new MethodProperty.MethodException("Bean property "
                            + beanPropertyName + " can not be found");
                }
            }
        }

        // In case the get method is found, resolve the type
        type = getMethod.getReturnType();

        // Finds the set method
        setMethod = null;
        try {
            setMethod = beanClass.getMethod("set" + beanPropertyName,
                    new Class[] { type });
        } catch (final java.lang.NoSuchMethodException skipped) {
        }

        // Gets the return type from get method
        if (type.isPrimitive()) {
            if (type.equals(Boolean.TYPE)) {
                type = Boolean.class;
            } else if (type.equals(Integer.TYPE)) {
                type = Integer.class;
            } else if (type.equals(Float.TYPE)) {
                type = Float.class;
            } else if (type.equals(Double.TYPE)) {
                type = Double.class;
            } else if (type.equals(Byte.TYPE)) {
                type = Byte.class;
            } else if (type.equals(Character.TYPE)) {
                type = Character.class;
            } else if (type.equals(Short.TYPE)) {
                type = Short.class;
            } else if (type.equals(Long.TYPE)) {
                type = Long.class;
            }
        }

        setArguments(new Object[] {}, new Object[] { null }, 0);
        readOnly = (setMethod == null);
        this.instance = instance;
    }

    /**
     * <p>
     * Creates a new instance of <code>MethodProperty</code> from named getter
     * and setter methods. The getter method of a <code>MethodProperty</code>
     * instantiated with this constructor will be called with no arguments, and
     * the setter method with only the new value as the sole argument.
     * </p>
     * 
     * <p>
     * If the setter method is <code>null</code>, the resulting
     * <code>MethodProperty</code> will be read-only, otherwise it will be
     * read-write.
     * </p>
     * 
     * @param type
     *                the type of the property.
     * @param instance
     *                the object that includes the property.
     * @param getMethodName
     *                the name of the getter method.
     * @param setMethodName
     *                the name of the setter method.
     * 
     */
    public MethodProperty(Class type, Object instance, String getMethodName,
            String setMethodName) {
        this(type, instance, getMethodName, setMethodName, new Object[] {},
                new Object[] { null }, 0);
    }

    /**
     * <p>
     * Creates a new instance of <code>MethodProperty</code> with the getter
     * and setter methods. The getter method of a <code>MethodProperty</code>
     * instantiated with this constructor will be called with no arguments, and
     * the setter method with only the new value as the sole argument.
     * </p>
     * 
     * <p>
     * If the setter method is <code>null</code>, the resulting
     * <code>MethodProperty</code> will be read-only, otherwise it will be
     * read-write.
     * </p>
     * 
     * @param type
     *                the type of the property.
     * @param instance
     *                the object that includes the property.
     * @param getMethod
     *                the getter method.
     * @param setMethod
     *                the setter method.
     */
    public MethodProperty(Class type, Object instance, Method getMethod,
            Method setMethod) {
        this(type, instance, getMethod, setMethod, new Object[] {},
                new Object[] { null }, 0);
    }

    /**
     * <p>
     * Creates a new instance of <code>MethodProperty</code> from named getter
     * and setter methods and argument lists. The getter method of a
     * <code>MethodProperty</code> instantiated with this constructor will be
     * called with the getArgs as arguments. The setArgs will be used as the
     * arguments for the setter method, though the argument indexed by the
     * setArgumentIndex will be replaced with the argument passed to the
     * {@link #setValue(Object newValue)} method.
     * </p>
     * 
     * <p>
     * For example, if the <code>setArgs</code> contains <code>A</code>,
     * <code>B</code> and <code>C</code>, and <code>setArgumentIndex =
     * 1</code>,
     * the call <code>methodProperty.setValue(X)</code> would result in the
     * setter method to be called with the parameter set of
     * <code>{A, X, C}</code>
     * </p>
     * 
     * @param type
     *                the type of the property.
     * @param instance
     *                the object that includes the property.
     * @param getMethodName
     *                the name of the getter method.
     * @param setMethodName
     *                the name of the setter method.
     * @param getArgs
     *                the fixed argument list to be passed to the getter method.
     * @param setArgs
     *                the fixed argument list to be passed to the setter method.
     * @param setArgumentIndex
     *                the index of the argument in <code>setArgs</code> to be
     *                replaced with <code>newValue</code> when
     *                {@link #setValue(Object newValue)} is called.
     */
    public MethodProperty(Class type, Object instance, String getMethodName,
            String setMethodName, Object[] getArgs, Object[] setArgs,
            int setArgumentIndex) {

        // Check the setargs and setargs index
        if (setMethodName != null && setArgs == null) {
            throw new IndexOutOfBoundsException("The setArgs can not be null");
        }
        if (setMethodName != null
                && (setArgumentIndex < 0 || setArgumentIndex >= setArgs.length)) {
            throw new IndexOutOfBoundsException(
                    "The setArgumentIndex must be >= 0 and < setArgs.length");
        }

        // Set type
        this.type = type;

        // Find set and get -methods
        final Method[] m = instance.getClass().getMethods();

        // Finds get method
        boolean found = false;
        for (int i = 0; i < m.length; i++) {

            // Tests the name of the get Method
            if (!m[i].getName().equals(getMethodName)) {

                // name does not match, try next method
                continue;
            }

            // Tests return type
            if (!type.equals(m[i].getReturnType())) {
                continue;
            }

            // Tests the parameter types
            final Class[] c = m[i].getParameterTypes();
            if (c.length != getArgs.length) {

                // not the right amount of parameters, try next method
                continue;
            }
            int j = 0;
            while (j < c.length) {
                if (getArgs[j] != null
                        && !c[j].isAssignableFrom(getArgs[j].getClass())) {

                    // parameter type does not match, try next method
                    break;
                }
                j++;
            }
            if (j == c.length) {

                // all paramteters matched
                if (found == true) {
                    throw new MethodProperty.MethodException(
                            "Could not uniquely identify " + getMethodName
                                    + "-method");
                } else {
                    found = true;
                    getMethod = m[i];
                }
            }
        }
        if (found != true) {
            throw new MethodProperty.MethodException("Could not find "
                    + getMethodName + "-method");
        }

        // Finds set method
        if (setMethodName != null) {

            // Finds setMethod
            found = false;
            for (int i = 0; i < m.length; i++) {

                // Checks name
                if (!m[i].getName().equals(setMethodName)) {

                    // name does not match, try next method
                    continue;
                }

                // Checks parameter compatibility
                final Class[] c = m[i].getParameterTypes();
                if (c.length != setArgs.length) {

                    // not the right amount of parameters, try next method
                    continue;
                }
                int j = 0;
                while (j < c.length) {
                    if (setArgs[j] != null
                            && !c[j].isAssignableFrom(setArgs[j].getClass())) {

                        // parameter type does not match, try next method
                        break;
                    } else if (j == setArgumentIndex && !c[j].equals(type)) {

                        // Property type is not the same as setArg type
                        break;
                    }
                    j++;
                }
                if (j == c.length) {

                    // all parameters match
                    if (found == true) {
                        throw new MethodProperty.MethodException(
                                "Could not identify unique " + setMethodName
                                        + "-method");
                    } else {
                        found = true;
                        setMethod = m[i];
                    }
                }
            }
            if (found != true) {
                throw new MethodProperty.MethodException("Could not identify "
                        + setMethodName + "-method");
            }
        }

        // Gets the return type from get method
        if (type.isPrimitive()) {
            if (type.equals(Boolean.TYPE)) {
                type = Boolean.class;
            } else if (type.equals(Integer.TYPE)) {
                type = Integer.class;
            } else if (type.equals(Float.TYPE)) {
                type = Float.class;
            } else if (type.equals(Double.TYPE)) {
                type = Double.class;
            } else if (type.equals(Byte.TYPE)) {
                type = Byte.class;
            } else if (type.equals(Character.TYPE)) {
                type = Character.class;
            } else if (type.equals(Short.TYPE)) {
                type = Short.class;
            } else if (type.equals(Long.TYPE)) {
                type = Long.class;
            }
        }

        setArguments(getArgs, setArgs, setArgumentIndex);
        readOnly = (setMethod == null);
        this.instance = instance;
    }

    /**
     * <p>
     * Creates a new instance of <code>MethodProperty</code> from the getter
     * and setter methods, and argument lists.
     * </p>
     * <p>
     * This constructor behaves exactly like
     * {@link #MethodProperty(Class type, Object instance, String getMethodName,
     * String setMethodName, Object [] getArgs, Object [] setArgs, int
     * setArgumentIndex)} except that instead of names of the getter and setter
     * methods this constructor is given the actual methods themselves.
     * </p>
     * 
     * @param type
     *                the type of the property.
     * @param instance
     *                the object that includes the property.
     * @param getMethod
     *                the getter method.
     * @param setMethod
     *                the setter method.
     * @param getArgs
     *                the fixed argument list to be passed to the getter method.
     * @param setArgs
     *                the fixed argument list to be passed to the setter method.
     * @param setArgumentIndex
     *                the index of the argument in <code>setArgs</code> to be
     *                replaced with <code>newValue</code> when
     *                {@link #setValue(Object newValue)} is called.
     */
    public MethodProperty(Class type, Object instance, Method getMethod,
            Method setMethod, Object[] getArgs, Object[] setArgs,
            int setArgumentIndex) {

        if (getMethod == null) {
            throw new MethodProperty.MethodException(
                    "Property GET-method cannot not be null: " + type);
        }

        if (setMethod != null
                && (setArgumentIndex < 0 || setArgumentIndex >= setArgs.length)) {
            throw new IndexOutOfBoundsException(
                    "The setArgumentIndex must be >= 0 and < setArgs.length");
        }

        // Gets the return type from get method
        if (type.isPrimitive()) {
            if (type.equals(Boolean.TYPE)) {
                type = Boolean.class;
            } else if (type.equals(Integer.TYPE)) {
                type = Integer.class;
            } else if (type.equals(Float.TYPE)) {
                type = Float.class;
            } else if (type.equals(Double.TYPE)) {
                type = Double.class;
            } else if (type.equals(Byte.TYPE)) {
                type = Byte.class;
            } else if (type.equals(Character.TYPE)) {
                type = Character.class;
            } else if (type.equals(Short.TYPE)) {
                type = Short.class;
            } else if (type.equals(Long.TYPE)) {
                type = Long.class;
            }
        }

        this.getMethod = getMethod;
        this.setMethod = setMethod;
        setArguments(getArgs, setArgs, setArgumentIndex);
        readOnly = (setMethod == null);
        this.instance = instance;
        this.type = type;
    }

    /**
     * Returns the type of the Property. The methods <code>getValue</code> and
     * <code>setValue</code> must be compatible with this type: one must be
     * able to safely cast the value returned from <code>getValue</code> to
     * the given type and pass any variable assignable to this type as an
     * argument to <code>setValue</code>.
     * 
     * @return type of the Property
     */
    public final Class getType() {
        return type;
    }

    /**
     * Tests if the object is in read-only mode. In read-only mode calls to
     * <code>setValue</code> will throw <code>ReadOnlyException</code> and
     * will not modify the value of the Property.
     * 
     * @return <code>true</code> if the object is in read-only mode,
     *         <code>false</code> if it's not
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Gets the value stored in the Property. The value is resolved by calling
     * the specified getter method with the argument specified at instantiation.
     * 
     * @return the value of the Property
     */
    public Object getValue() {
        try {
            return getMethod.invoke(instance, getArgs);
        } catch (final Throwable e) {
            throw new MethodProperty.MethodException(e);
        }
    }

    /**
     * Returns the value of the <code>MethodProperty</code> in human readable
     * textual format. The return value should be assignable to the
     * <code>setValue</code> method if the Property is not in read-only mode.
     * 
     * @return String representation of the value stored in the Property
     */
    public String toString() {
        final Object value = getValue();
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    /**
     * <p>
     * Sets the setter method and getter method argument lists.
     * </p>
     * 
     * @param getArgs
     *                the fixed argument list to be passed to the getter method.
     * @param setArgs
     *                the fixed argument list to be passed to the setter method.
     * @param setArgumentIndex
     *                the index of the argument in <code>setArgs</code> to be
     *                replaced with <code>newValue</code> when
     *                {@link #setValue(Object newValue)} is called.
     */
    public void setArguments(Object[] getArgs, Object[] setArgs,
            int setArgumentIndex) {
        this.getArgs = new Object[getArgs.length];
        for (int i = 0; i < getArgs.length; i++) {
            this.getArgs[i] = getArgs[i];
        }
        this.setArgs = new Object[setArgs.length];
        for (int i = 0; i < setArgs.length; i++) {
            this.setArgs[i] = setArgs[i];
        }
        this.setArgumentIndex = setArgumentIndex;
    }

    /**
     * Sets the value of the property. This method supports setting from
     * <code>String</code>s if either <code>String</code> is directly
     * assignable to property type, or the type class contains a string
     * constructor.
     * 
     * @param newValue
     *                the New value of the property.
     * @throws <code>Property.ReadOnlyException</code> if the object is in
     *                 read-only mode.
     * @throws <code>Property.ConversionException</code> if
     *                 <code>newValue</code> can't be converted into the
     *                 Property's native type directly or through
     *                 <code>String</code>.
     * @see #invokeSetMethod(Object)
     */
    public void setValue(Object newValue) throws Property.ReadOnlyException,
            Property.ConversionException {

        // Checks the mode
        if (isReadOnly()) {
            throw new Property.ReadOnlyException();
        }

        // Try to assign the compatible value directly
        if (newValue == null || type.isAssignableFrom(newValue.getClass())) {
            invokeSetMethod(newValue);
        } else {

            Object value;
            try {

                // Gets the string constructor
                final Constructor constr = getType().getConstructor(
                        new Class[] { String.class });

                value = constr
                        .newInstance(new Object[] { newValue.toString() });

            } catch (final java.lang.Exception e) {
                throw new Property.ConversionException(e);
            }

            // Creates new object from the string
            invokeSetMethod(value);
        }
    }

    /**
     * Internal method to actually call the setter method of the wrapped
     * property.
     * 
     * @param value
     */
    private void invokeSetMethod(Object value) {

        try {
            // Construct a temporary argument array only if needed
            if (setArgs.length == 1) {
                setMethod.invoke(instance, new Object[] { value });
            } else {

                // Sets the value to argument array
                final Object[] args = new Object[setArgs.length];
                for (int i = 0; i < setArgs.length; i++) {
                    args[i] = (i == setArgumentIndex) ? value : setArgs[i];
                }
                setMethod.invoke(instance, args);
            }
        } catch (final InvocationTargetException e) {
            final Throwable targetException = e.getTargetException();
            throw new MethodProperty.MethodException(targetException);
        } catch (final Exception e) {
            throw new MethodProperty.MethodException(e);
        }
    }

    /**
     * Sets the Property's read-only mode to the specified status.
     * 
     * @param newStatus
     *                the new read-only status of the Property.
     */
    public void setReadOnly(boolean newStatus) {
        final boolean prevStatus = readOnly;
        if (newStatus) {
            readOnly = true;
        } else {
            readOnly = (setMethod == null);
        }
        if (prevStatus != readOnly) {
            fireReadOnlyStatusChange();
        }
    }

    /**
     * <code>Exception</code> object that signals that there were problems
     * calling or finding the specified getter or setter methods of the
     * property.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class MethodException extends RuntimeException {

        /**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3690473623827855153L;

        /**
         * Cause of the method exception
         */
        private Throwable cause;

        /**
         * Constructs a new <code>MethodException</code> with the specified
         * detail message.
         * 
         * @param msg
         *                the detail message.
         */
        public MethodException(String msg) {
            super(msg);
        }

        /**
         * Constructs a new <code>MethodException</code> from another
         * exception.
         * 
         * @param cause
         *                the cause of the exception.
         */
        public MethodException(Throwable cause) {
            this.cause = cause;
        }

        /**
         * @see java.lang.Throwable#getCause()
         */
        public Throwable getCause() {
            return cause;
        }

        /**
         * Gets the method property this exception originates from.
         */
        public MethodProperty getMethodProperty() {
            return MethodProperty.this;
        }
    }

    /* Events *************************************************************** */

    /**
     * An <code>Event</code> object specifying the Property whose read-only
     * status has been changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    private class ReadOnlyStatusChangeEvent extends java.util.EventObject
            implements Property.ReadOnlyStatusChangeEvent {

        /**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3258129163305955896L;

        /**
         * Constructs a new read-only status change event for this object.
         * 
         * @param source
         *                source object of the event.
         */
        protected ReadOnlyStatusChangeEvent(MethodProperty source) {
            super(source);
        }

        /**
         * Gets the Property whose read-only state has changed.
         * 
         * @return source Property of the event.
         */
        public Property getProperty() {
            return (Property) getSource();
        }

    }

    /**
     * Registers a new read-only status change listener for this Property.
     * 
     * @param listener
     *                the new Listener to be registered.
     */
    public void addListener(Property.ReadOnlyStatusChangeListener listener) {
        if (readOnlyStatusChangeListeners == null) {
            readOnlyStatusChangeListeners = new LinkedList();
        }
        readOnlyStatusChangeListeners.add(listener);
    }

    /**
     * Removes a previously registered read-only status change listener.
     * 
     * @param listener
     *                the listener to be removed.
     */
    public void removeListener(Property.ReadOnlyStatusChangeListener listener) {
        if (readOnlyStatusChangeListeners != null) {
            readOnlyStatusChangeListeners.remove(listener);
        }
    }

    /**
     * Sends a read only status change event to all registered listeners.
     */
    private void fireReadOnlyStatusChange() {
        if (readOnlyStatusChangeListeners != null) {
            final Object[] l = readOnlyStatusChangeListeners.toArray();
            final Property.ReadOnlyStatusChangeEvent event = new MethodProperty.ReadOnlyStatusChangeEvent(
                    this);
            for (int i = 0; i < l.length; i++) {
                ((Property.ReadOnlyStatusChangeListener) l[i])
                        .readOnlyStatusChange(event);
            }
        }
    }

}
