package com.vaadin.server.data.provider;

import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.data.DataProvider;
import com.vaadin.server.data.ListDataProvider;
import com.vaadin.server.data.Query;

public class ListDataProviderTest {

    private ListDataProvider<StrBean> dataProvider;
    private List<StrBean> data;
    private SerializablePredicate<StrBean> fooFilter = s -> s.getValue()
            .equals("Foo");
    private SerializablePredicate<StrBean> gt5Filter = s -> s
            .getRandomNumber() > 5;

    @Before
    public void setUp() {
        data = StrBean.generateRandomBeans(100);
        dataProvider = DataProvider.create(data);
    }

    @Test
    public void testListContainsAllData() {
        List<StrBean> list = new LinkedList<>(data);
        dataProvider.fetch(new Query<>())
                .forEach(str -> assertTrue(
                        "Data provider contained values not in original data",
                        list.remove(str)));
        assertTrue("Not all values from original data were in data provider",
                list.isEmpty());
    }

    @Test
    public void testSortByComparatorListsDiffer() {
        Comparator<StrBean> comp = Comparator.comparing(StrBean::getValue)
                .thenComparing(StrBean::getRandomNumber)
                .thenComparing(StrBean::getId);
        List<StrBean> list = dataProvider.sortingBy(comp).fetch(new Query<>())
                .collect(Collectors.toList());

        // First value in data is { Xyz, 10, 100 } which should be last in list
        Assert.assertNotEquals("First value should not match", data.get(0),
                list.get(0));

        Assert.assertEquals("Sorted data and original data sizes don't match",
                data.size(), list.size());

        data.sort(comp);
        for (int i = 0; i < data.size(); ++i) {
            Assert.assertEquals("Sorting result differed", data.get(i),
                    list.get(i));
        }
    }

    @Test
    public void testDefatulSortWithSpecifiedPostSort() {
        Comparator<StrBean> comp = Comparator.comparing(StrBean::getValue)
                .thenComparing(Comparator.comparing(StrBean::getId).reversed());
        List<StrBean> list = dataProvider.sortingBy(comp).fetch(new Query<>())
                // The sort here should come e.g from a Component
                .sorted(Comparator.comparing(StrBean::getRandomNumber))
                .collect(Collectors.toList());

        Assert.assertEquals("Sorted data and original data sizes don't match",
                data.size(), list.size());

        for (int i = 1; i < list.size(); ++i) {
            StrBean prev = list.get(i - 1);
            StrBean cur = list.get(i);
            // Test specific sort
            Assert.assertTrue(prev.getRandomNumber() <= cur.getRandomNumber());

            if (prev.getRandomNumber() == cur.getRandomNumber()) {
                // Test default sort
                Assert.assertTrue(
                        prev.getValue().compareTo(cur.getValue()) <= 0);
                if (prev.getValue().equals(cur.getValue())) {
                    Assert.assertTrue(prev.getId() > cur.getId());
                }
            }
        }
    }

    @Test
    public void testDefatulSortWithFunction() {
        List<StrBean> list = dataProvider.sortingBy(StrBean::getValue)
                .fetch(new Query<>()).collect(Collectors.toList());

        Assert.assertEquals("Sorted data and original data sizes don't match",
                data.size(), list.size());

        for (int i = 1; i < list.size(); ++i) {
            StrBean prev = list.get(i - 1);
            StrBean cur = list.get(i);

            // Test default sort
            Assert.assertTrue(prev.getValue().compareTo(cur.getValue()) <= 0);
        }
    }

    @Test
    public void refreshAll_changeBeanInstance() {
        StrBean bean = new StrBean("foo", -1, hashCode());
        int size = dataProvider.size(new Query<>());

        data.set(0, bean);
        dataProvider.refreshAll();

        List<StrBean> list = dataProvider.fetch(new Query<>())
                .collect(Collectors.toList());
        StrBean first = list.get(0);
        Assert.assertEquals(bean.getValue(), first.getValue());
        Assert.assertEquals(bean.getRandomNumber(), first.getRandomNumber());
        Assert.assertEquals(bean.getId(), first.getId());

        Assert.assertEquals(size, dataProvider.size(new Query<>()));
    }

    @Test
    public void refreshAll_updateBean() {
        int size = dataProvider.size(new Query<>());

        StrBean bean = data.get(0);
        bean.setValue("foo");
        dataProvider.refreshAll();

        List<StrBean> list = dataProvider.fetch(new Query<>())
                .collect(Collectors.toList());
        StrBean first = list.get(0);
        Assert.assertEquals("foo", first.getValue());

        Assert.assertEquals(size, dataProvider.size(new Query<>()));
    }

    @Test
    public void refreshAll_sortingBy_changeBeanInstance() {
        StrBean bean = new StrBean("foo", -1, hashCode());
        int size = dataProvider.size(new Query<>());

        data.set(0, bean);

        ListDataProvider<StrBean> dSource = dataProvider
                .sortingBy(Comparator.comparing(StrBean::getId));
        dSource.refreshAll();

        List<StrBean> list = dSource.fetch(new Query<>())
                .collect(Collectors.toList());
        StrBean first = list.get(0);
        Assert.assertEquals(bean.getValue(), first.getValue());
        Assert.assertEquals(bean.getRandomNumber(), first.getRandomNumber());
        Assert.assertEquals(bean.getId(), first.getId());

        Assert.assertEquals(size, dataProvider.size(new Query<>()));
    }

    @Test
    public void refreshAll_addBeanInstance() {
        StrBean bean = new StrBean("foo", -1, hashCode());

        int size = dataProvider.size(new Query<>());

        data.add(0, bean);
        dataProvider.refreshAll();

        List<StrBean> list = dataProvider.fetch(new Query<>())
                .collect(Collectors.toList());
        StrBean first = list.get(0);
        Assert.assertEquals(bean.getValue(), first.getValue());
        Assert.assertEquals(bean.getRandomNumber(), first.getRandomNumber());
        Assert.assertEquals(bean.getId(), first.getId());

        Assert.assertEquals(size + 1, dataProvider.size(new Query<>()));
    }

    @Test
    public void refreshAll_removeBeanInstance() {
        int size = dataProvider.size(new Query<>());

        data.remove(0);
        dataProvider.refreshAll();

        Assert.assertEquals(size - 1, dataProvider.size(new Query<>()));
    }

    @Test
    public void filteringListDataProvider_convertFilter() {
        DataProvider<StrBean, String> strFilterDataProvider = dataProvider
                .convertFilter(
                        text -> strBean -> strBean.getValue().contains(text));
        Assert.assertEquals("Only one item should match 'Xyz'", 1,
                strFilterDataProvider.size(new Query<>("Xyz")));
        Assert.assertEquals("No item should match 'Zyx'", 0,
                strFilterDataProvider.size(new Query<>("Zyx")));
        Assert.assertEquals("Unexpected number of matches for 'Foo'", 36,
                strFilterDataProvider.size(new Query<>("Foo")));

        Assert.assertEquals("No items should've been filtered out", data.size(),
                strFilterDataProvider.size(new Query<>()));
    }

    @Test
    public void filteringListDataProvider_defaultFilterType() {
        Assert.assertEquals("Only one item should match 'Xyz'", 1,
                dataProvider.size(new Query<>(
                        strBean -> strBean.getValue().contains("Xyz"))));
        Assert.assertEquals("No item should match 'Zyx'", 0, dataProvider.size(
                new Query<>(strBean -> strBean.getValue().contains("Zyx"))));
        Assert.assertEquals("Unexpected number of matches for 'Foo'", 36,
                dataProvider.size(new Query<>(fooFilter)));
    }

    @Test
    public void filteringListDataProvider_defaultFilter() {
        // Intentionally lost filter type. Not actually filterable anymore.
        DataProvider<StrBean, ?> filtered = dataProvider.setFilter(gt5Filter);

        Assert.assertEquals("Filter not applied, unexpected item count",
                dataProvider.size(new Query<>(gt5Filter)),
                filtered.size(new Query<>()));

        Assert.assertEquals("Further filtering succeeded",
                filtered.size(new Query<>()),
                filtered.size((Query) new Query<>(fooFilter)));
    }

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

}
