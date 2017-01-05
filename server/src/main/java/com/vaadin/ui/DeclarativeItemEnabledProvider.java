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

import java.util.HashSet;
import java.util.Set;

import com.vaadin.server.SerializablePredicate;

/**
 * Item enabled provider class for declarative support.
 * <p>
 * Provides a straightforward mapping between an item and its enable state.
 *
 * @param <T>
 *            item type
 */
class DeclarativeItemEnabledProvider<T> implements SerializablePredicate<T> {

    private Set<T> disabled = new HashSet<>();

    @Override
    public boolean test(T item) {
        return !disabled.contains(item);
    }

    /**
     * Adds the {@code item} to disabled items list.
     *
     * @param item
     *            a data item
     */
    protected void addDisabled(T item) {
        disabled.add(item);
    }

}
