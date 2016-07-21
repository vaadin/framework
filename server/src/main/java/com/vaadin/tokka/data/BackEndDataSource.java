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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A {@link DataSource} for any back end.
 * 
 * @param <T>
 *            data source data type
 */
public class BackEndDataSource<T> implements
        DataSource<T, List<SortOrder<String>>> {

    private Function<Query, Stream<T>> request;

    public BackEndDataSource(Function<Query, Stream<T>> request) {
        this.request = request;
    }

    @Override
    public Stream<T> apply(Query t) {
        return request.apply(t);
    }

    @Override
    public BackEndDataSource<T> sortingBy(List<SortOrder<String>> sortOrder) {
        return new BackEndDataSource<T>(query -> {
            List<SortOrder<String>> queryOrder = new ArrayList<>(
                    query.getSortOrders());
            queryOrder.addAll(sortOrder);
            return request.apply(new Query(query.getLimit(), query.getOffset(),
                    queryOrder, query.getFilters()));
        });
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

}
