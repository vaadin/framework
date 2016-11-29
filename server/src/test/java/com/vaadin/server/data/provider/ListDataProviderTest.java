package com.vaadin.server.data.provider;

import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.data.DataProvider;
import com.vaadin.server.data.ListDataProvider;
import com.vaadin.server.data.Query;
import com.vaadin.server.data.SortOrder;

public class ListDataProviderTest
        extends DataProviderTestBase<ListDataProvider<StrBean>> {

    @Override
    protected ListDataProvider<StrBean> createDataProvider() {
        return DataProvider.create(data);
    }

    @Test
    public void filteringListDataProvider_appliedFilters() {
        Assert.assertEquals("Filtering result differ",
                data.stream().filter(fooFilter).count(),
                dataProvider.applyFilter(fooFilter).size(new Query<>()));

        Assert.assertEquals("Chained filtering result differ",
                data.stream().filter(fooFilter.and(gt5Filter)).count(),
                dataProvider.applyFilter(fooFilter)
                        .size(new Query<>(gt5Filter)));
    }

    @Test
    public void filteringListDataProvider_chainedFilters() {
        Assert.assertEquals("Chained filtering result differ",
                data.stream().filter(fooFilter.and(gt5Filter)).count(),
                dataProvider.applyFilter(fooFilter).applyFilter(gt5Filter)
                        .size(new Query<>()));
    }

    @Test
    public void filteringListDataProvider_chainedFiltersWithOrInsteadOfAnd() {
        ListDataProvider<StrBean> orFilteredDataProvider = new ListDataProvider<StrBean>(
                data) {

            @Override
            public SerializablePredicate<StrBean> combineFilters(
                    SerializablePredicate<StrBean> filter1,
                    SerializablePredicate<StrBean> filter2) {
                return t -> filter1.test(t) || filter2.test(t);
            }
        };

        Assert.assertEquals("Chained filtering result differ",
                data.stream().filter(fooFilter.or(gt5Filter)).count(),
                orFilteredDataProvider.applyFilter(fooFilter)
                        .applyFilter(gt5Filter).size(new Query<>()));
    }

    @Test
    public void filteringListDataProvider_appliedFilterAndConverter() {
        Assert.assertEquals("Filtering result differ with 'Foo'",
                data.stream().filter(gt5Filter.and(fooFilter)).count(),
                dataProvider.applyFilter(gt5Filter).convertFilter(
                        text -> strBean -> strBean.getValue().equals(text))
                        .size(new Query<>("Foo")));

        Assert.assertEquals("Filtering result differ with 'Xyz'", data.stream()
                .filter(gt5Filter.and(s -> s.getValue().equals("Xyz"))).count(),
                dataProvider.applyFilter(gt5Filter).convertFilter(
                        text -> strBean -> strBean.getValue().equals(text))
                        .size(new Query<>("Xyz")));

        Assert.assertEquals("No results should've been found", 0,
                dataProvider.applyFilter(gt5Filter).convertFilter(
                        text -> strBean -> strBean.getValue().equals(text))
                        .size(new Query<>("Zyx")));
    }

    @Override
    protected ListDataProvider<StrBean> sortingBy(
            List<SortOrder<String>> sortOrder, Comparator<StrBean> comp) {
        return getDataProvider().sortingBy(comp);
    }

}
