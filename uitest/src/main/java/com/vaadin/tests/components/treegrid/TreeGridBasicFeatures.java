package com.vaadin.tests.components.treegrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.shared.Registration;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TreeGrid;

public class TreeGridBasicFeatures extends AbstractComponentTest<TreeGrid> {

    private TreeGrid<TestBean> grid;
    private TestDataProvider dataProvider = new TestDataProvider();

    @Override
    public TreeGrid getComponent() {
        return grid;
    }

    @Override
    protected Class<TreeGrid> getTestClass() {
        return TreeGrid.class;
    }

    @Override
    protected void initializeComponents() {
        grid = new TreeGrid<>();
        grid.setSizeFull();
        grid.addColumn(TestBean::getStringValue).setId("First column");
        grid.addColumn(TestBean::getStringValue).setId("Second column");
        grid.setHierarchyColumn("First column");
        grid.setDataProvider(dataProvider);

        grid.setId("testComponent");
        addTestComponent(grid);
    }

    @Override
    protected void createActions() {
        super.createActions();

        createHierarchyColumnSelect();
        createToggleCollapseSelect();
    }

    private void createHierarchyColumnSelect() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        grid.getColumns().stream()
                .forEach(column -> options.put(column.getId(), column.getId()));

        createSelectAction("Set hierarchy column", CATEGORY_FEATURES, options,
                grid.getColumns().get(0).getId(),
                (treeGrid, value, data) -> treeGrid.setHierarchyColumn(value));
    }

    private void createToggleCollapseSelect() {
        MenuItem menu = createCategory("Toggle expand", CATEGORY_FEATURES);
        dataProvider.getAllItems().forEach(testBean -> {
            createClickAction(testBean.getStringValue(), "Toggle expand",
                    (grid, bean, data) -> grid.toggleCollapse(bean), testBean);
        });
    }

    private static class TestBean {

        private String stringValue;

        public TestBean(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }

    private static class TestDataProvider
            implements HierarchicalDataProvider<TestBean, Void> {

        private static class HierarchyWrapper<T> {
            private T item;
            private T parent;
            private Set<T> children;
            private boolean collapsed;

            public HierarchyWrapper(T item, T parent, boolean collapsed) {
                this.item = item;
                this.parent = parent;
                this.collapsed = collapsed;
                children = new LinkedHashSet<>();
            }

            public T getItem() {
                return item;
            }

            public void setItem(T item) {
                this.item = item;
            }

            public T getParent() {
                return parent;
            }

            public void setParent(T parent) {
                this.parent = parent;
            }

            public Set<T> getChildren() {
                return children;
            }

            public void setChildren(Set<T> children) {
                this.children = children;
            }

            public boolean isCollapsed() {
                return collapsed;
            }

            public void setCollapsed(boolean collapsed) {
                this.collapsed = collapsed;
            }
        }

        private Map<TestBean, HierarchyWrapper<TestBean>> itemToWrapperMap;
        private Map<HierarchyWrapper<TestBean>, TestBean> wrapperToItemMap;
        private Map<TestBean, HierarchyWrapper<TestBean>> rootNodes;

        public TestDataProvider() {
            itemToWrapperMap = new LinkedHashMap<>();
            wrapperToItemMap = new LinkedHashMap<>();
            rootNodes = new LinkedHashMap<>();

            List<String> strings = Arrays.asList("a", "b", "c");

            strings.stream().forEach(string -> {
                TestBean rootBean = new TestBean(string);

                HierarchyWrapper<TestBean> wrappedParent = new HierarchyWrapper<>(
                        rootBean, null, true);
                itemToWrapperMap.put(rootBean, wrappedParent);
                wrapperToItemMap.put(wrappedParent, rootBean);

                List<TestBean> children = strings.stream().map(string2 -> {
                    TestBean childBean = new TestBean(string + "/" + string2);
                    HierarchyWrapper<TestBean> wrappedChild = new HierarchyWrapper<>(
                            new TestBean(string + "/" + string2), rootBean,
                            true);
                    itemToWrapperMap.put(childBean, wrappedChild);
                    wrapperToItemMap.put(wrappedChild, childBean);
                    return childBean;
                }).collect(Collectors.toList());

                wrappedParent.setChildren(new LinkedHashSet<>(children));

                rootNodes.put(rootBean, wrappedParent);
            });
        }

        @Override
        public int getDepth(TestBean item) {
            int depth = 0;
            while (getItem(item) != null) {
                item = getItem(item).getParent();
                depth++;
            }
            return depth;
        }

        @Override
        public boolean isInMemory() {
            return true;
        }

        @Override
        public void refreshItem(TestBean item) {
            // NO-OP
        }

        @Override
        public void refreshAll() {
            // NO-OP
        }

        @Override
        public Registration addDataProviderListener(
                DataProviderListener<TestBean> listener) {
            return () -> {
            };
        }

        private List<TestBean> getAllItems() {
            return new ArrayList<>(itemToWrapperMap.keySet());
        }

        private List<TestBean> getVisibleItemsRecursive(
                Collection<HierarchyWrapper<TestBean>> wrappedItems) {
            List<TestBean> items = new ArrayList<>();

            wrappedItems.forEach(wrappedItem -> {
                items.add(wrapperToItemMap.get(wrappedItem));
                if (!wrappedItem.isCollapsed()) {
                    List<HierarchyWrapper<TestBean>> wrappedChildren = wrappedItem
                            .getChildren().stream()
                            .map(childItem -> getItem(childItem))
                            .collect(Collectors.toList());
                    items.addAll(getVisibleItemsRecursive(wrappedChildren));
                }
            });
            return items;
        }

        @Override
        public int size(Query<TestBean, Void> query) {
            return getVisibleItemsRecursive(rootNodes.values()).size();
        }

        @Override
        public Stream<TestBean> fetch(Query<TestBean, Void> query) {
            return getVisibleItemsRecursive(rootNodes.values()).stream();
        }

        @Override
        public boolean isRoot(TestBean item) {
            return getItem(item).getParent() == null;
        }

        @Override
        public TestBean getParent(TestBean item) {
            return getItem(item).getParent();
        }

        @Override
        public boolean isCollapsed(TestBean item) {
            return getItem(item).isCollapsed();
        }

        @Override
        public boolean hasChildren(TestBean item) {
            return !getItem(item).getChildren().isEmpty();
        }

        @Override
        public void setCollapsed(TestBean item, boolean b) {
            getItem(item).setCollapsed(b);
        }

        private HierarchyWrapper<TestBean> getItem(TestBean item) {
            return itemToWrapperMap.get(item);
        }
    }
}
