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
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.shared.Registration;
import com.vaadin.tests.components.treegrid.TreeGridBasicFeatures.HierarchicalTestBean;

class InMemoryHierarchicalDataProvider
        implements HierarchicalDataProvider<HierarchicalTestBean, Void> {

    private int counter;

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

    private Map<HierarchicalTestBean, InMemoryHierarchicalDataProvider.HierarchyWrapper<HierarchicalTestBean>> itemToWrapperMap;
    private Map<InMemoryHierarchicalDataProvider.HierarchyWrapper<HierarchicalTestBean>, HierarchicalTestBean> wrapperToItemMap;
    private Map<HierarchicalTestBean, InMemoryHierarchicalDataProvider.HierarchyWrapper<HierarchicalTestBean>> rootNodes;

    public InMemoryHierarchicalDataProvider() {
        itemToWrapperMap = new LinkedHashMap<>();
        wrapperToItemMap = new LinkedHashMap<>();
        rootNodes = new LinkedHashMap<>();

        List<Integer> ints = Arrays.asList(1, 2, 3);

        ints.stream().forEach(index -> {
            HierarchicalTestBean rootBean = new HierarchicalTestBean(null, 0,
                    index);

            InMemoryHierarchicalDataProvider.HierarchyWrapper<HierarchicalTestBean> wrappedParent = new InMemoryHierarchicalDataProvider.HierarchyWrapper<>(
                    rootBean, null, true);
            itemToWrapperMap.put(rootBean, wrappedParent);
            wrapperToItemMap.put(wrappedParent, rootBean);

            List<HierarchicalTestBean> children = ints.stream().map(index2 -> {
                HierarchicalTestBean childBean = new HierarchicalTestBean(
                        index.toString(), 1, index2);
                InMemoryHierarchicalDataProvider.HierarchyWrapper<HierarchicalTestBean> wrappedChild = new InMemoryHierarchicalDataProvider.HierarchyWrapper<>(
                        new HierarchicalTestBean(index2.toString(), 1, index2),
                        rootBean, true);
                itemToWrapperMap.put(childBean, wrappedChild);
                wrapperToItemMap.put(wrappedChild, childBean);
                return childBean;
            }).collect(Collectors.toList());

            wrappedParent.setChildren(new LinkedHashSet<>(children));

            rootNodes.put(rootBean, wrappedParent);
        });
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public void refreshItem(HierarchicalTestBean item) {
        // NO-OP
    }

    @Override
    public void refreshAll() {
        // NO-OP
    }

    @Override
    public Registration addDataProviderListener(
            DataProviderListener<HierarchicalTestBean> listener) {
        return () -> {
        };
    }

    List<HierarchicalTestBean> getAllItems() {
        return new ArrayList<>(itemToWrapperMap.keySet());
    }

    private List<HierarchicalTestBean> getVisibleItemsRecursive(
            Collection<InMemoryHierarchicalDataProvider.HierarchyWrapper<HierarchicalTestBean>> wrappedItems) {
        List<HierarchicalTestBean> items = new ArrayList<>();

        wrappedItems.forEach(wrappedItem -> {
            items.add(wrapperToItemMap.get(wrappedItem));
            if (!wrappedItem.isCollapsed()) {
                List<InMemoryHierarchicalDataProvider.HierarchyWrapper<HierarchicalTestBean>> wrappedChildren = wrappedItem
                        .getChildren().stream()
                        .map(childItem -> getItem(childItem))
                        .collect(Collectors.toList());
                items.addAll(getVisibleItemsRecursive(wrappedChildren));
            }
        });
        return items;
    }

    @Override
    public boolean hasChildren(HierarchicalTestBean item) {
        return !getItem(item).getChildren().isEmpty();
    }

    private InMemoryHierarchicalDataProvider.HierarchyWrapper<HierarchicalTestBean> getItem(
            HierarchicalTestBean item) {
        return itemToWrapperMap.get(item);
    }

    @Override
    public int getChildCount(
            HierarchicalQuery<HierarchicalTestBean, Void> query) {
        return getVisibleItemsRecursive(rootNodes.values()).size();
    }

    @Override
    public Stream<HierarchicalTestBean> fetchChildren(
            HierarchicalQuery<HierarchicalTestBean, Void> query) {
        return getVisibleItemsRecursive(rootNodes.values()).stream();
    }
}