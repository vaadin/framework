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

import com.vaadin.server.SerializableFunction;

/**
 * A callback interface for providing values from a given source.
 * <p>
 * For example this interface can be implemented to simply extract a value with
 * a getter, or to create a composite value based on the fields of the source
 * object.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 *
 * @param <SOURCE>
 *            the type of the object used to provide the value
 * @param <TARGET>
 *            the type of the provided value
 */
@FunctionalInterface
public interface ValueProvider<SOURCE, TARGET>
        extends SerializableFunction<SOURCE, TARGET> {

    /**
     * Returns a value provider that always returns its input argument.
     *
     * @param <T>
     *            the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    public static <T> ValueProvider<T, T> identity() {
        return t -> t;
    }

    /**
     * Provides a value from the given source object.
     *
     * @param source
     *            the source to retrieve the value from
     * @return the value provided by the source
     */
    @Override
    public TARGET apply(SOURCE source);
}
