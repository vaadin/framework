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

import java.util.Objects;
import java.util.stream.Stream;

import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializableToIntFunction;

/**
 * Data provider that uses one callback for fetching items from a back end and
 * another callback for counting the number of available items.
 *
 * @author Vaadin Ltd
 *
 * @param <T>
 *            data provider data type
 * @param <F>
 *            data provider filter type
 */
public class CallbackDataProvider<T, F>
        extends AbstractBackEndDataProvider<T, F> {
    private final SerializableFunction<Query<T, F>, Stream<T>> fetchCallback;
    private final SerializableToIntFunction<Query<T, F>> sizeCallback;

    /**
     * Constructs a new DataProvider to request data using callbacks for
     * fetching and counting items in the back end.
     *
     * @param fetchCallback
     *            function that returns a stream of items from the back end for
     *            a query
     * @param sizeCallback
     *            function that return the number of items in the back end for a
     *            query
     */
    public CallbackDataProvider(
            SerializableFunction<Query<T, F>, Stream<T>> fetchCallback,
            SerializableToIntFunction<Query<T, F>> sizeCallback) {
        Objects.requireNonNull(fetchCallback, "Request function can't be null");
        Objects.requireNonNull(sizeCallback, "Size callback can't be null");
        this.fetchCallback = fetchCallback;
        this.sizeCallback = sizeCallback;
    }

    @Override
    public Stream<T> fetchFromBackEnd(Query<T, F> query) {
        return fetchCallback.apply(query);
    }

    @Override
    protected int sizeInBackEnd(Query<T, F> query) {
        return sizeCallback.applyAsInt(query);
    }
}
