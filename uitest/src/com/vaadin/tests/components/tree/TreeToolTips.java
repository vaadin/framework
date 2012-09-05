package com.vaadin.tests.components.tree;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;

public class TreeToolTips extends TestBase {

    @Override
    protected void setup() {
        final Tree tree = new Tree(null, createContainer());
        tree.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {
            @Override
            public String generateDescription(Component source, Object itemId,
                    Object propertyId) {
                return "This is a tooltip for item id '" + itemId + "'";
            }
        });

        for (Object rootItems : tree.rootItemIds()) {
            tree.expandItemsRecursively(rootItems);
        }

        addComponent(tree);
    }

    @Override
    protected String getDescription() {
        return "Tree items should have tooltips";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6637;
    }

    private HierarchicalContainer createContainer() {
        HierarchicalContainer cont = new HierarchicalContainer();
        cont.addContainerProperty("name", String.class, "");

        for (int i = 0; i < 20; i++) {
            Item item = cont.addItem("Item " + i);
            item.getItemProperty("name").setValue("Item " + i);
            cont.setChildrenAllowed("Item " + i, false);

            if (i == 1 || i == 4) {
                cont.setChildrenAllowed("Item " + i, true);
            }

            // Add three items to item 1
            if (i > 1 && i < 4) {
                cont.setParent("Item " + i, "Item 1");
            }

            // Add 5 items to item 4
            if (i > 4 && i < 10) {
                cont.setChildrenAllowed("Item " + i, true);

                if (i == 7) {
                    item = cont.addItem("Item 71");
                    item.getItemProperty("name").setValue("Item 71");
                    cont.setParent("Item 71", "Item " + i);
                    cont.setChildrenAllowed("Item 71", false);

                    item = cont.addItem("Item 72");
                    item.getItemProperty("name").setValue("Item 72");
                    cont.setParent("Item 72", "Item " + i);
                    cont.setChildrenAllowed("Item 72", true);

                    item = cont.addItem("Item 73");
                    item.getItemProperty("name").setValue("Item 73");
                    cont.setParent("Item 73", "Item 72");
                    cont.setChildrenAllowed("Item 73", true);

                    item = cont.addItem("Item 74");
                    item.getItemProperty("name").setValue("Item 74");
                    cont.setParent("Item 74", "Item " + i);
                    cont.setChildrenAllowed("Item 74", true);
                }

                cont.setParent("Item " + i, "Item " + (i - 1));

            }
        }

        return cont;
    }
}
