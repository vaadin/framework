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
package com.vaadin.ui;

import java.util.IdentityHashMap;
import java.util.Map;

import com.vaadin.data.ValueProvider;

/**
 * Value provider class for declarative support.
 * <p>
 * Provides a straightforward mapping between an item and its value.
 *
 * @param <T>
 *            item type
 */
class DeclarativeValueProvider<T> implements ValueProvider<T, String> {

    private final Map<T, String> values = new IdentityHashMap<>();

    @Override
    public String apply(T t) {
        return values.get(t);
    }

    /**
     * Sets a {@code value} for the item {@code t}.
     *
     * @param t
     *            a data item
     * @param value
     *            a value for the item {@code t}
     */
    void addValue(T t, String value) {
        values.put(t, value);
    }

}
