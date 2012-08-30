package com.vaadin.tests.components.tree;

import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.AlwaysFailValidator;
import com.vaadin.ui.Tree;

public class TreeKeyboardNavigationValidators extends TestBase {

    @Override
    protected void setup() {
        addComponent(getTree());
    }

    private Tree getTree() {
        Tree tree = new Tree();
        tree.setSizeFull();
        tree.setContainerDataSource(generateHierarchicalContainer());
        tree.setImmediate(true);
        tree.addValidator(new AlwaysFailValidator("failed"));
        return tree;
    }

    private Container generateHierarchicalContainer() {
        HierarchicalContainer cont = new HierarchicalContainer();
        for (int i = 1; i < 6; i++) {
            cont.addItem(i);
            for (int j = 1; j < 3; j++) {
                String id = i + " -> " + j;
                cont.addItem(id);
                cont.setChildrenAllowed(id, false);
                cont.setParent(id, i);
            }
        }
        return cont;
    }

    @Override
    protected String getDescription() {
        return "Keyboard navigation should still work in a tree with validators.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7057;
    }

}
