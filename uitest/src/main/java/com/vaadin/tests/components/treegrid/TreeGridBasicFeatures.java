package com.vaadin.tests.components.treegrid;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.HierarchyData;
import com.vaadin.data.provider.InMemoryHierarchicalDataProvider;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.TreeGrid;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridBasicFeatures extends AbstractComponentTest<TreeGrid> {

    private TreeGrid<HierarchicalTestBean> grid;
    private InMemoryHierarchicalDataProvider<HierarchicalTestBean> inMemoryDataProvider;

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
        initializeInMemoryDataProvider();
        grid = new TreeGrid<>();
        grid.setSizeFull();
        grid.addColumn(HierarchicalTestBean::toString).setCaption("String")
                .setId("string");
        grid.addColumn(HierarchicalTestBean::getDepth).setCaption("Depth")
                .setId("depth");
        grid.addColumn(HierarchicalTestBean::getIndex)
                .setCaption("Index on this depth").setId("index");
        grid.setHierarchyColumn("string");
        // grid.setDataProvider(inMemoryDataProvider);
        grid.setDataProvider(new LazyHierarchicalDataProvider(3, 2));

        grid.setId("testComponent");
        addTestComponent(grid);
    }

    @Override
    protected void createActions() {
        super.createActions();

        createHierarchyColumnSelect();
    }

    private void initializeInMemoryDataProvider() {
        HierarchyData<HierarchicalTestBean> data = new HierarchyData<>();

        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

        ints.stream().forEach(index -> {
            HierarchicalTestBean parentBean = new HierarchicalTestBean(null, 0,
                    index);
            data.addItem(null, parentBean);
            ints.stream()
                    .forEach(childIndex -> data.addItem(
                            parentBean,
                            new HierarchicalTestBean(parentBean.getId(), 1,
                                    childIndex)));
        });

        inMemoryDataProvider = new InMemoryHierarchicalDataProvider<>(data);
    }

    private void createHierarchyColumnSelect() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        grid.getColumns().stream()
                .forEach(column -> options.put(column.getId(), column.getId()));

        createSelectAction("Set hierarchy column", CATEGORY_FEATURES, options,
                grid.getColumns().get(0).getId(),
                (treeGrid, value, data) -> treeGrid.setHierarchyColumn(value));
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
