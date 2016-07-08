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

import com.vaadin.tokka.event.Registration;

/**
 * Minimal DataSource API for communication between the DataProvider and a back
 * end service.
 * <p>
 * FIXME: Missing Query class
 * 
 * @since
 * @param <T>
 *            data type
 */
public interface DataSource<T> extends Function<Object, Stream<T>>,
        Serializable {

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