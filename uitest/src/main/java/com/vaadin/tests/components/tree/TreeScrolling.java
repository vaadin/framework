package com.vaadin.tests.components.tree;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class TreeScrolling extends AbstractTestCase {

    @Override
    public void init() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        LegacyWindow w = new LegacyWindow("", layout);
        setMainWindow(w);

        TextField filler1 = new TextField();
        RichTextArea filler2 = new RichTextArea();
        Tree tree = new Tree();
        for (int i = 0; i < 20; i++) {
            String parentId = "Item " + i;
            // Item parentItem =
            tree.addItem(parentId);
            for (int j = 0; j < 20; j++) {
                String subId = "Item " + i + " - " + j;
                // Item subItem =
                tree.addItem(subId);
                tree.setParent(subId, parentId);
            }

        }

        for (Object id : tree.rootItemIds()) {
            tree.expandItemsRecursively(id);
        }

        layout.addComponent(filler1);
        layout.addComponent(filler2);
        layout.addComponent(tree);
    }

    @Override
    protected String getDescription() {
        return "Tests what happens when a tree is partly out of view when an item is selected";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5400;
    }

}
