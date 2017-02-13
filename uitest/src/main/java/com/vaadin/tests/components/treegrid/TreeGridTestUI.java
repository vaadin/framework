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
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Registration;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TreeGrid;

public class TreeGridTestUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final TreeGrid<DemoBean> grid = new TreeGrid<>();
        grid.setSizeFull();
        grid.addColumn(DemoBean::getStringValue).setId("hierarchy");
        grid.addColumn(DemoBean::getStringValue);
        // Currently setting id through Grid#setId doesn't affect the client
        // side id of the column, which the following method relies on.
        // The current implementation of TreeGrid is just set to make the first
        // column its hierarchy column regardless if the following method is
        // called or not.
        grid.setHierarchyColumn("hierarchy");

        DemoDataProvider dataProvider = new DemoDataProvider();
        grid.setDataProvider(dataProvider);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);

        addComponent(grid);
    }

    private static class DemoBean {

        private String stringValue;

        public DemoBean(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }

    private static class DemoDataProvider
            implements HierarchicalDataProvider<DemoBean, Void> {

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

        private Map<DemoBean, HierarchyWrapper<DemoBean>> itemToWrapperMap;
        private Map<HierarchyWrapper<DemoBean>, DemoBean> wrapperToItemMap;
        private Map<DemoBean, HierarchyWrapper<DemoBean>> rootNodes;

        public DemoDataProvider() {
            itemToWrapperMap = new LinkedHashMap<>();
            wrapperToItemMap = new LinkedHashMap<>();
            rootNodes = new LinkedHashMap<>();

            List<String> strings = Arrays.asList("a", "b", "c");

            strings.stream().forEach(string -> {
                DemoBean rootBean = new DemoBean(string);

                HierarchyWrapper<DemoBean> wrappedParent = new HierarchyWrapper<>(
                        rootBean, null, false);
                itemToWrapperMap.put(rootBean, wrappedParent);
                wrapperToItemMap.put(wrappedParent, rootBean);

                List<DemoBean> children = strings.stream().map(string2 -> {
                    DemoBean childBean = new DemoBean(string + "/" + string2);
                    HierarchyWrapper<DemoBean> wrappedChild = new HierarchyWrapper<>(
                            new DemoBean(string + "/" + string2), rootBean,
                            false);
                    itemToWrapperMap.put(childBean, wrappedChild);
                    wrapperToItemMap.put(wrappedChild, childBean);
                    return childBean;
                }).collect(Collectors.toList());

                wrappedParent.setChildren(new LinkedHashSet<>(children));

                rootNodes.put(rootBean, wrappedParent);
            });
        }

        @Override
        public int getDepth(DemoBean item) {
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
        public void refreshItem(DemoBean item) {
            // TODO Auto-generated method stub
        }

        @Override
        public void refreshAll() {
            // TODO Auto-generated method stub
        }

        @Override
        public Registration addDataProviderListener(
                DataProviderListener<DemoBean> listener) {
            return () -> {
            };
        }

        private List<DemoBean> getVisibleItemsRecursive(
                Collection<HierarchyWrapper<DemoBean>> wrappedItems) {
            List<DemoBean> items = new ArrayList<>();

            wrappedItems.forEach(wrappedItem -> {
                items.add(wrapperToItemMap.get(wrappedItem));
                if (wrappedItem.isCollapsed()) {
                    List<HierarchyWrapper<DemoBean>> wrappedChildren = wrappedItem
                            .getChildren().stream()
                            .map(childItem -> getItem(childItem))
                            .collect(Collectors.toList());
                    items.addAll(getVisibleItemsRecursive(wrappedChildren));
                }
            });
            return items;
        }

        @Override
        public int size(Query<DemoBean, Void> query) {
            return getVisibleItemsRecursive(rootNodes.values()).size();
        }

        @Override
        public Stream<DemoBean> fetch(Query<DemoBean, Void> query) {
            return getVisibleItemsRecursive(rootNodes.values()).stream();
        }

        @Override
        public boolean isRoot(DemoBean item) {
            return getItem(item).getParent() == null;
        }

        @Override
        public DemoBean getParent(DemoBean item) {
            return getItem(item).getParent();
        }

        @Override
        public boolean isCollapsed(DemoBean item) {
            return getItem(item).isCollapsed();
        }

        @Override
        public boolean hasChildren(DemoBean item) {
            return !getItem(item).getChildren().isEmpty();
        }

        @Override
        public void setCollapsed(DemoBean item, boolean b) {
            getItem(item).setCollapsed(b);
        }

        private HierarchyWrapper<DemoBean> getItem(DemoBean item) {
            return itemToWrapperMap.get(item);
        }
    }
}
