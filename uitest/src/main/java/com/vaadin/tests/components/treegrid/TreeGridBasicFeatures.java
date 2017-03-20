package com.vaadin.tests.components.treegrid;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.HierarchyData;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.InMemoryHierarchicalDataProvider;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.TreeGrid;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridBasicFeatures extends AbstractComponentTest<TreeGrid> {

    private TreeGrid<HierarchicalTestBean> grid;
    private InMemoryHierarchicalDataProvider<HierarchicalTestBean> inMemoryDataProvider;
    private LazyHierarchicalDataProvider lazyDataProvider;

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
        initializeDataProviders();
        grid = new TreeGrid<>();
        grid.setSizeFull();
        grid.addColumn(HierarchicalTestBean::toString).setCaption("String")
                .setId("string");
        grid.addColumn(HierarchicalTestBean::getDepth).setCaption("Depth")
                .setId("depth").setDescriptionGenerator(
                        t -> "Hierarchy depth: " + t.getDepth());
        grid.addColumn(HierarchicalTestBean::getIndex)
                .setCaption("Index on this depth").setId("index");
        grid.setHierarchyColumn("string");
        grid.setDataProvider(new LazyHierarchicalDataProvider(3, 2));

        grid.setId("testComponent");
        addTestComponent(grid);
    }

    @Override
    protected void createActions() {
        super.createActions();

        createDataProviderSelect();
        createHierarchyColumnSelect();
        createCollapseAllowedSelect();
    }

    private void initializeDataProviders() {
        HierarchyData<HierarchicalTestBean> data = new HierarchyData<>();

        List<Integer> ints = Arrays.asList(0, 1, 2);

        ints.stream().forEach(index -> {
            HierarchicalTestBean bean = new HierarchicalTestBean(null, 0,
                    index);
            data.addItem(null, bean);
            ints.stream().forEach(childIndex -> {
                HierarchicalTestBean childBean = new HierarchicalTestBean(
                        bean.getId(), 1, childIndex);
                data.addItem(bean, childBean);
                ints.stream()
                        .forEach(grandChildIndex -> data.addItem(childBean,
                                new HierarchicalTestBean(childBean.getId(), 2,
                                        grandChildIndex)));
            });
        });

        inMemoryDataProvider = new InMemoryHierarchicalDataProvider<>(data);
        lazyDataProvider = new LazyHierarchicalDataProvider(3, 2);
    }

    @SuppressWarnings("unchecked")
    private void createDataProviderSelect() {
        @SuppressWarnings("rawtypes")
        LinkedHashMap<String, DataProvider> options = new LinkedHashMap<>();
        options.put("LazyHierarchicalDataProvider", lazyDataProvider);
        options.put("InMemoryHierarchicalDataProvider", inMemoryDataProvider);

        createSelectAction("Set data provider", CATEGORY_FEATURES, options,
                "LazyHierarchicalDataProvider",
                (treeGrid, value, data) -> treeGrid.setDataProvider(value));
    }

    private void createHierarchyColumnSelect() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        grid.getColumns().stream()
                .forEach(column -> options.put(column.getId(), column.getId()));

        createSelectAction("Set hierarchy column", CATEGORY_FEATURES, options,
                grid.getColumns().get(0).getId(),
                (treeGrid, value, data) -> treeGrid.setHierarchyColumn(value));
    }

    private void createCollapseAllowedSelect() {
        LinkedHashMap<String, SerializablePredicate<HierarchicalTestBean>> options = new LinkedHashMap<>();
        options.put("all allowed", t -> true);
        options.put("all disabled", t -> false);
        options.put("depth 0 disabled", t -> t.getDepth() != 0);
        options.put("depth 1 disabled", t -> t.getDepth() != 1);

        createSelectAction("Collapse allowed", CATEGORY_FEATURES, options,
                "all allowed", (treeGrid, value, data) -> treeGrid
                        .setItemCollapseAllowedProvider(value));
    }

    static class HierarchicalTestBean {

        private final String id;
        private final int depth;
        private final int index;

        public HierarchicalTestBean(String parentId, int depth, int index) {
            id = (parentId == null ? "" : parentId) + "/" + depth + "/" + index;
            this.depth = depth;
            this.index = index;
        }

        public int getDepth() {
            return depth;
        }

        public int getIndex() {
            return index;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return depth + " | " + index;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            HierarchicalTestBean other = (HierarchicalTestBean) obj;
            if (id == null) {
                if (other.id != null) {
                    return false;
                }
            } else if (!id.equals(other.id)) {
                return false;
            }
            return true;
        }

    }
}
