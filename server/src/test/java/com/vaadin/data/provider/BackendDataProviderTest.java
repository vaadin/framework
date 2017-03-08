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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.data.sort.SortDirection;

public class BackendDataProviderTest extends
        DataProviderTestBase<BackEndDataProvider<StrBean, SerializablePredicate<StrBean>>> {

    private static Map<String, Comparator<StrBean>> propertyToComparatorMap = new HashMap<>();

    static {
        propertyToComparatorMap.put("value",
                Comparator.comparing(StrBean::getValue));
        propertyToComparatorMap.put("id", Comparator.comparing(StrBean::getId));
        propertyToComparatorMap.put("randomNumber",
                Comparator.comparing(StrBean::getRandomNumber));
    }

    private static Comparator<StrBean> getComparator(SortOrder<String> so) {
        Comparator<StrBean> comparator = propertyToComparatorMap
                .get(so.getSorted());
        if (so.getDirection() == SortDirection.DESCENDING) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    public static class StrBeanBackEndDataProvider extends
            CallbackDataProvider<StrBean, SerializablePredicate<StrBean>> {

        public StrBeanBackEndDataProvider(List<StrBean> data) {
            super(query -> {
                Stream<StrBean> stream = data.stream().filter(
                        t -> query.getFilter().orElse(s -> true).test(t));
                if (!query.getSortOrders().isEmpty()) {
                    Comparator<StrBean> sorting = query.getSortOrders().stream()
                            .map(BackendDataProviderTest::getComparator)
                            .reduce((c1, c2) -> c1.thenComparing(c2)).get();
                    stream = stream.sorted(sorting);
                }
                List<StrBean> list = stream.skip(query.getOffset())
                        .limit(query.getLimit()).collect(Collectors.toList());
                list.forEach(s -> System.err.println(s.toString()));
                return list.stream();
            }, query -> (int) data.stream()
                    .filter(t -> query.getFilter().orElse(s -> true).test(t))
                    .count());
        }
    }

    @Override
    protected BackEndDataProvider<StrBean, SerializablePredicate<StrBean>> createDataProvider() {
        return dataProvider = new StrBeanBackEndDataProvider(data);
    }

    @Override
    protected void setSortOrder(List<QuerySortOrder> sortOrder,
            Comparator<StrBean> comp) {
        getDataProvider().setSortOrders(sortOrder);
    }

}
