package com.vaadin.data.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.vaadin.data.TreeData;
import com.vaadin.server.SerializablePredicate;

public class TreeDataProviderTest
        extends DataProviderTestBase<TreeDataProvider<StrBean>> {

    private TreeData<StrBean> data;
    private List<StrBean> flattenedData;
    private List<StrBean> rootData;

    @Override
    public void setUp() {
        List<StrBean> randomBeans = StrBean.generateRandomBeans(20);
        flattenedData = new ArrayList<>();
        rootData = new ArrayList<>();

        data = new TreeData<>();
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
    public void treeData_add_item_parent_not_in_hierarchy_throws() {
        new TreeData<>().addItem(new StrBean("", 0, 0), new StrBean("", 0, 0));
    }

    @Test(expected = NullPointerException.class)
    public void treeData_add_null_item_throws() {
        new TreeData<>().addItem(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void treeData_add_item_already_in_hierarchy_throws() {
        StrBean bean = new StrBean("", 0, 0);
        new TreeData<>().addItem(null, bean).addItem(null, bean);
    }

    @Test
    public void treeData_remove_root_item() {
        data.removeItem(null);
        assertTrue(data.getChildren(null).isEmpty());
    }

    @Test
    public void treeData_clear() {
        data.clear();
        assertTrue(data.getChildren(null).isEmpty());
    }

    @Test
    public void treeData_re_add_removed_item() {
        StrBean item = rootData.get(0);
        data.removeItem(item).addItem(null, item);
        assertTrue(data.getChildren(null).contains(item));
    }

    @Test
    public void treeData_get_parent() {
        StrBean root = rootData.get(0);
        StrBean firstChild = data.getChildren(root).get(0);
        assertNull(data.getParent(root));
        assertEquals(root, data.getParent(firstChild));
    }

    @Test
    public void treeData_set_parent() {
        StrBean item1 = rootData.get(0);
        StrBean item2 = rootData.get(1);
        assertEquals(0, data.getChildren(item2).size());
        assertEquals(10, data.getRootItems().size());

        // Move item1 as item2's child
        data.setParent(item1, item2);
        assertEquals(1, data.getChildren(item2).size());
        assertEquals(9, data.getRootItems().size());
        assertEquals(item1, data.getChildren(item2).get(0));

        // Move back to root
        data.setParent(item1, null);
        assertEquals(0, data.getChildren(item2).size());
        assertEquals(10, data.getRootItems().size());
    }

    @Test
    public void treeData_move_after_sibling() {
        StrBean root0 = rootData.get(0);
        StrBean root9 = rootData.get(9);
        assertEquals(root0, data.getRootItems().get(0));
        assertEquals(root9, data.getRootItems().get(9));

        // Move to last position
        data.moveAfterSibling(root0, root9);
        assertEquals(root0, data.getRootItems().get(9));
        assertEquals(root9, data.getRootItems().get(8));

        // Move back to first position
        data.moveAfterSibling(root0, null);
        assertEquals(root0, data.getRootItems().get(0));
        assertEquals(root9, data.getRootItems().get(9));

        StrBean child0 = data.getChildren(root0).get(0);
        StrBean child2 = data.getChildren(root0).get(2);

        // Move first child to different position
        data.moveAfterSibling(child0, child2);
        assertEquals(2, data.getChildren(root0).indexOf(child0));
        assertEquals(1, data.getChildren(root0).indexOf(child2));

        // Move child back to first position
        data.moveAfterSibling(child0, null);
        assertEquals(0, data.getChildren(root0).indexOf(child0));
        assertEquals(2, data.getChildren(root0).indexOf(child2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void treeData_move_after_sibling_different_parents() {
        StrBean root0 = rootData.get(0);
        StrBean wrongSibling = data.getChildren(root0).get(0);

        data.moveAfterSibling(root0, wrongSibling);
    }

    @Test
    public void treeData_root_items() {
        TreeData<String> data = new TreeData<>();
        TreeData<String> dataVarargs = new TreeData<>();
        TreeData<String> dataCollection = new TreeData<>();
        TreeData<String> dataStream = new TreeData<>();

        data.addItems(null, "a", "b", "c");
        dataVarargs.addRootItems("a", "b", "c");
        dataCollection.addRootItems(Arrays.asList("a", "b", "c"));
        dataStream.addRootItems(Arrays.asList("a", "b", "c").stream());

        assertEquals(data.getRootItems(), dataVarargs.getRootItems());
        assertEquals(data.getRootItems(), dataCollection.getRootItems());
        assertEquals(data.getRootItems(), dataStream.getRootItems());
    }

    @Test
    public void populate_treeData_with_child_item_provider() {
        TreeData<String> stringData = new TreeData<>();
        List<String> rootItems = Arrays.asList("a", "b", "c");
        stringData.addItems(rootItems, item -> {
            if (item.length() >= 3 || item.startsWith("c")) {
                return Arrays.asList();
            }
            return Arrays.asList(item + "/a", item + "/b", item + "/c");
        });
        assertEquals(stringData.getChildren("a"),
                Arrays.asList("a/a", "a/b", "a/c"));
        assertEquals(stringData.getChildren("b"),
                Arrays.asList("b/a", "b/b", "b/c"));
        assertEquals(stringData.getChildren("c"), Arrays.asList());
        assertEquals(stringData.getChildren("a/b"), Arrays.asList());
    }

    @Test
    public void populate_treeData_with_stream_child_item_provider() {
        TreeData<String> stringData = new TreeData<>();
        Stream<String> rootItems = Stream.of("a", "b", "c");
        stringData.addItems(rootItems, item -> {
            if (item.length() >= 3 || item.startsWith("c")) {
                return Stream.empty();
            }
            return Stream.of(item + "/a", item + "/b", item + "/c");
        });
        assertEquals(stringData.getChildren("a"),
                Arrays.asList("a/a", "a/b", "a/c"));
        assertEquals(stringData.getChildren("b"),
                Arrays.asList("b/a", "b/b", "b/c"));
        assertEquals(stringData.getChildren("c"), Arrays.asList());
        assertEquals(stringData.getChildren("a/b"), Arrays.asList());
    }

    @Test
    public void filter_is_applied_to_children_provider_filter() {
        final SerializablePredicate<String> dataProviderFilter = item -> item
                .contains("Sub");
        final HierarchicalQuery<String, SerializablePredicate<String>> query = new HierarchicalQuery<>(
                null, null);
        filter_is_applied_to_children(dataProviderFilter, query);
    }

    @Test
    public void filter_is_applied_to_children_query_filter() {
        final SerializablePredicate<String> dataProviderFilter = null;
        final HierarchicalQuery<String, SerializablePredicate<String>> query = new HierarchicalQuery<>(
                item -> item.contains("Sub"), null);
        filter_is_applied_to_children(dataProviderFilter, query);
    }

    @Test
    public void filter_is_applied_to_children_both_filters() {
        final SerializablePredicate<String> dataProviderFilter = item -> item
                .contains("Sub");
        final HierarchicalQuery<String, SerializablePredicate<String>> query = new HierarchicalQuery<>(
                dataProviderFilter, null);
        filter_is_applied_to_children(dataProviderFilter, query);
    }

    private void filter_is_applied_to_children(
            final SerializablePredicate<String> dataProviderFilter,
            final HierarchicalQuery<String, SerializablePredicate<String>> query) {
        final TreeData<String> stringData = new TreeData<>();
        final String root1 = "Main";
        final List<String> children1 = Arrays.asList("Sub1", "Sub2");
        final String root2 = "Other";
        final List<String> children2 = Arrays.asList("Foo1", "Foo2");
        stringData.addRootItems(root1, root2);
        stringData.addItems(root1, children1);
        stringData.addItems(root2, children2);
        final TreeDataProvider<String> provider = new TreeDataProvider<>(
                stringData);
        provider.setFilter(dataProviderFilter);
        assertEquals("Unexpected amount of root items after filtering.", 1,
                provider.getChildCount(query));
        assertTrue(provider.fetchChildren(query).allMatch(root1::equals));
    }

    @Test
    public void setFilter() {
        getDataProvider().setFilter(item -> item.getValue().equals("Xyz")
                || item.getValue().equals("Baz"));

        assertEquals(10, sizeWithUnfilteredQuery());

        getDataProvider().setFilter(item -> !item.getValue().equals("Foo")
                && !item.getValue().equals("Xyz"));

        assertEquals(
                "Previous filter should be replaced when setting a new one", 14,
                sizeWithUnfilteredQuery());

        getDataProvider().setFilter(null);

        assertEquals("Setting filter to null should remove all filters", 20,
                sizeWithUnfilteredQuery());
    }

    @Test
    public void addFilter() {
        getDataProvider().addFilter(item -> item.getId() <= 10);
        getDataProvider().addFilter(item -> item.getId() >= 5);
        assertEquals(8, sizeWithUnfilteredQuery());
    }

    @Override
    public void filteringListDataProvider_convertFilter() {
        DataProvider<StrBean, String> strFilterDataProvider = getDataProvider()
                .withConvertedFilter(
                        text -> strBean -> strBean.getValue().contains(text));
        assertEquals("Only one item should match 'Xyz'", 1,
                strFilterDataProvider
                        .size(new HierarchicalQuery<>("Xyz", null)));
        assertEquals("No item should match 'Zyx'", 0, strFilterDataProvider
                .size(new HierarchicalQuery<>("Zyx", null)));
        assertEquals("Unexpected number of matches for 'Foo'", 4,
                strFilterDataProvider
                        .size(new HierarchicalQuery<>("Foo", null)));
        assertEquals("No items should've been filtered out", rootData.size(),
                strFilterDataProvider
                        .size(new HierarchicalQuery<>(null, null)));
    }

    @Override
    public void filteringListDataProvider_defaultFilterType() {
        assertEquals("Only one item should match 'Xyz'", 1,
                getDataProvider().size(new HierarchicalQuery<>(
                        strBean -> strBean.getValue().contains("Xyz"), null)));
        assertEquals("No item should match 'Zyx'", 0,
                dataProvider.size(new HierarchicalQuery<>(
                        strBean -> strBean.getValue().contains("Zyx"), null)));
        assertEquals("Unexpected number of matches for 'Foo'", 4,
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

        assertEquals("Sorted data and original data sizes don't match",
                getDataProvider().fetch(new HierarchicalQuery<>(null, null))
                        .count(),
                list.size());

        for (int i = 1; i < list.size(); ++i) {
            StrBean prev = list.get(i - 1);
            StrBean cur = list.get(i);
            // Test specific sort
            assertTrue(
                    "Failure: " + prev.getRandomNumber() + " > "
                            + cur.getRandomNumber(),
                    prev.getRandomNumber() <= cur.getRandomNumber());

            if (prev.getRandomNumber() == cur.getRandomNumber()) {
                // Test default sort
                assertTrue(prev.getValue().compareTo(cur.getValue()) <= 0);
                if (prev.getValue().equals(cur.getValue())) {
                    assertTrue(prev.getId() > cur.getId());
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

        assertEquals("Sorted data and original data sizes don't match",
                rootData.size(), list.size());

        for (int i = 1; i < list.size(); ++i) {
            StrBean prev = list.get(i - 1);
            StrBean cur = list.get(i);

            // Test default sort
            assertTrue(prev.getValue().compareTo(cur.getValue()) <= 0);
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

        assertNotEquals("First value should not match", rootData.get(0),
                list.get(0));

        assertEquals("Sorted data and original data sizes don't match",
                rootData.size(), list.size());

        rootData.sort(comp);
        for (int i = 0; i < rootData.size(); ++i) {
            assertEquals("Sorting result differed", rootData.get(i),
                    list.get(i));
        }
    }

    @Override
    protected TreeDataProvider<StrBean> createDataProvider() {
        return new TreeDataProvider<>(data);
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
        assertEquals(flattenedData, getFlattenedData(new ArrayList<>(), null));
        assertEquals(flattenedData,
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
