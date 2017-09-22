/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.osgi.liferay;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.portlet.Portlet;

/**
 * This annotation is used to inform the
 * {@link PortletUIServiceTrackerCustomizer} that this UI should be wrapped in a
 * {@link Portlet} and provides the necessary configuration for that.
 * <p>
 * This only applies to Liferay Portal 7+ with OSGi support.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface VaadinLiferayPortletConfiguration {
    /**
     * Category of the portlet in Liferay menus. By default
     * <i>category.vaadin</i>.
     */
    String category() default "category.vaadin";

    /**
     * Portlet name, must conform to the portlet specification and is used as
     * the key for the portlet.
     */
    String name() default "";

    /**
     * Display name of the portlet.
     */
    String displayName() default "";

    /**
     * Array of allowed security roles. By default, <i>power-user</i> and
     * <i>user</i>.
     */
    String[] securityRole() default { "power-user", "user" };
}
