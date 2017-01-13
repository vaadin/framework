package com.vaadin.data.provider;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.data.sort.SortDirection;

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
                dataProvider.withFilter(fooFilter).size(new Query<>()));

        Assert.assertEquals("Chained filtering result differ",
                data.stream().filter(fooFilter.and(gt5Filter)).count(),
                dataProvider.withFilter(fooFilter)
                        .size(new Query<>(gt5Filter)));
    }

    @Test
    public void filteringListDataProvider_chainedFilters() {
        Assert.assertEquals("Chained filtering result differ",
                data.stream().filter(fooFilter.and(gt5Filter)).count(),
                dataProvider.withFilter(fooFilter).withFilter(gt5Filter)
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
                orFilteredDataProvider.withFilter(fooFilter)
                        .withFilter(gt5Filter).size(new Query<>()));
    }

    @Test
    public void filteringListDataProvider_appliedFilterAndConverter() {
        Assert.assertEquals("Filtering result differ with 'Foo'",
                data.stream().filter(gt5Filter.and(fooFilter)).count(),
                dataProvider.withFilter(gt5Filter).convertFilter(
                        text -> strBean -> strBean.getValue().equals(text))
                        .size(new Query<>("Foo")));

        Assert.assertEquals("Filtering result differ with 'Xyz'", data.stream()
                .filter(gt5Filter.and(s -> s.getValue().equals("Xyz"))).count(),
                dataProvider.withFilter(gt5Filter).convertFilter(
                        text -> strBean -> strBean.getValue().equals(text))
                        .size(new Query<>("Xyz")));

        Assert.assertEquals("No results should've been found", 0,
                dataProvider.withFilter(gt5Filter).convertFilter(
                        text -> strBean -> strBean.getValue().equals(text))
                        .size(new Query<>("Zyx")));
    }

    @Test
    public void setSortByProperty_ascending() {
        ListDataProvider<StrBean> dataProvider = getDataProvider();

        dataProvider.setSortOrder(StrBean::getId, SortDirection.ASCENDING);

        int[] threeFirstIds = dataProvider.fetch(new Query<>())
                .mapToInt(StrBean::getId).limit(3).toArray();

        Assert.assertArrayEquals(new int[] { 0, 1, 2 }, threeFirstIds);
    }

    @Test
    public void setSortByProperty_descending() {
        ListDataProvider<StrBean> dataProvider = getDataProvider();

        dataProvider.setSortOrder(StrBean::getId, SortDirection.DESCENDING);

        int[] threeFirstIds = dataProvider.fetch(new Query<>())
                .mapToInt(StrBean::getId).limit(3).toArray();

        Assert.assertArrayEquals(new int[] { 98, 97, 96 }, threeFirstIds);
    }

    @Test
    public void testMultipleSortOrder_firstAddedWins() {
        ListDataProvider<StrBean> dataProvider = getDataProvider();

        dataProvider.addSortOrder(StrBean::getValue, SortDirection.DESCENDING);
        dataProvider.addSortOrder(StrBean::getId, SortDirection.DESCENDING);

        List<StrBean> threeFirstItems = dataProvider.fetch(new Query<>())
                .limit(3).collect(Collectors.toList());

        // First one is Xyz
        Assert.assertEquals(new StrBean("Xyz", 10, 100),
                threeFirstItems.get(0));
        // The following are Foos ordered by id
        Assert.assertEquals(new StrBean("Foo", 93, 2), threeFirstItems.get(1));
        Assert.assertEquals(new StrBean("Foo", 91, 2), threeFirstItems.get(2));
    }

    @Override
    protected void setSortOrder(List<SortOrder<String>> sortOrder,
            Comparator<StrBean> comp) {
        getDataProvider().setSortOrder(comp);
    }

}
