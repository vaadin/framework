package com.vaadin.tests.components.treegrid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeGrid;

public class TreeGridDetailsExpandLast extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeData<String> data = new TreeData<>();
        List<String> roots = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            String root = "Root " + i;
            roots.add(root);
            data.addItem(null, root);
            for (int j = 0; j < 2; j++) {
                String leaf = "Leaf " + i + "/" + j;
                data.addItem(root, leaf);
            }
        }

        TreeDataProvider<String> treeDataProvider = new TreeDataProvider<>(
                data);
        TreeGrid<String> treeGrid = new TreeGrid<>();
        treeGrid.setDataProvider(treeDataProvider);
        treeGrid.addColumn(String::toString).setCaption("String")
                .setId("string");
        treeGrid.addColumn((i) -> "--").setCaption("Nothing");
        treeGrid.setHierarchyColumn("string");
        treeGrid.setDetailsGenerator(
                row -> new Label("details for " + row.toString()));
        treeGrid.addItemClickListener(event -> {
            treeGrid.setDetailsVisible(event.getItem(),
                    !treeGrid.isDetailsVisible(event.getItem()));
        });

        treeGrid.expand(roots);
        for (String id : roots) {
            treeGrid.setDetailsVisible(id, true);
        }
        treeGrid.collapse("Root 99");
        addComponent(treeGrid);
    }

    @Override
    protected String getTestDescription() {
        return "Details row heights should be taken into account when expanding rows. "
                + "Expanding the last row shouldn't cause exceptions or weird row positions.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11348;
    }
}
