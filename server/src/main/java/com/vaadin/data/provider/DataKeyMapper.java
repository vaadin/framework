/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.data.provider;

import java.io.Serializable;

import com.vaadin.data.ValueProvider;

/**
 * DataKeyMapper to map data objects to key strings.
 *
 * @since 8.0
 * @param <T>
 *            data type
 */
public interface DataKeyMapper<T> extends Serializable {

    /**
     * Gets the key for data object. If no key exists beforehand, a new key is
     * created.
     *
     * @param dataObject
     *            data object for key mapping
     * @return key for given data object
     */
    String key(T dataObject);

    /**
     * Check whether this key mapper contains the given data object.
     *
     * @param dataObject
     *            the data object to check
     * @return {@code true} if the given data object is contained in this key
     *         mapper, {@code false} otherwise
     */
    boolean has(T dataObject);

    /**
     * Gets the data object identified by given key.
     *
     * @param key
     *            key of a data object
     * @return identified data object; <code>null</code> if invalid key
     */
    T get(String key);

    /**
     * Removes a data object from the key mapping. The key is also dropped.
     * Dropped keys are not reused.
     *
     * @param dataObject
     *            dropped data object
     */
    void remove(T dataObject);

    /**
     * Removes all data objects from the key mapping. The keys are also dropped.
     * Dropped keys are not reused.
     */
    void removeAll();

    /**
     * Updates any existing mappings of given data object. The equality of two
     * data objects is determined by the equality of their identifiers provided
     * by the given value provider.
     *
     * @param dataObject
     *            the data object to update
     *
     * @since 8.1
     *
     */
    void refresh(T dataObject);

    /**
     * Takes identifier getter into use and updates existing mappings.
     *
     * @param identifierGetter
     *            has to return a unique key for every bean, and the returned
     *            key has to follow general {@code hashCode()} and
     *            {@code equals()} contract, see {@link Object#hashCode()} for
     *            details.
     * @since 8.1
     */
    void setIdentifierGetter(ValueProvider<T, Object> identifierGetter);
}
