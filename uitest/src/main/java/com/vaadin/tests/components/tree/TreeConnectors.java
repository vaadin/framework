package com.vaadin.tests.components.tree;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class TreeConnectors extends TestBase {

    private Tree tree = createTree();

    @Override
    protected void setup() {
        CheckBox cb = new CheckBox("Connectors");
        cb.setValue(false);
        cb.setImmediate(true);
        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if ((Boolean) event.getProperty().getValue()) {
                    tree.addStyleName(BaseTheme.TREE_CONNECTORS);
                } else {
                    tree.removeStyleName(BaseTheme.TREE_CONNECTORS);
                }
            }
        });
        addComponent(cb);
        addComponent(tree);
    }

    private Tree createTree() {
        final Tree tree = new Tree(null, createContainer());
        tree.setWidth("300px");
        for (Object rootItems : tree.rootItemIds()) {
            tree.expandItemsRecursively(rootItems);
        }
        tree.setChildrenAllowed("Item 73", false);

        tree.setDragMode(TreeDragMode.NODE);
        tree.setDropHandler(new DropHandler() {

            @Override
            public void drop(DragAndDropEvent event) {
                // TODO Auto-generated method stub

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

        });

        tree.setItemIcon("Item 1", new ThemeResource(
                "../runo/icons/32/folder.png"));
        tree.setItemIcon("Item 3", new ThemeResource(
                "../runo/icons/32/document.png"));
        tree.setItemIcon("Item 13", new ThemeResource(
                "../runo/icons/64/user.png"));
        tree.setItemIcon("Item 72", new ThemeResource(
                "../runo/icons/64/users.png"));
        tree.setItemIcon("Item 17", new ThemeResource(
                "../runo/icons/16/document-pdf.png"));

        return tree;
    }

    @Override
    protected String getDescription() {
        return "The second tree's nodes should be connected with lines indicating the hierarchy. The first tree should be without connectors.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6745;
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
