package com.vaadin.tests.components.treegrid;

import com.vaadin.data.TreeData;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class TreeGridInWindow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeGrid<String> treeGrid = new TreeGrid<>();
        treeGrid.addColumn(Object::toString).setCaption("Column");

        TreeData<String> data = treeGrid.getTreeData();

        data.addRootItems("parent");
        data.addItems("parent", "child1", "child2");
        data.addItems("child1", "grandchild1", "grandchild2");
        data.addItems("child2", "grandchild3", "grandchild4");

        treeGrid.expand("parent", "child1", "child2");

        Window window = new Window("Window", treeGrid);

        Button openWindow = new Button("Open window", event -> {
            UI.getCurrent().addWindow(window);
        });

        getLayout().addComponent(openWindow);
    }
}
