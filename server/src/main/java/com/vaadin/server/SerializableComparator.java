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
import java.util.Comparator;

/**
 * A {@link Comparator} that is also {@link Serializable}.
 * <p>
 * You can create a serializable comparator from a regular comparator through a
 * method reference by appending <code>::compare</code>. For example
 * <code>SerializableComparator&lt;Employee&gt;
 * comparator = Comparator.comparing(Employee::getFirstName)::compare</code>.
 * The resulting comparator will in most cases cause exceptions if it is
 * actually being serialized, but this construct will enable using the
 * shorthands in {@link Comparator} in applications where session will not be
 * serialized.
 *
 * @author Vaadin Ltd
 * @param <T>
 *            the type of objects that may be compared by this comparator
 * @since 8.0
 *
 */
@FunctionalInterface
public interface SerializableComparator<T> extends Comparator<T>, Serializable {
    // Relevant methods inherited from Comparator
}
