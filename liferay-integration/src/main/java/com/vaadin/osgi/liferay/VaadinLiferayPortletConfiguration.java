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
 *
 * @author Vaadin Ltd.
 *
 * @since 8.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface VaadinLiferayPortletConfiguration {
    String category() default "category.vaadin";

    String name() default "";

    String displayName() default "";

    String[] securityRole() default { "power-user", "user" };
}
