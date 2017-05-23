package com.vaadin.tests.components.treegrid;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.Range;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.tests.data.bean.HierarchicalTestBean;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.ItemCollapseAllowedProvider;
import com.vaadin.ui.TreeGrid;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridBasicFeatures extends AbstractComponentTest<TreeGrid> {

    private TreeGrid<HierarchicalTestBean> grid;
    private TreeDataProvider<HierarchicalTestBean> inMemoryDataProvider;
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
        createSelectionModeMenu();
    }

    private void initializeDataProviders() {
        TreeData<HierarchicalTestBean> data = new TreeData<>();

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

        inMemoryDataProvider = new TreeDataProvider<>(data);
        lazyDataProvider = new LazyHierarchicalDataProvider(3, 2);
        loggingDataProvider = new TreeDataProvider<HierarchicalTestBean>(
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
        options.put("TreeDataProvider", inMemoryDataProvider);
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

    @SuppressWarnings("unchecked")
    private void createCollapseAllowedSelect() {
        LinkedHashMap<String, ItemCollapseAllowedProvider<HierarchicalTestBean>> options = new LinkedHashMap<>();
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
                treeGrid -> treeGrid.addCollapseListener(
                        event -> log("Item collapsed (user originated: "
                                + event.isUserOriginated() + "): "
                                + event.getCollapsedItem())));
        createListenerAction("Expand listener", "State",
                treeGrid -> treeGrid.addExpandListener(
                        event -> log("Item expanded (user originated: "
                                + event.isUserOriginated() + "): "
                                + event.getExpandedItem())));
    }

    private void createSelectionModeMenu() {
        LinkedHashMap<String, SelectionMode> options = new LinkedHashMap<>();
        options.put("none", SelectionMode.NONE);
        options.put("single", SelectionMode.SINGLE);
        options.put("multi", SelectionMode.MULTI);

        createSelectAction("Selection mode", "State", options, "single",
                (treeGrid, value, data) -> treeGrid.setSelectionMode(value));
    }
}
