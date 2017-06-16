package com.vaadin.tests.components.treegrid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.TreeGrid;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridHugeTreeNavigation extends AbstractComponentTest<TreeGrid> {

    private TreeGrid<String> treeGrid;
    private TreeDataProvider<String> inMemoryDataProvider;

    @Override
    public TreeGrid getComponent() {
        return treeGrid;
    }

    @Override
    protected Class<TreeGrid> getTestClass() {
        return TreeGrid.class;
    }

    @Override
    protected void initializeComponents() {
        initializeDataProvider();
        treeGrid = new TreeGrid<>();
        treeGrid.setDataProvider(inMemoryDataProvider);
        treeGrid.setSizeFull();
        treeGrid.addColumn(String::toString).setCaption("String")
                .setId("string");
        treeGrid.addColumn((i) -> "--").setCaption("Nothing");
        treeGrid.setHierarchyColumn("string");
        treeGrid.setId("testComponent");
        treeGrid.setItemCollapseAllowedProvider(s -> !"Dad 2/1".equals(s));
        addTestComponent(treeGrid);
    }

    private void initializeDataProvider() {
        TreeData<String> data = new TreeData<>();
        for (int i = 0; i < 3; i++) {
            String granddad = "Granddad " + i;
            data.addItem(null, granddad);
            for (int j = 0; j < 3; j++) {
                String dad = "Dad " + i + "/" + j;
                data.addItem(granddad, dad);
                for (int k = 0; k < 300; k++) {
                    String son = "Son " + i + "/" + j + "/" + k;
                    data.addItem(dad, son);
                }
            }
        }
        inMemoryDataProvider = new TreeDataProvider<>(data);
    }

}
