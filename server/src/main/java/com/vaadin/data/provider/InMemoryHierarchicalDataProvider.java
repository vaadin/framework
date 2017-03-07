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

import com.vaadin.data.HierarchyData;
import com.vaadin.server.SerializablePredicate;

/**
 * 
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 */
public class InMemoryHierarchicalDataProvider<T>
        extends AbstractHierarchicalDataProvider<T, SerializablePredicate<T>> {

    private final HierarchyData<T> hierarchyData;

    public InMemoryHierarchicalDataProvider(HierarchyData<T> hierarchyData) {
        this.hierarchyData = hierarchyData;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public boolean hasChildren(T item) {
        return !hierarchyData.getChildren(item).isEmpty();
    }

    @Override
    public int getChildCount(
            HierarchicalQuery<T, SerializablePredicate<T>> query) {
        return hierarchyData.getChildren(query.getParent()).size();
    }

    @Override
    public Stream<T> fetchChildren(
            HierarchicalQuery<T, SerializablePredicate<T>> query) {
        Stream<T> childStream = hierarchyData.getChildren(query.getParent())
                .stream();
        return query.getFilter().map(childStream::filter).orElse(childStream)
                .skip(query.getOffset()).limit(query.getLimit());
    }
}