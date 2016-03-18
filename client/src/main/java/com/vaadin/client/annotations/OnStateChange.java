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
package com.vaadin.client.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.client.communication.StateChangeEvent;

/**
 * Marks a method in Connector classes that should be used to handle changes to
 * specific properties in the connector's shared state.
 * <p>
 * The annotated method will be called whenever at least one of the named state
 * properties have changed. If multiple listened properties are changed by the
 * same {@link StateChangeEvent}, the method will only be called once.
 * <p>
 * If there is no state variable with the provided name, the widgetset
 * compilation will fail.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface OnStateChange {
    /**
     * Defines a list of property names to listen for.
     * 
     * @return an array of property names, should contain at least one item
     */
    public String[] value();
}
