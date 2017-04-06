package com.vaadin.tests.components.treegrid;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.HierarchyData;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.data.provider.InMemoryHierarchicalDataProvider;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.Range;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.TreeGrid;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridBasicFeatures extends AbstractComponentTest<TreeGrid> {

    private TreeGrid<HierarchicalTestBean> grid;
    private InMemoryHierarchicalDataProvider<HierarchicalTestBean> inMemoryDataProvider;
    private LazyHierarchicalDataProvider lazyDataProvider;
    private HierarchicalDataProvider<HierarchicalTestBean, ?> loggingDataProvider;

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
        createExpandMenu();
        createCollapseMenu();
        createListenerMenu();
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
        loggingDataProvider = new InMemoryHierarchicalDataProvider<HierarchicalTestBean>(
                data) {

            @Override
            public Stream<HierarchicalTestBean> fetchChildren(
                    HierarchicalQuery<HierarchicalTestBean, SerializablePredicate<HierarchicalTestBean>> query) {
                Optional<HierarchicalTestBean> parentOptional = query
                        .getParentOptional();
                if (parentOptional.isPresent()) {
                    log("Children request: " + parentOptional.get() + " ; "
                            + Range.withLength(query.getOffset(),
                                    query.getLimit()));
                } else {
                    log("Root node request: " + Range
                            .withLength(query.getOffset(), query.getLimit()));
                }
                return super.fetchChildren(query);
            }
        };

    }

    @SuppressWarnings("unchecked")
    private void createDataProviderSelect() {
        @SuppressWarnings("rawtypes")
        LinkedHashMap<String, DataProvider> options = new LinkedHashMap<>();
        options.put("LazyHierarchicalDataProvider", lazyDataProvider);
        options.put("InMemoryHierarchicalDataProvider", inMemoryDataProvider);
        options.put("LoggingDataProvider", loggingDataProvider);

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

    @SuppressWarnings("unchecked")
    private void createExpandMenu() {
        createCategory("Server-side expand", CATEGORY_FEATURES);
        createClickAction("Expand 0 | 0", "Server-side expand",
                (treeGrid, value, data) -> treeGrid.expand(value),
                new HierarchicalTestBean(null, 0, 0));
        createClickAction("Expand 1 | 1", "Server-side expand",
                (treeGrid, value, data) -> treeGrid.expand(value),
                new HierarchicalTestBean("/0/0", 1, 1));
        createClickAction("Expand 2 | 1", "Server-side expand",
                (treeGrid, value, data) -> treeGrid.expand(value),
                new HierarchicalTestBean("/0/0/1/1", 2, 1));
    }

    @SuppressWarnings("unchecked")
    private void createCollapseMenu() {
        createCategory("Server-side collapse", CATEGORY_FEATURES);
        createClickAction("Collapse 0 | 0", "Server-side collapse",
                (treeGrid, value, data) -> treeGrid.collapse(value),
                new HierarchicalTestBean(null, 0, 0));
        createClickAction("Collapse 1 | 1", "Server-side collapse",
                (treeGrid, value, data) -> treeGrid.collapse(value),
                new HierarchicalTestBean("/0/0", 1, 1));
        createClickAction("Collapse 2 | 1", "Server-side collapse",
                (treeGrid, value, data) -> treeGrid.collapse(value),
                new HierarchicalTestBean("/0/0/1/1", 2, 1));
    }

    @SuppressWarnings("unchecked")
    private void createListenerMenu() {
        createListenerAction("Collapse listener", "State",
                treeGrid -> treeGrid.addCollapseListener(event -> log(
                        "Item collapsed (user originated: "
                                + event.isUserOriginated() + "): "
                                + event.getCollapsedItem())));
        createListenerAction("Expand listener", "State",
                treeGrid -> treeGrid.addExpandListener(event -> log(
                        "Item expanded (user originated: "
                                + event.isUserOriginated() + "): "
                                + event.getExpandedItem())));
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
