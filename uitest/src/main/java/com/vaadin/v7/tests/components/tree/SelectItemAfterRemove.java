package com.vaadin.v7.tests.components.tree;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.ui.Tree;

public class SelectItemAfterRemove extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Tree tree = new Tree();

        tree.setImmediate(true);
        tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {

                tree.removeItem(tree.getValue());
                tree.select(event.getItemId());
            }
        });

        tree.addItem("first");
        tree.addItem("second");
        tree.addItem("third");

        tree.select("first");

        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking on an item should select the clicked item and remove "
                + "the previously selected item.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15181;
    }
}
