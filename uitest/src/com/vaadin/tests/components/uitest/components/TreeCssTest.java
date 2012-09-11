package com.vaadin.tests.components.uitest.components;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Tree;

public class TreeCssTest {
    private int debugIdCounter = 0;

    public TreeCssTest(TestSampler parent) {
        // Actions for the context menu
        final Action ACTION_ADD = new Action("Add child item");
        final Action ACTION_DELETE = new Action("Delete");
        final Action[] ACTIONS = new Action[] { ACTION_ADD, ACTION_DELETE };

        final Tree tree = new Tree();
        tree.setId("tree" + debugIdCounter++);

        HierarchicalContainer hc = createHierarchicalContainer();

        tree.setContainerDataSource(hc);

        tree.addActionHandler(new Action.Handler() {

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                // We don't care about functionality, we just want the UI for
                // testing..

            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                // TODO Auto-generated method stub
                return ACTIONS;
            }
        });

        // Expand whole tree
        for (Object id : tree.rootItemIds()) {
            tree.expandItemsRecursively(id);
        }

        parent.addComponent(tree);
    }

    private HierarchicalContainer createHierarchicalContainer() {
        String[] itemNames = new String[] { "Foo", "Baar" };

        HierarchicalContainer hc = new HierarchicalContainer();
        hc.addContainerProperty("NAME", String.class, null);

        for (String parentId : itemNames) {
            Item parent = hc.addItem(parentId);
            parent.getItemProperty("NAME").setValue(parentId);
            hc.setChildrenAllowed(parent, true);
            for (int i = 0; i < 5; i++) {
                String childId = parentId + i;
                Item child = hc.addItem(childId);
                child.getItemProperty("NAME").setValue(childId);
                if (!hc.setParent(childId, parentId)) {
                    System.out.println("Unable to set parent \"" + parentId
                            + "\" for child with id: \"" + childId + "\"");
                }
            }
        }
        return hc;
    }

}
