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
package com.vaadin.server;

import java.io.Serializable;
import java.util.function.BiPredicate;

/**
 * A {@link BiPredicate} that is also {@link Serializable}.
 *
 * @author Vaadin Ltd
 * @since 8.0
 * @param <T>
 *            the type of the first input to the predicate
 * @param <U>
 *            the type of the second input to the predicate
 */
public interface SerializableBiPredicate<T, U>
        extends BiPredicate<T, U>, Serializable {
    // Only method inherited from BiPredicate
}
