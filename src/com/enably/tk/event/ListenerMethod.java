/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.enably.tk.event;

import java.util.EventListener;
import java.util.EventObject;
import java.lang.reflect.Method;

/** <p>One registered event listener. This class contains the listener
 * object reference, listened event type, the trigger method to call when
 * the event fires, and the optional argument list to pass to the method and
 * the index of the argument to replace with the event object. It provides
 * several constructors that allow omission of the optional arguments, and
 * giving the listener method directly, or having the constructor to reflect
 * it using merely the name of the method.</p>
 * 
 * <p>It should be pointed out that the method
 * {@link #receiveEvent(EventObject event)} is the one that filters out the
 * events that do not match with the given event type and thus do not result
 * in calling of the trigger method.</p>
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class ListenerMethod implements EventListener {

	/** Type of the event that should trigger this listener. Also the
	 * subclasses of this class are accepted to trigger the listener.
	 */
	private Class eventType;

	/** The object containing the trigger method, */
	private Object object;

	/** The trigger method to call when an event passing the given criteria
	 * fires.
	 */
	private Method method;

	/** Optional argument set to pass to the trigger method. */
	private Object[] arguments;

	/** Optional index to <code>arguments</code> that point out which one
	 * should be replaced with the triggering event object and thus be
	 * passed to the trigger method.
	 */
	private int eventArgumentIndex;

	/** <p>Constructs a new event listener from a trigger method, it's
	 * arguments and the argument index specifying which one is replaced
	 * with the event object when the trigger method is called.</p>
	 * 
	 * <p>This constructor gets the trigger method as a parameter so it
	 * does not need to reflect to find it out.</p>
	 * 
	 * @param eventType The event type that is listener listens to. All
	 * events of this kind (or its subclasses) result in calling the trigger
	 * method.
	 * @param object The object instance that contains the trigger method
	 * @param method the trigger method
	 * @param arguments arguments to be passed to the trigger method
	 * @param eventArgumentIndex An index to the argument list. This index
	 * points out the argument that is replaced with the event object before
	 * the argument set is passed to the trigger method. If 
	 * <code>eventArgumentIndex</code> is negative, the triggering event
	 * object will not be passed to the trigger method, though it is still
	 * called.
	 * @throws java.lang.IllegalArgumentException if <code>method</code>
	 * is not a member of <code>object</code>.
	 */
	public ListenerMethod(
		Class eventType,
		Object object,
		Method method,
		Object[] arguments,
		int eventArgumentIndex)
		throws java.lang.IllegalArgumentException {

		// Check that the object is of correct type
		if (!method.getDeclaringClass().isAssignableFrom(object.getClass()))
			throw new java.lang.IllegalArgumentException();

		// Check that the event argument is null
		if (eventArgumentIndex >= 0 && arguments[eventArgumentIndex] != null)
			throw new java.lang.IllegalArgumentException();

		// Check the event type is supported by the method
		if (eventArgumentIndex >= 0
			&& !method.getParameterTypes()[eventArgumentIndex].isAssignableFrom(
				eventType))
			throw new java.lang.IllegalArgumentException();

		this.eventType = eventType;
		this.object = object;
		this.method = method;
		this.arguments = arguments;
		this.eventArgumentIndex = eventArgumentIndex;
	}

	/** <p>Constructs a new event listener from a trigger method name, it's
	 * arguments and the argument index specifying which one is replaced
	 * with the event object. The actual trigger method is reflected from
	 * <code>object</code>, and
	 * <code>java.lang.IllegalArgumentException</code> is thrown unless
	 * exactly one match is found.
	 * 
	 * @param eventType The event type that is listener listens to. All
	 * events of this kind (or its subclasses) result in calling the trigger
	 * method.
	 * @param object The object instance that contains the trigger method
	 * @param methodName The name of the trigger method. If
	 * <code>object</code> does not contain the method or it contains more
	 * than one matching methods
	 * <code>java.lang.IllegalArgumentException</code> is thrown.
	 * @param arguments arguments to be passed to the trigger method
	 * @param eventArgumentIndex An index to the argument list. This index
	 * points out the argument that is replaced with the event object before
	 * the argument set is passed to the trigger method. If 
	 * <code>eventArgumentIndex</code> is negative, the triggering event
	 * object will not be passed to the trigger method, though it is still
	 * called.
	 * @throws java.lang.IllegalArgumentException unless exactly one match
	 * <code>methodName</code> is found in <code>object</code>.
	 */
	public ListenerMethod(
		Class eventType,
		Object object,
		String methodName,
		Object[] arguments,
		int eventArgumentIndex)
		throws java.lang.IllegalArgumentException {

		// Find the correct method
		Method[] methods = object.getClass().getMethods();
		Method method = null;
		for (int i = 0; i < methods.length; i++)
			if (methods[i].getName().equals(methodName))
				method = methods[i];
		if (method == null)
			throw new IllegalArgumentException();

		// Check that the event argument is null
		if (eventArgumentIndex >= 0 && arguments[eventArgumentIndex] != null)
			throw new java.lang.IllegalArgumentException();

		// Check the event type is supported by the method
		if (eventArgumentIndex >= 0
			&& !method.getParameterTypes()[eventArgumentIndex].isAssignableFrom(
				eventType))
			throw new java.lang.IllegalArgumentException();

		this.eventType = eventType;
		this.object = object;
		this.method = method;
		this.arguments = arguments;
		this.eventArgumentIndex = eventArgumentIndex;
	}

	/** <p>Constructs a new event listener from the trigger method and it's
	 * arguments. Since the the index to the replaced parameter is not
	 * specified the event triggering this listener will not be passed to
	 * the trigger method.</p>
	 * 
	 * <p>This constructor gets the trigger method as a parameter so it
	 * does not need to reflect to find it out.</p>
	 * 
	 * @param eventType The event type that is listener listens to. All
	 * events of this kind (or its subclasses) result in calling the trigger
	 * method.
	 * @param object The object instance that contains the trigger method
	 * @param method the trigger method
	 * @param arguments arguments to be passed to the trigger method
	 * @throws java.lang.IllegalArgumentException if <code>method</code>
	 * is not a member of <code>object</code>.
	 */
	public ListenerMethod(
		Class eventType,
		Object object,
		Method method,
		Object[] arguments)
		throws java.lang.IllegalArgumentException {

		// Check that the object is of correct type
		if (!method.getDeclaringClass().isAssignableFrom(object.getClass()))
			throw new java.lang.IllegalArgumentException();

		this.eventType = eventType;
		this.object = object;
		this.method = method;
		this.arguments = arguments;
		this.eventArgumentIndex = -1;
	}

	/** <p>Constructs a new event listener from a trigger method name and
	 * it's arguments. Since the the index to the replaced parameter is not
	 * specified the event triggering this listener will not be passed to
	 * the trigger method.</p>
	 * 
	 * <p>The actual trigger method is reflected from <code>object</code>,
	 * and <code>java.lang.IllegalArgumentException</code> is thrown unless
	 * exactly one match is found.</p>
	 * 
	 * @param eventType The event type that is listener listens to. All
	 * events of this kind (or its subclasses) result in calling the trigger
	 * method.
	 * @param object The object instance that contains the trigger method
	 * @param methodName The name of the trigger method. If
	 * <code>object</code> does not contain the method or it contains more
	 * than one matching methods
	 * <code>java.lang.IllegalArgumentException</code> is thrown.
	 * @param arguments arguments to be passed to the trigger method
	 * @throws java.lang.IllegalArgumentException unless exactly one match
	 * <code>methodName</code> is found in <code>object</code>.
	 */
	public ListenerMethod(
		Class eventType,
		Object object,
		String methodName,
		Object[] arguments)
		throws java.lang.IllegalArgumentException {

		// Find the correct method
		Method[] methods = object.getClass().getMethods();
		Method method = null;
		for (int i = 0; i < methods.length; i++)
			if (methods[i].getName().equals(methodName))
				method = methods[i];
		if (method == null)
			throw new IllegalArgumentException();

		this.eventType = eventType;
		this.object = object;
		this.method = method;
		this.arguments = arguments;
		this.eventArgumentIndex = -1;
	}

	/** <p>Constructs a new event listener from a trigger method. Since the
	 * argument list is unspecified no parameters are passed to the trigger
	 * method when the listener is triggered.</p>
	 * 
	 * <p>This constructor gets the trigger method as a parameter so it
	 * does not need to reflect to find it out.</p>
	 * 
	 * @param eventType The event type that is listener listens to. All
	 * events of this kind (or its subclasses) result in calling the trigger
	 * method.
	 * @param object The object instance that contains the trigger method
	 * @param method the trigger method
	 * @throws java.lang.IllegalArgumentException if <code>method</code>
	 * is not a member of <code>object</code>.
	 */
	public ListenerMethod(Class eventType, Object object, Method method)
		throws java.lang.IllegalArgumentException {

		// Check that the object is of correct type
		if (!method.getDeclaringClass().isAssignableFrom(object.getClass()))
			throw new java.lang.IllegalArgumentException();

		this.eventType = eventType;
		this.object = object;
		this.method = method;
		this.eventArgumentIndex = -1;

		Class[] params = method.getParameterTypes();

		if (params.length == 0)
			this.arguments = new Object[0];
		else if (params.length == 1 && params[0].isAssignableFrom(eventType)) {
			this.arguments = new Object[] { null };
			this.eventArgumentIndex = 0;
		} else
			throw new IllegalArgumentException();
	}

	/** <p>Constructs a new event listener from a trigger method name. Since
	 * the argument list is unspecified no parameters are passed to the
	 * trigger method when the listener is triggered.</p>
	 * 
	 * <p>The actual trigger method is reflected from <code>object</code>,
	 * and <code>java.lang.IllegalArgumentException</code> is thrown unless
	 * exactly one match is found.</p>
	 * 
	 * @param eventType The event type that is listener listens to. All
	 * events of this kind (or its subclasses) result in calling the trigger
	 * method.
	 * @param object The object instance that contains the trigger method
	 * @param methodName The name of the trigger method. If
	 * <code>object</code> does not contain the method or it contains more
	 * than one matching methods
	 * <code>java.lang.IllegalArgumentException</code> is thrown.
	 * @throws java.lang.IllegalArgumentException unless exactly one match
	 * <code>methodName</code> is found in <code>object</code>.
	 */
	public ListenerMethod(Class eventType, Object object, String methodName)
		throws java.lang.IllegalArgumentException {

		// Find the correct method
		Method[] methods = object.getClass().getMethods();
		Method method = null;
		for (int i = 0; i < methods.length; i++)
			if (methods[i].getName().equals(methodName))
				method = methods[i];
		if (method == null)
			throw new IllegalArgumentException();

		this.eventType = eventType;
		this.object = object;
		this.method = method;
		this.eventArgumentIndex = -1;

		Class[] params = method.getParameterTypes();

		if (params.length == 0)
			this.arguments = new Object[0];
		else if (params.length == 1 && params[0].isAssignableFrom(eventType)) {
			this.arguments = new Object[] { null };
			this.eventArgumentIndex = 0;
		} else
			throw new IllegalArgumentException();
	}

	/** Receives one event from the EventRouter and calls the trigger
	 * method if it matches with the criteria defined for the listener.
	 * Only the events of the same or subclass of the specified event
	 * class result in the trigger method to be called.
	 * 
	 * @param event The fired event. Unless the trigger method's
	 * argument list and the index to the to be replaced argument is
	 * specified, this event will not be passed to the trigger method.
	 */
	public void receiveEvent(EventObject event) {

		// Only send events supported by the method
		if (eventType.isAssignableFrom(event.getClass())) {
			try {
				if (eventArgumentIndex >= 0) {
					if (eventArgumentIndex == 0 && arguments.length == 1)
						method.invoke(object, new Object[] { event });
					else {
						Object[] arg = new Object[arguments.length];
						for (int i = 0; i < arg.length; i++)
							arg[i] = arguments[i];
						arg[eventArgumentIndex] = event;
						method.invoke(object, arg);
					}
				} else
					method.invoke(object, arguments);

			} catch (java.lang.IllegalAccessException e) {
				// This should never happen
				throw new java.lang.RuntimeException(
					"Internal error - please report: " + e.toString());
			} catch (java.lang.reflect.InvocationTargetException e) {
				// This should never happen
				throw new MethodException(
					"Invocation if method " + method + " failed.",
					e.getTargetException());
			}
		}
	}

	/** Checks if the given object and event match with the ones stored
	 * in this listener.
	 * 
	 * @param target object to be matched against the object stored by this
	 * listener
	 * @param eventType type to be tested for equality against the type
	 * stored by this listener
	 * @return <code>true</code> if <code>target</code> is the same object
	 * as the one stored in this object and <code>eventType</code> equals
	 * the event type stored in this object.
	 */
	public boolean matches(Class eventType, Object target) {
		return (target == object) && (eventType.equals(this.eventType));
	}

	/** Checks if the given object, event and method match with the ones
	 * stored in this listener.
	 * 
	 * @param target object to be matched against the object stored by this
	 * listener
	 * @param eventType type to be tested for equality against the type
	 * stored by this listener
	 * @param method method to be tested for equality against the method
	 * stored by this listener
	 * @return <code>true</code> if <code>target</code> is the same object
	 * as the one stored in this object, <code>eventType</code> equals
	 * with the event type stored in this object and <code>method</code>
	 * equals with the method stored in this object
	 */
	public boolean matches(Class eventType, Object target, Method method) {
		return (target == object)
			&& (eventType.equals(this.eventType) && method.equals(this.method));
	}

	/** Exception that wraps an exception thrown by an invoked method. 
	 *  When ListenerMethod invokes the target method, it may throw arbitrary
	 *  exception. The original exception is wrapped into MethodException instance and
	 *  rethrown by the ListenerMethod.
	 * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
	 * */
	public class MethodException extends RuntimeException {

		/**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3257005445242894135L;
        
        private Throwable cause;
		private String message;

		private MethodException(String message, Throwable cause) {
			super(message);
			this.cause = cause;
		}

		public Throwable getCause() {
			return this.cause;
		}

		/**
		 * @see java.lang.Throwable#getMessage()
		 */
		public String getMessage() {
			return message;
		}

		public String toString() {
			String msg = super.toString();
			if (cause != null)
				msg += "\nCause: " + cause.toString();
			return msg;
		}

	}
}
