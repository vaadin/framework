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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.ui.declarative.Design;

/**
 * Marks the component as the root of a design (html) file.
 * <p>
 * Used together with {@link Design#read(com.vaadin.ui.Component)} to be able
 * the load the design without further configuration. The design is loaded from
 * the same package as the annotated class and by default the design filename is
 * derived from the class name. Using the {@link #value()} parameter you can
 * specify another design file name.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DesignRoot {
    String value() default "";
}
