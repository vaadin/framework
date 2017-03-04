package com.vaadin.tests.components.treegrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.shared.Registration;
import com.vaadin.tests.components.treegrid.TreeGridBasicFeatures.HierarchicalTestBean;

public class LazyHierarchicalDataProvider
        implements HierarchicalDataProvider<HierarchicalTestBean, Void> {

    private final int nodesPerLevel;
    private final int depth;

    public LazyHierarchicalDataProvider(int nodesPerLevel, int depth) {
        this.nodesPerLevel = nodesPerLevel;
        this.depth = depth;
    }

    @Override
    public boolean isInMemory() {
        return false;
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

    @Override
    public int getChildCount(
            HierarchicalQuery<HierarchicalTestBean, Void> query) {

        Optional<Integer> count = query.getParentOptional()
                .flatMap(parent -> Optional.of(Integer.valueOf(
                        (internalHasChildren(parent) ? nodesPerLevel : 0))));

        return count.orElse(nodesPerLevel);
    }

    @Override
    public Stream<HierarchicalTestBean> fetchChildren(
            HierarchicalQuery<HierarchicalTestBean, Void> query) {
        final int depth = query.getParentOptional().isPresent()
                ? query.getParent().getDepth() + 1 : 0;
        final Optional<String> parentKey = query.getParentOptional()
                .flatMap(parent -> Optional.of(parent.getId()));

        List<HierarchicalTestBean> list = new ArrayList<>();
        for (int i = 0; i < query.getLimit(); i++) {
            list.add(new HierarchicalTestBean(parentKey.orElse(null), depth,
                    i + query.getOffset()));
        }
        return list.stream();
    }

    @Override
    public boolean hasChildren(HierarchicalTestBean item) {
        return internalHasChildren(item);
    }

    private boolean internalHasChildren(HierarchicalTestBean node) {
        return node.getDepth() < depth;
        // return node.getDepth() < depth && node.getIndex() % 2 == 1;
    }

}
