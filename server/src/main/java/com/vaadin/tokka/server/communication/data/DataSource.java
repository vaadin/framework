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
package com.vaadin.tokka.server.communication.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Minimal DataSource API for communication between the DataProvider and a back
 * end service.
 * 
 * @since
 * @param <T>
 *            data type
 * @param <SORT>
 *            sorting information data type
 * 
 * @see InMemoryDataSource
 * @see BackEndDataSource
 */
public interface DataSource<T, SORT> extends Function<Query, Stream<T>>,
        Serializable {

    /**
     * Sets a default sorting order to the data source.
     * 
     * @param sortOrder
     *            an object providing the needed sorting information
     * @return new data source with modified sorting
     */
    DataSource<T, SORT> sortingBy(SORT sortOrder);

    /**
     * Gets whether the DataSource content all available in memory or does it
     * use some external backend.
     * 
     * @return {@code true} if all data is in memory; {@code false} if not
     */
    boolean isInMemory();

    /**
     * This method creates a new {@link InMemoryDataSource} from a given
     * Collection. The InMemoryDataSource creates a protective List copy of all
     * the contents in the Collection.
     * 
     * @param data
     *            collection of data
     * @return in-memory data source
     */
    public static <T> InMemoryDataSource<T> create(Collection<T> data) {
        return new InMemoryDataSource<>(data);
    }

    /**
     * This method creates a new {@link InMemoryDataSource} from given
     * objects.The InMemoryDataSource creates a protective List copy of all the
     * contents in the array.
     * 
     * @param data
     *            data objects
     * @return in-memory data source
     */
    @SafeVarargs
    public static <T> InMemoryDataSource<T> create(T... data) {
        return new InMemoryDataSource<>(Arrays.asList(data));
    }
}