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
package com.vaadin.server.data;

import java.util.stream.Stream;

import com.vaadin.server.SerializableFunction;
import com.vaadin.shared.Registration;

/**
 * Wrapper class for modifying filters in a query. Used to create a suitable
 * {@link Query} for the underlying data provider.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            data provider data type
 * @param <F>
 *            wrapper query filter type
 * @param <M>
 *            underlying data provider filter type
 */
public class FilteringDataProviderWrapper<T, F, M>
        implements DataProvider<T, F> {

    private DataProvider<T, M> dataProvider;
    private SerializableFunction<F, M> mapper;

    public FilteringDataProviderWrapper(DataProvider<T, M> dataProvider,
            SerializableFunction<F, M> mapper) {
        this.dataProvider = dataProvider;
        this.mapper = mapper;
    }

    @Override
    public boolean isInMemory() {
        return dataProvider.isInMemory();
    }

    @Override
    public void refreshAll() {
        dataProvider.refreshAll();
    }

    @Override
    public Registration addDataProviderListener(DataProviderListener listener) {
        return dataProvider.addDataProviderListener(listener);
    }

    @Override
    public int size(Query<F> t) {
        return dataProvider.size(new Query<M>(t.getOffset(), t.getLimit(),
                t.getSortOrders(), t.getFilter().map(mapper).orElse(null)));
    }

    @Override
    public Stream<T> fetch(Query<F> t) {
        return dataProvider.fetch(new Query<M>(t.getOffset(), t.getLimit(),
                t.getSortOrders(), t.getFilter().map(mapper).orElse(null)));
    }

}
