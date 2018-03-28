package com.vaadin.tests.contextclick;

import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.Tree.TreeContextClickEvent;

public class TreeContextClick
        extends AbstractContextClickUI<Tree, TreeContextClickEvent> {

    @Override
    protected Tree createTestComponent() {
        Tree tree = new Tree();
        tree.addItem("Foo");
        tree.addItem("Bar");
        tree.addItem("Baz");
        tree.setParent("Baz", "Bar");
        tree.setHeight("200px");
        return tree;
    }

    @Override
    protected void handleContextClickEvent(TreeContextClickEvent event) {
        log("ContextClickEvent: " + event.getItemId());
    }
}
