package com.vaadin.tests.components.treegrid;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TreeGrid;

public class TreeGridInitialExpand extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeGrid<String> treeGrid = new TreeGrid<>();
        treeGrid.setCaption("Test");
        treeGrid.addColumn(String::toString).setCaption("String");
        TreeData<String> data = new TreeData<>();
        data.addItem(null, "parent1");
        data.addItem("parent1", "parent1-child1");
        data.addItem("parent1", "parent1-child2");
        data.addItem(null, "parent2");
        data.addItem("parent2", "parent2-child2");
        treeGrid.setDataProvider(new TreeDataProvider<>(data));
        treeGrid.setHeightByRows(5);
        treeGrid.expand("parent1");
        treeGrid.expand("parent2");
        addComponent(treeGrid);
    }
}
