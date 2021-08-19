package com.vaadin.tests.components.treegrid;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.TreeGrid;

public class TreeGridProgrammaticExpand extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeData<String> data = new TreeData<>();
        String root = "Root";
        data.addItem(null, root);
        for (int i = 0; i < 15; i++) {
            String leaf = "Leaf " + i;
            data.addItem(root, leaf);
        }

        TreeDataProvider<String> treeDataProvider = new TreeDataProvider<>(
                data);
        TreeGrid<String> treeGrid = new TreeGrid<>();
        treeGrid.setDataProvider(treeDataProvider);
        treeGrid.addColumn(String::toString).setCaption("String")
                .setId("string");
        treeGrid.addColumn(i -> "--").setCaption("Nothing");

        Button button = new Button("Expand", e -> treeGrid.expand(root));

        addComponents(button, treeGrid);
    }

    @Override
    protected String getTestDescription() {
        return "There should be no client-side exception when clicking Leaf 4 "
                + "or lower before scrolling.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12372;
    }
}
