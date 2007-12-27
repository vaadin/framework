/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.event;

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.EventObject;

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
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class ListenerMethod implements EventListener {

    /**
     * Type of the event that should trigger this listener. Also the subclasses
     * of this class are accepted to trigger the listener.
     */
    private final Class eventType;

    /**
     * The object containing the trigger method.
     */
    private final Object object;

    /**
     * The trigger method to call when an event passing the given criteria
     * fires.
     */
    private final Method method;

    /**
     * Optional argument set to pass to the trigger method.
     */
    private Object[] arguments;

    /**
     * Optional index to <code>arguments</code> that point out which one
     * should be replaced with the triggering event object and thus be passed to
     * the trigger method.
     */
    private int eventArgumentIndex;

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
     *                the event type that is listener listens to. All events of
     *                this kind (or its subclasses) result in calling the
     *                trigger method.
     * @param object
     *                the object instance that contains the trigger method
     * @param method
     *                the trigger method
     * @param arguments
     *                the arguments to be passed to the trigger method
     * @param eventArgumentIndex
     *                An index to the argument list. This index points out the
     *                argument that is replaced with the event object before the
     *                argument set is passed to the trigger method. If the
     *                eventArgumentIndex is negative, the triggering event
     *                object will not be passed to the trigger method, though it
     *                is still called.
     * @throws java.lang.IllegalArgumentException
     *                 if <code>method</code> is not a member of
     *                 <code>object</code>.
     */
    public ListenerMethod(Class eventType, Object object, Method method,
            Object[] arguments, int eventArgumentIndex)
            throws java.lang.IllegalArgumentException {

        // Checks that the object is of correct type
        if (!method.getDeclaringClass().isAssignableFrom(object.getClass())) {
            throw new java.lang.IllegalArgumentException();
        }

        // Checks that the event argument is null
        if (eventArgumentIndex >= 0 && arguments[eventArgumentIndex] != null) {
            throw new java.lang.IllegalArgumentException();
        }

        // Checks the event type is supported by the method
        if (eventArgumentIndex >= 0
                && !method.getParameterTypes()[eventArgumentIndex]
                        .isAssignableFrom(eventType)) {
            throw new java.lang.IllegalArgumentException();
        }

        this.eventType = eventType;
        this.object = object;
        this.method = method;
        this.arguments = arguments;
        this.eventArgumentIndex = eventArgumentIndex;
    }

    /**
     * <p>
     * Constructs a new event listener from a trigger method name, it's
     * arguments and the argument index specifying which one is replaced with
     * the event object. The actual trigger method is reflected from
     * <code>object</code>, and
     * <code>java.lang.IllegalArgumentException</code> is thrown unless
     * exactly one match is found.
     * </p>
     * 
     * @param eventType
     *                the event type that is listener listens to. All events of
     *                this kind (or its subclasses) result in calling the
     *                trigger method.
     * @param object
     *                the object instance that contains the trigger method.
     * @param methodName
     *                the name of the trigger method. If the object does not
     *                contain the method or it contains more than one matching
     *                methods <code>java.lang.IllegalArgumentException</code>
     *                is thrown.
     * @param arguments
     *                the arguments to be passed to the trigger method.
     * @param eventArgumentIndex
     *                An index to the argument list. This index points out the
     *                argument that is replaced with the event object before the
     *                argument set is passed to the trigger method. If the
     *                eventArgumentIndex is negative, the triggering event
     *                object will not be passed to the trigger method, though it
     *                is still called.
     * @throws java.lang.IllegalArgumentException
     *                 unless exactly one match <code>methodName</code> is
     *                 found in <code>object</code>.
     */
    public ListenerMethod(Class eventType, Object object, String methodName,
            Object[] arguments, int eventArgumentIndex)
            throws java.lang.IllegalArgumentException {

        // Finds the correct method
        final Method[] methods = object.getClass().getMethods();
        Method method = null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                method = methods[i];
            }
        }
        if (method == null) {
            throw new IllegalArgumentException();
        }

        // Checks that the event argument is null
        if (eventArgumentIndex >= 0 && arguments[eventArgumentIndex] != null) {
            throw new java.lang.IllegalArgumentException();
        }

        // Checks the event type is supported by the method
        if (eventArgumentIndex >= 0
                && !method.getParameterTypes()[eventArgumentIndex]
                        .isAssignableFrom(eventType)) {
            throw new java.lang.IllegalArgumentException();
        }

        this.eventType = eventType;
        this.object = object;
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
     *                the event type that is listener listens to. All events of
     *                this kind (or its subclasses) result in calling the
     *                trigger method.
     * @param object
     *                the object instance that contains the trigger method.
     * @param method
     *                the trigger method.
     * @param arguments
     *                the arguments to be passed to the trigger method.
     * @throws java.lang.IllegalArgumentException
     *                 if <code>method</code> is not a member of
     *                 <code>object</code>.
     */
    public ListenerMethod(Class eventType, Object object, Method method,
            Object[] arguments) throws java.lang.IllegalArgumentException {

        // Check that the object is of correct type
        if (!method.getDeclaringClass().isAssignableFrom(object.getClass())) {
            throw new java.lang.IllegalArgumentException();
        }

        this.eventType = eventType;
        this.object = object;
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
     * The actual trigger method is reflected from <code>object</code>, and
     * <code>java.lang.IllegalArgumentException</code> is thrown unless
     * exactly one match is found.
     * </p>
     * 
     * @param eventType
     *                the event type that is listener listens to. All events of
     *                this kind (or its subclasses) result in calling the
     *                trigger method.
     * @param object
     *                the object instance that contains the trigger method.
     * @param methodName
     *                the name of the trigger method. If the object does not
     *                contain the method or it contains more than one matching
     *                methods <code>java.lang.IllegalArgumentException</code>
     *                is thrown.
     * @param arguments
     *                the arguments to be passed to the trigger method.
     * @throws java.lang.IllegalArgumentException
     *                 unless exactly one match <code>methodName</code> is
     *                 found in <code>object</code>.
     */
    public ListenerMethod(Class eventType, Object object, String methodName,
            Object[] arguments) throws java.lang.IllegalArgumentException {

        // Find the correct method
        final Method[] methods = object.getClass().getMethods();
        Method method = null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                method = methods[i];
            }
        }
        if (method == null) {
            throw new IllegalArgumentException();
        }

        this.eventType = eventType;
        this.object = object;
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
     *                the event type that is listener listens to. All events of
     *                this kind (or its subclasses) result in calling the
     *                trigger method.
     * @param object
     *                the object instance that contains the trigger method.
     * @param method
     *                the trigger method.
     * @throws java.lang.IllegalArgumentException
     *                 if <code>method</code> is not a member of
     *                 <code>object</code>.
     */
    public ListenerMethod(Class eventType, Object object, Method method)
            throws java.lang.IllegalArgumentException {

        // Checks that the object is of correct type
        if (!method.getDeclaringClass().isAssignableFrom(object.getClass())) {
            throw new java.lang.IllegalArgumentException();
        }

        this.eventType = eventType;
        this.object = object;
        this.method = method;
        eventArgumentIndex = -1;

        final Class[] params = method.getParameterTypes();

        if (params.length == 0) {
            arguments = new Object[0];
        } else if (params.length == 1 && params[0].isAssignableFrom(eventType)) {
            arguments = new Object[] { null };
            eventArgumentIndex = 0;
        } else {
            throw new IllegalArgumentException();
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
     * <code>java.lang.IllegalArgumentException</code> is thrown unless
     * exactly one match is found.
     * </p>
     * 
     * @param eventType
     *                the event type that is listener listens to. All events of
     *                this kind (or its subclasses) result in calling the
     *                trigger method.
     * @param object
     *                the object instance that contains the trigger method.
     * @param methodName
     *                the name of the trigger method. If the object does not
     *                contain the method or it contains more than one matching
     *                methods <code>java.lang.IllegalArgumentException</code>
     *                is thrown.
     * @throws java.lang.IllegalArgumentException
     *                 unless exactly one match <code>methodName</code> is
     *                 found in <code>object</code>.
     */
    public ListenerMethod(Class eventType, Object object, String methodName)
            throws java.lang.IllegalArgumentException {

        // Finds the correct method
        final Method[] methods = object.getClass().getMethods();
        Method method = null;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                method = methods[i];
            }
        }
        if (method == null) {
            throw new IllegalArgumentException();
        }

        this.eventType = eventType;
        this.object = object;
        this.method = method;
        eventArgumentIndex = -1;

        final Class[] params = method.getParameterTypes();

        if (params.length == 0) {
            arguments = new Object[0];
        } else if (params.length == 1 && params[0].isAssignableFrom(eventType)) {
            arguments = new Object[] { null };
            eventArgumentIndex = 0;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Receives one event from the <code>EventRouter</code> and calls the
     * trigger method if it matches with the criteria defined for the listener.
     * Only the events of the same or subclass of the specified event class
     * result in the trigger method to be called.
     * 
     * @param event
     *                the fired event. Unless the trigger method's argument list
     *                and the index to the to be replaced argument is specified,
     *                this event will not be passed to the trigger method.
     */
    public void receiveEvent(EventObject event) {

        // Only send events supported by the method
        if (eventType.isAssignableFrom(event.getClass())) {
            try {
                if (eventArgumentIndex >= 0) {
                    if (eventArgumentIndex == 0 && arguments.length == 1) {
                        method.invoke(object, new Object[] { event });
                    } else {
                        final Object[] arg = new Object[arguments.length];
                        for (int i = 0; i < arg.length; i++) {
                            arg[i] = arguments[i];
                        }
                        arg[eventArgumentIndex] = event;
                        method.invoke(object, arg);
                    }
                } else {
                    method.invoke(object, arguments);
                }

            } catch (final java.lang.IllegalAccessException e) {
                // This should never happen
                e.printStackTrace();
                throw new java.lang.RuntimeException(
                        "Internal error - please report: " + e.toString());
            } catch (final java.lang.reflect.InvocationTargetException e) {
                // This should never happen
                e.printStackTrace();
                throw new MethodException("Invocation if method " + method
                        + " failed.", e.getTargetException());
            }
        }
    }

    /**
     * Checks if the given object and event match with the ones stored in this
     * listener.
     * 
     * @param target
     *                the object to be matched against the object stored by this
     *                listener.
     * @param eventType
     *                the type to be tested for equality against the type stored
     *                by this listener.
     * @return <code>true</code> if <code>target</code> is the same object
     *         as the one stored in this object and <code>eventType</code>
     *         equals the event type stored in this object.
     */
    public boolean matches(Class eventType, Object target) {
        return (target == object) && (eventType.equals(this.eventType));
    }

    /**
     * Checks if the given object, event and method match with the ones stored
     * in this listener.
     * 
     * @param target
     *                the object to be matched against the object stored by this
     *                listener.
     * @param eventType
     *                the type to be tested for equality against the type stored
     *                by this listener.
     * @param method
     *                the method to be tested for equality against the method
     *                stored by this listener.
     * @return <code>true</code> if <code>target</code> is the same object
     *         as the one stored in this object, <code>eventType</code> equals
     *         with the event type stored in this object and <code>method</code>
     *         equals with the method stored in this object
     */
    public boolean matches(Class eventType, Object target, Method method) {
        return (target == object)
                && (eventType.equals(this.eventType) && method
                        .equals(this.method));
    }

    /**
     * Exception that wraps an exception thrown by an invoked method. When
     * <code>ListenerMethod</code> invokes the target method, it may throw
     * arbitrary exception. The original exception is wrapped into
     * MethodException instance and rethrown by the <code>ListenerMethod</code>.
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
        private static final long serialVersionUID = 3257005445242894135L;

        private final Throwable cause;

        private String message;

        private MethodException(String message, Throwable cause) {
            super(message);
            this.cause = cause;
        }

        /**
         * Retrieves the cause of this throwable or <code>null</code> if the
         * cause does not exist or not known.
         * 
         * @return the cause of this throwable or <code>null</code> if the
         *         cause is nonexistent or unknown.
         * @see java.lang.Throwable#getCause()
         */
        public Throwable getCause() {
            return cause;
        }

        /**
         * Returns the error message string of this throwable object.
         * 
         * @return the error message.
         * @see java.lang.Throwable#getMessage()
         */
        public String getMessage() {
            return message;
        }

        /**
         * @see java.lang.Throwable#toString()
         */
        public String toString() {
            String msg = super.toString();
            if (cause != null) {
                msg += "\nCause: " + cause.toString();
            }
            return msg;
        }

    }
}
