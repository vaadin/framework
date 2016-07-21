/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tokka.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * {@link DataSource} wrapper for {@link Collection}s. This class does not
 * actually handle the {@link Query} parameters.
 *
 * @param <T>
 *            data type
 */
public class InMemoryDataSource<T> implements DataSource<T, Comparator<T>> {

    private Function<Query, Stream<T>> request;

    /**
     * Constructs a new ListDataSource. This method makes a protective copy of
     * the contents of the Collection.
     * 
     * @param collection
     *            initial data
     */
    public InMemoryDataSource(Collection<T> collection) {
        final List<T> backend = new ArrayList<T>(collection);
        request = query -> backend.stream();
    }

    /**
     * Chaining constructor for making modified {@link InMemoryDataSource}s.
     * This Constructor is used internally for making sorted and filtered
     * variants of a base data source with actual data.
     * 
     * @param request
     *            request for the new data source
     */
    protected InMemoryDataSource(Function<Query, Stream<T>> request) {
        this.request = request;
    }

    @Override
    public Stream<T> apply(Query query) {
        return request.apply(query);
    }

    @Override
    public InMemoryDataSource<T> sortingBy(Comparator<T> sortOrder) {
        return new InMemoryDataSource<T>(q -> request.apply(q)
                .sorted(sortOrder));
    }

    public <U extends Comparable<? super U>> InMemoryDataSource<T> sortingBy(
            Function<T, U> sortOrder) {
        return sortingBy(Comparator.comparing(sortOrder));
    }

    @Override
    public boolean isInMemory() {
        return true;
    }
}
