package com.vaadin.tests.components.uitest.components;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.TreeTable;

public class TreeTableCssTest {
    private int debugIdCounter = 0;

    public TreeTableCssTest(TestSampler parent) {
        TreeTable treeTable = new TreeTable();
        treeTable.setId("treetable" + debugIdCounter++);
        treeTable.setWidth("100%");
        parent.addComponent(treeTable);

        HierarchicalContainer hc = createHierarchicalContainer();

        treeTable.setContainerDataSource(hc);

        for (Object itemId : treeTable.getItemIds()) {
            treeTable.setCollapsed(itemId, false);
        }
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
