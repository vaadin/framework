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

import java.util.stream.Stream;

/**
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 * @param <F>
 */
public interface HierarchicalDataProvider<T, F> extends DataProvider<T, F> {

    @Override
    public default int size(Query<T, F> query) {
        if (query instanceof HierarchicalQuery<?, ?>) {
            return getChildCount((HierarchicalQuery<T, F>) query);
        }
        throw new IllegalArgumentException(
                "Hierarchical data provider doesn't support non-hierarchical queries");
    }

    @Override
    public default Stream<T> fetch(Query<T, F> query) {
        if (query instanceof HierarchicalQuery<?, ?>) {
            return fetchChildren((HierarchicalQuery<T, F>) query);
        }
        throw new IllegalArgumentException(
                "Hierarchical data provider doesn't support non-hierarchical queries");
    }

    public int getChildCount(HierarchicalQuery<T, F> query);

    public Stream<T> fetchChildren(HierarchicalQuery<T, F> query);

    public boolean hasChildren(T item);

}
