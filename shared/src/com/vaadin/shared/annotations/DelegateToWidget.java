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
package com.vaadin.shared.annotations;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Signals that the property value from a state class should be forwarded to the
 * Widget of the corresponding connector instance.
 * <p>
 * When this annotation is present on a field or on a setter method, the
 * framework will call the corresponding setter in the Connector's Widget
 * instance with the current state property value whenever it has been changed.
 * This is happens after firing
 * {@link com.vaadin.client.ConnectorHierarchyChangeEvent}s but before firing
 * any {@link com.vaadin.client.communication.StateChangeEvent}.
 * <p>
 * Take for example a state class looking like this:
 * 
 * <pre>
 * public class MyComponentState extends AbstractComponentState {
 *     &#064;DelegateToWidget
 *     public String myProperty;
 * }
 * </pre>
 * 
 * Whenever <code>myProperty</code> is changed, the framework will call code
 * like this:
 * 
 * <pre>
 * connector.getWidget().setMyProperty(connector.getState().myProperty);
 * </pre>
 * 
 * <p>
 * By default, the Widget method to call is derived from the property name, but
 * {@link #value()} in the annotation can be used to provide a custom method
 * name, e.g. {@code @DelegateToWidget("someSpecialName")}.
 * 
 * @since 7.0.0
 * @author Vaadin Ltd
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface DelegateToWidget {
    /**
     * Defines the name of the Widget method to call when the annotated state
     * property has changed. If no value is defined, the method name will be
     * derived from the property name, so e.g. a field named
     * <code>myProperty</code> will delegate to a method named
     * <code>setMyProperty</code>.
     * 
     * @return the name of the method to delegate to, or empty string to use the
     *         default name
     */
    public String value() default "";

    /**
     * Internal helper for handling default values in a uniform way both at
     * runtime and during widgetset compilation.
     */
    public static class Helper implements Serializable {
        /**
         * Gets the name of the method to delegate to for a given property name
         * and annotation value.
         * 
         * @param propertyName
         *            the name of the delegated property
         * @param annotationValue
         *            the {@link DelegateToWidget#value()} of the annotation
         * @return the name of the method to delegate to
         */
        public static String getDelegateTarget(String propertyName,
                String annotationValue) {
            String name = annotationValue;
            if (name.isEmpty()) {
                name = "set" + Character.toUpperCase(propertyName.charAt(0))
                        + propertyName.substring(1);
            }
            return name;
        }
    }
}
