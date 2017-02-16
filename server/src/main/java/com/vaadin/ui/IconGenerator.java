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

import com.vaadin.server.Resource;
import com.vaadin.server.SerializableFunction;

/**
 * A callback interface for generating icons for an item.
 *
 * @param <T>
 *            item type for which the icon is generated
 * @since 8.0
 */
@FunctionalInterface
public interface IconGenerator<T> extends SerializableFunction<T, Resource> {

    /**
     * Gets an icon resource for the {@code item}.
     *
     * @param item
     *            the item for which to generate an icon for
     * @return the generated icon resource
     */
    @Override
    public Resource apply(T item);
}