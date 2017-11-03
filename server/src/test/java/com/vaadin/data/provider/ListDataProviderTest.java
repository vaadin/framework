package com.vaadin.data.provider;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.SerializableComparator;
import com.vaadin.shared.data.sort.SortDirection;

public class ListDataProviderTest
        extends DataProviderTestBase<ListDataProvider<StrBean>> {

    @Override
    protected ListDataProvider<StrBean> createDataProvider() {
        return DataProvider.ofCollection(data);
    }

    @Test
    public void dataProvider_ofItems_shouldCreateAnEditableDataProvider() {
        ListDataProvider<String> dataProvider = DataProvider.ofItems("0", "1");

        Assert.assertTrue(
                "DataProvider.ofItems should create a list data provider backed an ArrayList allowing edits",
                dataProvider.getItems() instanceof ArrayList);

        List<String> list = (List<String>) dataProvider.getItems();
        // previously the following would explode since Arrays.ArrayList does
        // not support it
        list.add(0, "2");
    }

    @Test
    public void setSortByProperty_ascending() {
        ListDataProvider<StrBean> dataProvider = getDataProvider();

        dataProvider.setSortOrder(StrBean::getId, SortDirection.ASCENDING);

        int[] threeFirstIds = dataProvider.fetch(new Query<>())
                .mapToInt(StrBean::getId).limit(3).toArray();

        assertArrayEquals(new int[] { 0, 1, 2 }, threeFirstIds);
    }

    @Test
    public void setSortByProperty_descending() {
        ListDataProvider<StrBean> dataProvider = getDataProvider();

        dataProvider.setSortOrder(StrBean::getId, SortDirection.DESCENDING);

        int[] threeFirstIds = dataProvider.fetch(new Query<>())
                .mapToInt(StrBean::getId).limit(3).toArray();

        assertArrayEquals(new int[] { 98, 97, 96 }, threeFirstIds);
    }

    @Test
    public void testMultipleSortOrder_firstAddedWins() {
        ListDataProvider<StrBean> dataProvider = getDataProvider();

        dataProvider.addSortOrder(StrBean::getValue, SortDirection.DESCENDING);
        dataProvider.addSortOrder(StrBean::getId, SortDirection.DESCENDING);

        List<StrBean> threeFirstItems = dataProvider.fetch(new Query<>())
                .limit(3).collect(Collectors.toList());

        // First one is Xyz
        assertEquals(new StrBean("Xyz", 10, 100), threeFirstItems.get(0));
        // The following are Foos ordered by id
        assertEquals(new StrBean("Foo", 93, 2), threeFirstItems.get(1));
        assertEquals(new StrBean("Foo", 91, 2), threeFirstItems.get(2));
    }

    @Test
    public void setFilter() {
        dataProvider.setFilter(item -> item.getValue().equals("Foo"));

        assertEquals(36, sizeWithUnfilteredQuery());

        dataProvider.setFilter(item -> !item.getValue().equals("Foo"));

        assertEquals("Previous filter should be reset when setting a new one",
                64, sizeWithUnfilteredQuery());

        dataProvider.setFilter(null);

        assertEquals("Setting filter to null should remove all filters", 100,
                sizeWithUnfilteredQuery());
    }

    @Test
    public void setFilter_valueProvider() {
        dataProvider.setFilter(StrBean::getValue, "Foo"::equals);

        assertEquals(36, sizeWithUnfilteredQuery());

        dataProvider.setFilter(StrBean::getValue,
                value -> !value.equals("Foo"));

        assertEquals("Previous filter should be reset when setting a new one",
                64, sizeWithUnfilteredQuery());
    }

    @Test
    public void setFilterEquals() {
        dataProvider.setFilterByValue(StrBean::getValue, "Foo");

        assertEquals(36, sizeWithUnfilteredQuery());

        dataProvider.setFilterByValue(StrBean::getValue, "Bar");

        assertEquals(23, sizeWithUnfilteredQuery());
    }

    @Test
    public void addFilter_withPreviousFilter() {
        dataProvider.setFilterByValue(StrBean::getValue, "Foo");

        dataProvider.addFilter(item -> item.getId() > 50);

        assertEquals("Both filters should be used", 17,
                sizeWithUnfilteredQuery());
    }

    @Test
    public void addFilter_noPreviousFilter() {
        dataProvider.addFilter(item -> item.getId() > 50);

        assertEquals(48, sizeWithUnfilteredQuery());
    }

    @Test
    public void addFilter_valueProvider() {
        dataProvider.setFilter(item -> item.getId() > 50);

        dataProvider.addFilter(StrBean::getValue, "Foo"::equals);

        assertEquals("Both filters should be used", 17,
                sizeWithUnfilteredQuery());
    }

    @Test
    public void addFilterEquals() {
        dataProvider.setFilter(item -> item.getId() > 50);

        dataProvider.addFilterByValue(StrBean::getValue, "Foo");

        assertEquals("Both filters should be used", 17,
                sizeWithUnfilteredQuery());
    }

    @Test
    public void addFilter_firstAddedUsedFirst() {
        dataProvider.addFilter(item -> false);
        dataProvider.addFilter(item -> {
            fail("This filter should never be invoked");
            return true;
        });

        assertEquals(0, sizeWithUnfilteredQuery());
    }

    @Test
    public void combineProviderAndQueryFilters() {
        dataProvider.addFilterByValue(StrBean::getValue, "Foo");

        int size = dataProvider.size(new Query<>(item -> item.getId() > 50));

        assertEquals("Both filters should be used", 17, size);
    }

    @Test
    public void providerFilterBeforeQueryFilter() {
        dataProvider.setFilter(item -> false);

        int size = dataProvider.size(new Query<>(item -> {
            fail("This filter should never be invoked");
            return true;
        }));

        assertEquals(0, size);
    }

    @Test
    public void filteringBy_itemPredicate() {
        DataProvider<StrBean, String> filteringBy = dataProvider.filteringBy(
                (item, filterValue) -> item.getValue().equals(filterValue));

        assertSizeWithFilter(36, filteringBy, "Foo");
    }

    @Test
    public void filteringBy_equals() {
        DataProvider<StrBean, String> filteringBy = dataProvider
                .filteringByEquals(StrBean::getValue);

        assertSizeWithFilter(36, filteringBy, "Foo");
    }

    @Test
    public void filteringBy_propertyValuePredicate() {
        DataProvider<StrBean, Integer> filteringBy = dataProvider.filteringBy(
                StrBean::getId,
                (propertyValue, filterValue) -> propertyValue >= filterValue);

        assertSizeWithFilter(90, filteringBy, 10);
    }

    @Test
    public void filteringBy_caseInsensitiveSubstring() {
        DataProvider<StrBean, String> filteringBy = dataProvider
                .filteringBySubstring(StrBean::getValue, Locale.ENGLISH);

        assertSizeWithFilter(36, filteringBy, "oo");
        assertSizeWithFilter(36, filteringBy, "Oo");
    }

    @Test
    public void filterBy_caseInsensitivePrefix() {
        DataProvider<StrBean, String> filteringBy = dataProvider
                .filteringByPrefix(StrBean::getValue, Locale.ENGLISH);

        assertSizeWithFilter(36, filteringBy, "Fo");
        assertSizeWithFilter(36, filteringBy, "fo");
        assertSizeWithFilter(0, filteringBy, "oo");
    }

    @Override
    protected void setSortOrder(List<QuerySortOrder> sortOrder,
            Comparator<StrBean> comp) {
        SerializableComparator<StrBean> serializableComp = comp::compare;
        getDataProvider().setSortComparator(serializableComp);
    }

}
