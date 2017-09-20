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
package com.vaadin.server;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link BiFunction} that is also {@link Serializable}.
 *
 * @see Function
 * @author Vaadin Ltd
 * @since 8.0
 * @param <T>
 *            the type of the the first function parameter
 * @param <U>
 *            the type of the the second function parameter
 * @param <R>
 *            the type of the result of the function
 */
@FunctionalInterface
public interface SerializableBiFunction<T, U, R>
        extends BiFunction<T, U, R>, Serializable {
    // Only method inherited from BiFunction
}
