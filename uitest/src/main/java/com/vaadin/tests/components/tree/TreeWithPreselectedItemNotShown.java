package com.vaadin.tests.components.tree;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

public class TreeWithPreselectedItemNotShown extends TestBase {

    @Override
    protected void setup() {
        Button open = new Button("Open modal window with tree",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        getMainWindow().addWindow(new SubwindowWithTree());
                    }
                });

        addComponent(open);
    }

    private class SubwindowWithTree extends Window {

        private SubwindowWithTree() {
            super("Tree here");

            String itemId1 = "Item 1";
            String itemId2 = "Item 2";

            Tree tree = new Tree();

            tree.addItem(itemId1);
            tree.addItem(itemId2);

            // todo error here
            tree.select(itemId1);

            addComponent(tree);
        }
    }

    @Override
    protected String getDescription() {
        return "IE8 doesn't display a tree if an item has been selected before the tree becomes visible";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6878;
    }

}
