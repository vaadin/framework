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

    private Comparator<StrBean> getComparator(SortOrder<String> so) {
        Comparator<StrBean> comparator = propertyToComparatorMap
                .get(so.getSorted());
        if (so.getDirection() == SortDirection.DESCENDING) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    @Override
    protected BackEndDataProvider<StrBean, SerializablePredicate<StrBean>> createDataProvider() {
        return dataProvider = new CallbackDataProvider<>(query -> {
            Stream<StrBean> stream = data.stream()
                    .filter(t -> query.getFilter().orElse(s -> true).test(t));
            if (!query.getSortOrders().isEmpty()) {
                Comparator<StrBean> sorting = query.getSortOrders().stream()
                        .map(this::getComparator)
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

    @Override
    protected void setSortOrder(List<SortOrder<String>> sortOrder,
            Comparator<StrBean> comp) {
        getDataProvider().setSortOrders(sortOrder);
    }
}
