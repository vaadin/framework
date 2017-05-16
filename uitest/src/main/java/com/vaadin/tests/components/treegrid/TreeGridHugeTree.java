package com.vaadin.tests.components.treegrid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.HierarchyData;
import com.vaadin.data.provider.SimpleHierarchicalDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.TreeGrid;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridHugeTree
        extends AbstractTestUI {

    private TreeGrid<String> treeGrid;
    private SimpleHierarchicalDataProvider<String> inMemoryDataProvider;

    private void initializeDataProvider() {
        inMemoryDataProvider = new SimpleHierarchicalDataProvider<>();
        for (int i = 0; i < 3; i++) {
            String granddad = "Granddad " + i;
            inMemoryDataProvider.addItem(null, granddad);
            for (int j = 0; j < 3; j++) {
                String dad = "Dad " + i + "/" + j;
                inMemoryDataProvider.addItem(granddad, dad);
                for (int k = 0; k < 300; k++) {
                    String son = "Son " + i + "/" + j + "/" + k;
                    inMemoryDataProvider.addItem(dad, son);
                }
            }
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        initializeDataProvider();
        treeGrid = new TreeGrid<>();
        treeGrid.setDataProvider(inMemoryDataProvider);
        treeGrid.setSizeFull();
        treeGrid.addColumn(String::toString).setCaption("String")
                .setId("string");
        treeGrid.addColumn((i) -> "--").setCaption("Nothing");
        treeGrid.setHierarchyColumn("string");
        treeGrid.setId("testComponent");

        Button expand = new Button("Expand Granddad 1");
        expand.addClickListener(event -> treeGrid.expand("Granddad 1"));
        Button collapse = new Button("Collapse Granddad 1");
        collapse.addClickListener(event -> treeGrid.collapse("Granddad 1"));

        addComponents(treeGrid, expand, collapse);
    }

}