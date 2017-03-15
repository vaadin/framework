package com.vaadin.data.provider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.HierarchyData;
import com.vaadin.server.SerializablePredicate;

public class InMemoryHierarchicalDataProviderTest extends
        DataProviderTestBase<InMemoryHierarchicalDataProvider<StrBean>> {

    private HierarchyData<StrBean> data;
    private List<StrBean> flattenedData;
    private List<StrBean> rootData;

    @Override
    public void setUp() {
        List<StrBean> randomBeans = StrBean.generateRandomBeans(20);
        flattenedData = new ArrayList<>();
        rootData = new ArrayList<>();

        data = new HierarchyData<>();
        data.addItems(null, randomBeans.subList(0, 5));
        data.addItems(randomBeans.get(0), randomBeans.subList(5, 10));
        data.addItems(randomBeans.get(5), randomBeans.subList(10, 15));
        data.addItems(null, randomBeans.subList(15, 20));

        flattenedData.add(randomBeans.get(0));
        flattenedData.add(randomBeans.get(5));
        flattenedData.addAll(randomBeans.subList(10, 15));
        flattenedData.addAll(randomBeans.subList(6, 10));
        flattenedData.addAll(randomBeans.subList(1, 5));
        flattenedData.addAll(randomBeans.subList(15, 20));

        rootData.addAll(randomBeans.subList(0, 5));
        rootData.addAll(randomBeans.subList(15, 20));

        super.setUp();
    }

    @Test(expected = IllegalArgumentException.class)
    public void hierarchyData_add_item_parent_not_in_hierarchy_throws() {
        new HierarchyData<>().addItem(new StrBean("", 0, 0),
                new StrBean("", 0, 0));
    }

    @Test(expected = NullPointerException.class)
    public void hierarchyData_add_null_item_throws() {
        new HierarchyData<>().addItem(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hierarchyData_add_item_already_in_hierarchy_throws() {
        StrBean bean = new StrBean("", 0, 0);
        new HierarchyData<>().addItem(null, bean).addItem(null, bean);
    }

    @Test
    public void hierarchyData_remove_root_item() {
        data.removeItem(null);
        Assert.assertTrue(data.getChildren(null).isEmpty());
    }

    @Test
    public void hierarchyData_clear() {
        data.clear();
        Assert.assertTrue(data.getChildren(null).isEmpty());
    }

    @Test
    public void setFilter() {
        getDataProvider().setFilter(item -> item.getValue().equals("Xyz")
                || item.getValue().equals("Baz"));

        Assert.assertEquals(10, sizeWithUnfilteredQuery());

        getDataProvider().setFilter(item -> !item.getValue().equals("Foo")
                && !item.getValue().equals("Xyz"));

        Assert.assertEquals(
                "Previous filter should be replaced when setting a new one", 6,
                sizeWithUnfilteredQuery());

        getDataProvider().setFilter(null);

        Assert.assertEquals("Setting filter to null should remove all filters",
                20, sizeWithUnfilteredQuery());
    }

    @Test
    public void addFilter() {
        getDataProvider().addFilter(item -> item.getId() <= 10);
        getDataProvider().addFilter(item -> item.getId() >= 5);
        Assert.assertEquals(5, sizeWithUnfilteredQuery());
    }

    @Override
    public void filteringListDataProvider_convertFilter() {
        DataProvider<StrBean, String> strFilterDataProvider = getDataProvider()
                .withConvertedFilter(
                        text -> strBean -> strBean.getValue().contains(text));
        Assert.assertEquals("Only one item should match 'Xyz'", 1,
                strFilterDataProvider
                        .size(new HierarchicalQuery<>("Xyz", null)));
        Assert.assertEquals("No item should match 'Zyx'", 0,
                strFilterDataProvider
                        .size(new HierarchicalQuery<>("Zyx", null)));
        Assert.assertEquals("Unexpected number of matches for 'Foo'", 3,
                strFilterDataProvider
                        .size(new HierarchicalQuery<>("Foo", null)));
        Assert.assertEquals("No items should've been filtered out",
                rootData.size(), strFilterDataProvider
                        .size(new HierarchicalQuery<>(null, null)));
    }

    @Override
    public void filteringListDataProvider_defaultFilterType() {
        Assert.assertEquals("Only one item should match 'Xyz'", 1,
                getDataProvider().size(new HierarchicalQuery<>(
                        strBean -> strBean.getValue().contains("Xyz"), null)));
        Assert.assertEquals("No item should match 'Zyx'", 0,
                dataProvider.size(new HierarchicalQuery<>(
                        strBean -> strBean.getValue().contains("Zyx"), null)));
        Assert.assertEquals("Unexpected number of matches for 'Foo'", 3,
                getDataProvider()
                        .size(new HierarchicalQuery<>(fooFilter, null)));
    }

    @Override
    public void testDefaultSortWithSpecifiedPostSort() {
        Comparator<StrBean> comp = Comparator.comparing(StrBean::getValue)
                .thenComparing(Comparator.comparing(StrBean::getId).reversed());
        setSortOrder(QuerySortOrder.asc("value").thenDesc("id").build(), comp);

        List<StrBean> list = getDataProvider()
                .fetch(createQuery(QuerySortOrder.asc("randomNumber").build(),
                        Comparator.comparing(StrBean::getRandomNumber), null,
                        null))
                .collect(Collectors.toList());

        Assert.assertEquals("Sorted data and original data sizes don't match",
                getDataProvider().fetch(new HierarchicalQuery<>(null, null))
                        .count(),
                list.size());

        for (int i = 1; i < list.size(); ++i) {
            StrBean prev = list.get(i - 1);
            StrBean cur = list.get(i);
            // Test specific sort
            Assert.assertTrue(
                    "Failure: " + prev.getRandomNumber() + " > "
                            + cur.getRandomNumber(),
                    prev.getRandomNumber() <= cur.getRandomNumber());

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

    @Override
    public void testDefaultSortWithFunction() {
        setSortOrder(QuerySortOrder.asc("value").build(),
                Comparator.comparing(StrBean::getValue));

        List<StrBean> list = getDataProvider()
                .fetch(new HierarchicalQuery<>(null, null))
                .collect(Collectors.toList());

        Assert.assertEquals("Sorted data and original data sizes don't match",
                rootData.size(), list.size());

        for (int i = 1; i < list.size(); ++i) {
            StrBean prev = list.get(i - 1);
            StrBean cur = list.get(i);

            // Test default sort
            Assert.assertTrue(prev.getValue().compareTo(cur.getValue()) <= 0);
        }
    }

    @Override
    public void testListContainsAllData() {
        assertHierarchyCorrect();
    }

    @Override
    public void testSortByComparatorListsDiffer() {
        Comparator<StrBean> comp = Comparator.comparing(StrBean::getValue)
                .thenComparing(StrBean::getRandomNumber)
                .thenComparing(StrBean::getId);

        List<StrBean> list = getDataProvider().fetch(
                createQuery(QuerySortOrder.asc("value").thenAsc("randomNumber")
                        .thenAsc("id").build(), comp, null, null))
                .collect(Collectors.toList());

        Assert.assertNotEquals("First value should not match", rootData.get(0),
                list.get(0));

        Assert.assertEquals("Sorted data and original data sizes don't match",
                rootData.size(), list.size());

        rootData.sort(comp);
        for (int i = 0; i < rootData.size(); ++i) {
            Assert.assertEquals("Sorting result differed", rootData.get(i),
                    list.get(i));
        }
    }

    @Override
    protected InMemoryHierarchicalDataProvider<StrBean> createDataProvider() {
        return new InMemoryHierarchicalDataProvider<>(data);
    }

    @Override
    protected void setSortOrder(List<QuerySortOrder> sortOrder,
            Comparator<StrBean> comp) {
        getDataProvider().setSortComparator(comp::compare);
    }

    @Override
    protected long sizeWithUnfilteredQuery() {
        return getFlattenedDataFromProvider(new ArrayList<>(), null).size();
    }

    private void assertHierarchyCorrect() {
        Assert.assertEquals(flattenedData,
                getFlattenedData(new ArrayList<>(), null));
        Assert.assertEquals(flattenedData,
                getFlattenedDataFromProvider(new ArrayList<>(), null));
    }

    private List<StrBean> getFlattenedData(List<StrBean> flattened,
            StrBean item) {
        if (item != null) {
            flattened.add(item);
        }
        data.getChildren(item)
                .forEach(child -> getFlattenedData(flattened, child));
        return flattened;
    }

    private List<StrBean> getFlattenedDataFromProvider(List<StrBean> flattened,
            StrBean item) {
        if (item != null) {
            flattened.add(item);
        }
        getDataProvider().fetchChildren(new HierarchicalQuery<>(null, item))
                .forEach(child -> getFlattenedDataFromProvider(flattened,
                        child));
        return flattened;
    }

    private HierarchicalQuery<StrBean, SerializablePredicate<StrBean>> createQuery(
            List<QuerySortOrder> sortOrder, Comparator<StrBean> comp,
            SerializablePredicate<StrBean> filter, StrBean parent) {
        return new HierarchicalQuery<>(0, Integer.MAX_VALUE, sortOrder, comp,
                filter, parent);
    }
}
