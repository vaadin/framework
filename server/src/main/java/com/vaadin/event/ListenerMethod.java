/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.event;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EventListener;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * One registered event listener. This class contains the listener object
 * reference, listened event type, the trigger method to call when the event
 * fires, and the optional argument list to pass to the method and the index of
 * the argument to replace with the event object.
 * </p>
 * 
 * <p>
 * This Class provides several constructors that allow omission of the optional
 * arguments, and giving the listener method directly, or having the constructor
 * to reflect it using merely the name of the method.
 * </p>
 * 
 * <p>
 * It should be pointed out that the method
 * {@link #receiveEvent(EventObject event)} is the one that filters out the
 * events that do not match with the given event type and thus do not result in
 * calling of the trigger method.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ListenerMethod implements EventListener, Serializable {

    /**
     * Type of the event that should trigger this listener. Also the subclasses
     * of this class are accepted to trigger the listener.
     */
    private final Class<?> eventType;

    /**
     * The object containing the trigger method.
     */
    private final Object target;

    /**
     * The trigger method to call when an event passing the given criteria
     * fires.
     */
    private transient Method method;

    /**
     * Optional argument set to pass to the trigger method.
     */
    private Object[] arguments;

    /**
     * Optional index to <code>arguments</code> that point out which one should
     * be replaced with the triggering event object and thus be passed to the
     * trigger method.
     */
    private int eventArgumentIndex;

    /* Special serialization to handle method references */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        try {
            out.defaultWriteObject();
            String name = method.getName();
            Class<?>[] paramTypes = method.getParameterTypes();
            out.writeObject(name);
            out.writeObject(paramTypes);
        } catch (NotSerializableException e) {
            getLogger()
                    .log(Level.WARNING,
                            "Error in serialization of the application: Class {0} must implement serialization.",
                            target.getClass().getName());
            throw e;
        }

    }

    /* Special serialization to handle method references */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        try {
            String name = (String) in.readObject();
            Class<?>[] paramTypes = (Class<?>[]) in.readObject();
            // We can not use getMethod directly as we want to support anonymous
            // inner classes
            method = findHighestMethod(target.getClass(), name, paramTypes);
        } catch (SecurityException e) {
            getLogger().log(Level.SEVERE, "Internal deserialization error", e);
        }
    }

    private static Method findHighestMethod(Class<?> cls, String method,
            Class<?>[] paramTypes) {
        Class<?>[] ifaces = cls.getInterfaces();
        for (int i = 0; i < ifaces.length; i++) {
            Method ifaceMethod = findHighestMethod(ifaces[i], method,
                    paramTypes);
            if (ifaceMethod != null) {
                return ifaceMethod;
            }
        }
        if (cls.getSuperclass() != null) {
            Method parentMethod = findHighestMethod(cls.getSuperclass(),
                    method, paramTypes);
            if (parentMethod != null) {
                return parentMethod;
            }
        }
        Method[] methods = cls.getMethods();
        for (int i = 0; i < methods.length; i++) {
            // we ignore parameter types for now - you need to add this
            if (methods[i].getName().equals(method)) {
                return methods[i];
            }
        }
        return null;
    }

    /**
     * <p>
     * Constructs a new event listener from a trigger method, it's arguments and
     * the argument index specifying which one is replaced with the event object
     * when the trigger method is called.
     * </p>
     * 
     * <p>
     * This constructor gets the trigger method as a parameter so it does not
     * need to reflect to find it out.
     * </p>
     * 
     * @param eventType
     *            the event type that is listener listens to. All events of this
     *            kind (or its subclasses) result in calling the trigger method.
     * @param target
     *            the object instance that contains the trigger method
     * @param method
     *            the trigger method
     * @param arguments
     *            the arguments to be passed to the trigger method
     * @param eventArgumentIndex
     *            An index to the argument list. This index points out the
     *            argument that is replaced with the event object before the
     *            argument set is passed to the trigger method. If the
     *            eventArgumentIndex is negative, the triggering event object
     *            will not be passed to the trigger method, though it is still
     *            called.
     * @throws java.lang.IllegalArgumentException
     *             if <code>method</code> is not a member of <code>target</code>
     *             .
     */
    public ListenerMethod(Class<?> eventType, Object target, Method method,
            Object[] arguments, int eventArgumentIndex)
            throws java.lang.IllegalArgumentException {

        // Checks that the object is of correct type
        if (!method.getDeclaringClass().isAssignableFrom(target.getClass())) {
            throw new java.lang.IllegalArgumentException("The method "
                    + method.getName()
                    + " cannot be used for the given target: "
                    + target.getClass().getName());
        }

        // Checks that the event argument is null
        if (eventArgumentIndex >= 0 && arguments[eventArgumentIndex] != null) {
            throw new java.lang.IllegalArgumentException("argument["
                    + eventArgumentIndex + "] must be null");
        }

        // Checks the event type is supported by the method
        if (eventArgumentIndex >= 0
                && !method.getParameterTypes()[eventArgumentIndex]
                        .isAssignableFrom(eventType)) {
            throw new java.lang.IllegalArgumentException("The method "
                    + method.getName()
                    + " does not accept the given eventType: "
                    + eventType.getName());
        }

        this.eventType = eventType;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.eventArgumentIndex = eventArgumentIndex;
    }

    /**
     * <p>
     * Constructs a new event listener from a trigger method name, it's
     * arguments and the argument index specifying which one is replaced with
     * the event object. The actual trigger method is reflected from
     * <code>object</code>, and <code>java.lang.IllegalArgumentException</code>
     * is thrown unless exactly one match is found.
     * </p>
     * 
     * @param eventType
     *            the event type that is listener listens to. All events of this
     *            kind (or its subclasses) result in calling the trigger method.
     * @param target
     *            the object instance that contains the trigger method.
     * @param methodName
     *            the name of the trigger method. If the object does not contain
     *            the method or it contains more than one matching methods
     *            <code>java.lang.IllegalArgumentException</code> is thrown.
     * @param arguments
     *            the arguments to be passed to the trigger method.
     * @param eventArgumentIndex
     *            An index to the argument list. This index points out the
     *            argument that is replaced with the event object before the
     *            argument set is passed to the trigger method. If the
     *            eventArgumentIndex is negative, the triggering event object
     *            will not be passed to the trigger method, though it is still
     *            called.
     * @throws java.lang.IllegalArgumentException
     *             unless exactly one match <code>methodName</code> is found in
     *             <code>target</code>.
     */
    public ListenerMethod(Class<?> eventType, Object target, String methodName,
            Object[] arguments, int eventArgumentIndex)
            throws java.lang.IllegalArgumentException {

        // Finds the correct method
        final Method[] methods = target.getClass().getMethods();
        Method method = null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                method = methods[i];
            }
        }
        if (method == null) {
            throw new IllegalArgumentException("Method " + methodName
                    + " not found in class " + target.getClass().getName());
        }

        // Checks that the event argument is null
        if (eventArgumentIndex >= 0 && arguments[eventArgumentIndex] != null) {
            throw new java.lang.IllegalArgumentException("argument["
                    + eventArgumentIndex + "] must be null");
        }

        // Checks the event type is supported by the method
        if (eventArgumentIndex >= 0
                && !method.getParameterTypes()[eventArgumentIndex]
                        .isAssignableFrom(eventType)) {
            throw new java.lang.IllegalArgumentException("The method "
                    + method.getName()
                    + " does not accept the given eventType: "
                    + eventType.getName());
        }

        this.eventType = eventType;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.eventArgumentIndex = eventArgumentIndex;
    }

    /**
     * <p>
     * Constructs a new event listener from the trigger method and it's
     * arguments. Since the the index to the replaced parameter is not specified
     * the event triggering this listener will not be passed to the trigger
     * method.
     * </p>
     * 
     * <p>
     * This constructor gets the trigger method as a parameter so it does not
     * need to reflect to find it out.
     * </p>
     * 
     * @param eventType
     *            the event type that is listener listens to. All events of this
     *            kind (or its subclasses) result in calling the trigger method.
     * @param target
     *            the object instance that contains the trigger method.
     * @param method
     *            the trigger method.
     * @param arguments
     *            the arguments to be passed to the trigger method.
     * @throws java.lang.IllegalArgumentException
     *             if <code>method</code> is not a member of <code>target</code>
     *             .
     */
    public ListenerMethod(Class<?> eventType, Object target, Method method,
            Object[] arguments) throws java.lang.IllegalArgumentException {

        // Check that the object is of correct type
        if (!method.getDeclaringClass().isAssignableFrom(target.getClass())) {
            throw new java.lang.IllegalArgumentException("The method "
                    + method.getName()
                    + " cannot be used for the given target: "
                    + target.getClass().getName());
        }

        this.eventType = eventType;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        eventArgumentIndex = -1;
    }

    /**
     * <p>
     * Constructs a new event listener from a trigger method name and it's
     * arguments. Since the the index to the replaced parameter is not specified
     * the event triggering this listener will not be passed to the trigger
     * method.
     * </p>
     * 
     * <p>
     * The actual trigger method is reflected from <code>target</code>, and
     * <code>java.lang.IllegalArgumentException</code> is thrown unless exactly
     * one match is found.
     * </p>
     * 
     * @param eventType
     *            the event type that is listener listens to. All events of this
     *            kind (or its subclasses) result in calling the trigger method.
     * @param target
     *            the object instance that contains the trigger method.
     * @param methodName
     *            the name of the trigger method. If the object does not contain
     *            the method or it contains more than one matching methods
     *            <code>java.lang.IllegalArgumentException</code> is thrown.
     * @param arguments
     *            the arguments to be passed to the trigger method.
     * @throws java.lang.IllegalArgumentException
     *             unless exactly one match <code>methodName</code> is found in
     *             <code>object</code>.
     */
    public ListenerMethod(Class<?> eventType, Object target, String methodName,
            Object[] arguments) throws java.lang.IllegalArgumentException {

        // Find the correct method
        final Method[] methods = target.getClass().getMethods();
        Method method = null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                method = methods[i];
            }
        }
        if (method == null) {
            throw new IllegalArgumentException("Method " + methodName
                    + " not found in class " + target.getClass().getName());
        }

        this.eventType = eventType;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        eventArgumentIndex = -1;
    }

    /**
     * <p>
     * Constructs a new event listener from a trigger method. Since the argument
     * list is unspecified no parameters are passed to the trigger method when
     * the listener is triggered.
     * </p>
     * 
     * <p>
     * This constructor gets the trigger method as a parameter so it does not
     * need to reflect to find it out.
     * </p>
     * 
     * @param eventType
     *            the event type that is listener listens to. All events of this
     *            kind (or its subclasses) result in calling the trigger method.
     * @param target
     *            the object instance that contains the trigger method.
     * @param method
     *            the trigger method.
     * @throws java.lang.IllegalArgumentException
     *             if <code>method</code> is not a member of <code>object</code>
     *             .
     */
    public ListenerMethod(Class<?> eventType, Object target, Method method)
            throws java.lang.IllegalArgumentException {

        // Checks that the object is of correct type
        if (!method.getDeclaringClass().isAssignableFrom(target.getClass())) {
            throw new java.lang.IllegalArgumentException("The method "
                    + method.getName()
                    + " cannot be used for the given target: "
                    + target.getClass().getName());
        }

        this.eventType = eventType;
        this.target = target;
        this.method = method;
        eventArgumentIndex = -1;

        final Class<?>[] params = method.getParameterTypes();

        if (params.length == 0) {
            arguments = new Object[0];
        } else if (params.length == 1 && params[0].isAssignableFrom(eventType)) {
            arguments = new Object[] { null };
            eventArgumentIndex = 0;
        } else {
            throw new IllegalArgumentException(
                    "Method requires unknown parameters");
        }
    }

    /**
     * <p>
     * Constructs a new event listener from a trigger method name. Since the
     * argument list is unspecified no parameters are passed to the trigger
     * method when the listener is triggered.
     * </p>
     * 
     * <p>
     * The actual trigger method is reflected from <code>object</code>, and
     * <code>java.lang.IllegalArgumentException</code> is thrown unless exactly
     * one match is found.
     * </p>
     * 
     * @param eventType
     *            the event type that is listener listens to. All events of this
     *            kind (or its subclasses) result in calling the trigger method.
     * @param target
     *            the object instance that contains the trigger method.
     * @param methodName
     *            the name of the trigger method. If the object does not contain
     *            the method or it contains more than one matching methods
     *            <code>java.lang.IllegalArgumentException</code> is thrown.
     * @throws java.lang.IllegalArgumentException
     *             unless exactly one match <code>methodName</code> is found in
     *             <code>target</code>.
     */
    public ListenerMethod(Class<?> eventType, Object target, String methodName)
            throws java.lang.IllegalArgumentException {

        // Finds the correct method
        final Method[] methods = target.getClass().getMethods();
        Method method = null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                method = methods[i];
            }
        }
        if (method == null) {
            throw new IllegalArgumentException("Method " + methodName
                    + " not found in class " + target.getClass().getName());
        }

        this.eventType = eventType;
        this.target = target;
        this.method = method;
        eventArgumentIndex = -1;

        final Class<?>[] params = method.getParameterTypes();

        if (params.length == 0) {
            arguments = new Object[0];
        } else if (params.length == 1 && params[0].isAssignableFrom(eventType)) {
            arguments = new Object[] { null };
            eventArgumentIndex = 0;
        } else {
            throw new IllegalArgumentException(
                    "Method requires unknown parameters");
        }
    }

    /**
     * Receives one event from the <code>EventRouter</code> and calls the
     * trigger method if it matches with the criteria defined for the listener.
     * Only the events of the same or subclass of the specified event class
     * result in the trigger method to be called.
     * 
     * @param event
     *            the fired event. Unless the trigger method's argument list and
     *            the index to the to be replaced argument is specified, this
     *            event will not be passed to the trigger method.
     */
    public void receiveEvent(EventObject event) {
        // Only send events supported by the method
        if (eventType.isAssignableFrom(event.getClass())) {
            try {
                if (eventArgumentIndex >= 0) {
                    if (eventArgumentIndex == 0 && arguments.length == 1) {
                        method.invoke(target, new Object[] { event });
                    } else {
                        final Object[] arg = new Object[arguments.length];
                        for (int i = 0; i < arg.length; i++) {
                            arg[i] = arguments[i];
                        }
                        arg[eventArgumentIndex] = event;
                        method.invoke(target, arg);
                    }
                } else {
                    method.invoke(target, arguments);
                }

            } catch (final java.lang.IllegalAccessException e) {
                // This should never happen
                throw new java.lang.RuntimeException(
                        "Internal error - please report", e);
            } catch (final java.lang.reflect.InvocationTargetException e) {
                // An exception was thrown by the invocation target. Throw it
                // forwards.
                throw new MethodException("Invocation of method "
                        + method.getName() + " in "
                        + target.getClass().getName() + " failed.",
                        e.getTargetException());
            }
        }
    }

    /**
     * Checks if the given object and event match with the ones stored in this
     * listener.
     * 
     * @param target
     *            the object to be matched against the object stored by this
     *            listener.
     * @param eventType
     *            the type to be tested for equality against the type stored by
     *            this listener.
     * @return <code>true</code> if <code>target</code> is the same object as
     *         the one stored in this object and <code>eventType</code> equals
     *         the event type stored in this object. *
     */
    public boolean matches(Class<?> eventType, Object target) {
        return (this.target == target) && (eventType.equals(this.eventType));
    }

    /**
     * Checks if the given object, event and method match with the ones stored
     * in this listener.
     * 
     * @param target
     *            the object to be matched against the object stored by this
     *            listener.
     * @param eventType
     *            the type to be tested for equality against the type stored by
     *            this listener.
     * @param method
     *            the method to be tested for equality against the method stored
     *            by this listener.
     * @return <code>true</code> if <code>target</code> is the same object as
     *         the one stored in this object, <code>eventType</code> equals with
     *         the event type stored in this object and <code>method</code>
     *         equals with the method stored in this object
     */
    public boolean matches(Class<?> eventType, Object target, Method method) {
        return (this.target == target)
                && (eventType.equals(this.eventType) && method
                        .equals(this.method));
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + eventArgumentIndex;
        hash = 31 * hash + (eventType == null ? 0 : eventType.hashCode());
        hash = 31 * hash + (target == null ? 0 : target.hashCode());
        hash = 31 * hash + (method == null ? 0 : method.hashCode());

        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        // return false if obj is a subclass (do not use instanceof check)
        if ((obj == null) || (obj.getClass() != getClass())) {
            return false;
        }

        // obj is of same class, test it further
        ListenerMethod t = (ListenerMethod) obj;

        return eventArgumentIndex == t.eventArgumentIndex
                && (eventType == t.eventType || (eventType != null && eventType
                        .equals(t.eventType)))
                && (target == t.target || (target != null && target
                        .equals(t.target)))
                && (method == t.method || (method != null && method
                        .equals(t.method)))
                && (arguments == t.arguments || (Arrays.equals(arguments,
                        t.arguments)));
    }

    /**
     * Exception that wraps an exception thrown by an invoked method. When
     * <code>ListenerMethod</code> invokes the target method, it may throw
     * arbitrary exception. The original exception is wrapped into
     * MethodException instance and rethrown by the <code>ListenerMethod</code>.
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public class MethodException extends RuntimeException implements
            Serializable {

        private MethodException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    /**
     * Compares the type of this ListenerMethod to the given type
     * 
     * @param eventType
     *            The type to compare with
     * @return true if this type of this ListenerMethod matches the given type,
     *         false otherwise
     */
    public boolean isType(Class<?> eventType) {
        return this.eventType == eventType;
    }

    /**
     * Compares the type of this ListenerMethod to the given type
     * 
     * @param eventType
     *            The type to compare with
     * @return true if this event type can be assigned to the given type, false
     *         otherwise
     */
    public boolean isOrExtendsType(Class<?> eventType) {
        return eventType.isAssignableFrom(this.eventType);
    }

    /**
     * Returns the target object which contains the trigger method.
     * 
     * @return The target object
     */
    public Object getTarget() {
        return target;
    }

    private static final Logger getLogger() {
        return Logger.getLogger(ListenerMethod.class.getName());
    }

}
