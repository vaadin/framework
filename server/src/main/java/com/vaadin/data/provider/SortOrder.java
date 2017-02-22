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
package com.vaadin.data.provider;

import java.io.Serializable;

import com.vaadin.shared.data.sort.SortDirection;

/**
 * Sorting information for one field.
 *
 * @param <T>
 *            the type of the sorting information, usually a String (field id)
 *            or a {@link java.util.Comparator}.
 * @since 8.0
 */
public class SortOrder<T> implements Serializable {

    private final T sorted;
    private final SortDirection direction;

    /**
     * Constructs a field sorting information
     *
     * @param sorted
     *            sorting information, usually field id or
     *            {@link java.util.Comparator}
     * @param direction
     *            sorting direction
     */
    public SortOrder(T sorted, SortDirection direction) {
        this.sorted = sorted;
        this.direction = direction;
    }

    /**
     * Sorting information
     *
     * @return sorting entity, usually field id or {@link java.util.Comparator}
     */
    public T getSorted() {
        return sorted;
    }

    /**
     * Sorting direction
     *
     * @return sorting direction
     */
    public SortDirection getDirection() {
        return direction;
    }
}
