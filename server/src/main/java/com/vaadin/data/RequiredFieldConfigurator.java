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
package com.vaadin.data;

import java.lang.annotation.Annotation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.vaadin.server.SerializablePredicate;

/**
 * This interface represents a predicate which returns {@code true} if bound
 * field should be configured to have required indicator via
 * {@link HasValue#setRequiredIndicatorVisible(boolean)}.
 * 
 * @see BeanValidationBinder
 * @see BeanValidationBinder#setRequiredConfigurator(RequiredFieldConfigurator)
 * 
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public interface RequiredFieldConfigurator
        extends SerializablePredicate<Annotation> {

    /**
     * Configurator which is aware of {@literal @NotNull} annotation presence
     * for a property.
     */
    public RequiredFieldConfigurator NOT_NULL = annotation -> annotation
            .annotationType().equals(NotNull.class);

    /**
     * Configurator which is aware of {@literal @NotEmpty} annotation presence
     * for a property.
     */
    public RequiredFieldConfigurator NOT_EMPTY = annotation -> annotation
            .annotationType().getName()
            .equals("org.hibernate.validator.constraints.NotEmpty");

    /**
     * Configurator which is aware of {@literal Size} annotation with
     * {@code min()> 0} presence for a property.
     */
    public RequiredFieldConfigurator SIZE = annotation -> annotation
            .annotationType().equals(Size.class)
            && ((Size) annotation).min() > 0;

    /**
     * Default configurator which is combination of {@link #NOT_NULL},
     * {@link #NOT_EMPTY} and {@link #SIZE} configurators.
     */
    public RequiredFieldConfigurator DEFAULT = NOT_NULL.chain(NOT_EMPTY)
            .chain(SIZE);

    public default RequiredFieldConfigurator chain(
            RequiredFieldConfigurator configurator) {
        return descriptor -> test(descriptor) || configurator.test(descriptor);
    }
}
