/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.io.Serializable;

/**
 * Factory for providing {@link Converter}s automatically between model and
 * presentation types for {@link com.vaadin.data.Binder.BindingBuilder}. Used in
 * Binder when creating bindings with {@link Binder#bindInstanceFields(Object)}.
 * <p>
 * The framework default implementation is
 * {@link DefaultConverterFactory}.
 *
 * @author Vaadin Ltd.
 * @since
 */
public interface ConverterFactory extends Serializable {

    <PRESENTATION, MODEL> boolean applyConverter(
            Binder.BindingBuilder<MODEL, PRESENTATION> bindingBuilder,
            Class<PRESENTATION> presentationType, Class<MODEL> modelType);

//    <MODEL, PRESENTATION> Optional<Consumer<Binder.BindingBuilder<MODEL,
//            PRESENTATION>>> getConverterApplier(
//          Class<PRESENTATION> presentationType, Class<MODEL> modelType);

    /**
     * Builds converter to the given binding to convert between the presentation
     * and model type.
     * 
     *
     * @param builder
     *            the binding builder
     * @param presentationType
     *            the target field type
     * @param modelType
     *            the bean property type
     * @return the binder builder with converter applied if possible
     */
    // Binder.BindingBuilder buildBindingConverter(Binder.BindingBuilder
    // builder,
    // Class<?> presentationType, Class<?> modelType);

    /**
     * Returns whether this factory has a converter to convert between the
     * presentation type (field component) and the model type (property).
     *
     * @param presentationType
     *            presentation type of the {@link HasValue} field component
     *            (e.g. String for {@link com.vaadin.ui.TextField})
     * @param modelType
     *            class of the property type in the entity
     * @return {@code true} if conversion possible, {@code false} if not
     */
    // boolean isSupported(Class<?> presentationType, Class<?> modelType);

}
