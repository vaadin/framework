package com.vaadin.tests.components.treegrid;

import com.vaadin.data.TreeData;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.TreeGrid;

public class TreeGridExpandOnAdd extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeGrid<Item> tree = new TreeGrid<>(Item.class);
        Button button = new Button("Add new item", e -> addItem(tree));
        addComponents(tree, button);
    }

    private void addItem(TreeGrid<Item> tree) {
        Item parent = new Item("Parent");
        Item child = new Item("Child");

        TreeData<Item> treeData = tree.getTreeData();
        treeData.addItem(null, parent);
        treeData.addItem(parent, child);

        tree.getDataProvider().refreshAll();

        tree.expand(parent);
    }

    public static class Item {
        private String name;

        public Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
