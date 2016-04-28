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
package com.vaadin.client.metadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;

/**
 * Encapsulates the data that the widgetset compiler generates for supporting a
 * connector method annotated with {@link OnStateChange}
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class OnStateChangeMethod {

    private final String methodName;
    private final List<String> properties;
    private final Class<?> declaringClass;

    /**
     * Creates a new instance based on a method name, a list of parameters names
     * and a list of properties to listen for.
     * 
     * @param methodName
     *            the name of the method to call
     * @param properties
     *            an array of state property names to listen to
     */
    public OnStateChangeMethod(String methodName, String[] properties) {
        this(null, methodName, properties);
    }

    /**
     * Creates a new instance based on declaring class, a method name, a list of
     * parameters names and a list of properties to listen for.
     * <p>
     * If the declaring class is <code>null</code>, the method is found based on
     * the type of the connector that fired the state change event.
     * 
     * @param declaringClass
     *            the class in which the target method is declared, or
     *            <code>null</code> to use the class of the connector firing the
     *            event
     * @param methodName
     *            the name of the method to call
     * @param properties
     *            an array of state property names to listen to
     */
    public OnStateChangeMethod(Class<?> declaringClass, String methodName,
            String[] properties) {

        this.methodName = methodName;

        this.properties = Collections.unmodifiableList(Arrays
                .asList(properties));

        this.declaringClass = declaringClass;
    }

    /**
     * Invokes the listener method for a state change.
     * 
     * @param stateChangeEvent
     *            the state change event
     */
    public void invoke(StateChangeEvent stateChangeEvent) {
        ServerConnector connector = (ServerConnector) stateChangeEvent
                .getSource();

        Class<?> declaringClass = this.declaringClass;
        if (declaringClass == null) {
            declaringClass = connector.getClass();
        }
        Type declaringType = TypeDataStore.getType(declaringClass);

        try {
            declaringType.getMethod(methodName).invoke(connector);
        } catch (NoDataException e) {
            throw new RuntimeException("Couldn't invoke @OnStateChange method "
                    + declaringType.getSignature() + "." + methodName, e);
        }
    }

    /**
     * Gets the list of state property names to listen for.
     * 
     * @return the list of state property names to listen for
     */
    public List<String> getProperties() {
        return properties;
    }
}
