package com.vaadin.tests.components.tree;

import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;

public class ExpandCollapseTree extends TestBase {

    private final Tree tree = new Tree();
    private final Label valueLbl = new Label("No selection");

    @Override
    protected void setup() {

        getLayout().setSpacing(true);

        tree.setContainerDataSource(createContainer());
        tree.setItemCaptionPropertyId("name");
        tree.setWidth("300px");
        tree.setImmediate(true);
        tree.setSelectable(true);
        tree.setMultiSelect(true);
        tree.expandItemsRecursively("Item 1");
        tree.expandItemsRecursively("Item 4");

        tree.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (tree.getValue() instanceof Set) {
                    @SuppressWarnings("unchecked")
                    // safe cast after instanceof check
                    Set<Object> itemIds = (Set<Object>) tree.getValue();
                    if (itemIds.size() == 0) {
                        valueLbl.setValue("No selection");
                    } else {
                        valueLbl.setValue(itemIds.toString());
                    }
                } else {
                    valueLbl.setValue(tree.getValue().toString());
                }
            }
        });

        addComponent(tree);

        valueLbl.setWidth("300px");
        valueLbl.setHeight("600px");
        addComponent(valueLbl);

    }

    @Override
    protected String getDescription() {
        return "Test collapsing and expansion of tree nodes";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5439;
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
