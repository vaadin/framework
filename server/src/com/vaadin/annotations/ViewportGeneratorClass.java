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
package com.vaadin.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.server.ViewportGenerator;

/**
 * Defines a viewport tag generator class that will be used for generating the
 * content of a viewport tag that will be added to the HTML of the host page of
 * a UI class.
 * <p>
 * If you want to use the same viewport values for all requests, you can use the
 * simpler {@link Viewport} annotation instead.
 * 
 * @see ViewportGenerator
 * 
 * @since 7.4
 * 
 * @author Vaadin Ltd
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ViewportGeneratorClass {
    /**
     * Gets the viewport generator class to use. Please note that the class must
     * be public and have a default constructor. It must additionally be
     * declared as static if it's declared as an inner class.
     * 
     * @return the viewport generator class
     */
    public Class<? extends ViewportGenerator> value();
}
