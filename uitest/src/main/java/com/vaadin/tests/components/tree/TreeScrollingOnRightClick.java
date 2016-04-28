package com.vaadin.tests.components.tree;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Tree;

/**
 * Test for #12618: Trying to select item with right click in Tree causes focus
 * issues.
 */
@SuppressWarnings("serial")
public class TreeScrollingOnRightClick extends AbstractTestUI {

    public static final String TREE_ID = "my-tree";

    @Override
    protected void setup(VaadinRequest request) {
        final Tree tree = new Tree();
        tree.setId(TREE_ID);
        tree.setSizeUndefined();

        // Add item click listener for right click selection
        tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
                    tree.select(event.getItemId());
                }
            }
        });

        // Add some items
        for (int i = 0; i < 200; i++) {
            tree.addItem(String.format("Node %s", i));
        }

        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "Right clicking on items should not scroll Tree.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12618;
    }

}
