package com.vaadin.tests.components.tree;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Tree;

@SuppressWarnings("serial")
public class TreeItemSelectionWithoutImmediate extends AbstractTestUIWithLog {

    protected static final String TREE_ID = "TreeId";

    protected static final String MENU_ITEM_TEMPLATE = "Menu Item %d";

    @Override
    protected void setup(VaadinRequest request) {
        Tree tree = new Tree("With ItemClickListener not Immediate");
        tree.setId(TREE_ID);
        tree.setImmediate(false);

        for (int i = 1; i <= 4; i++) {
            tree.addItem(String.format(MENU_ITEM_TEMPLATE, i));
        }

        tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                log("ItemClickEvent = " + event.getItemId());
            }
        });

        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "Test for ensuring that selection of tree items works correctly if immediate == false "
                + "and ItemClickListener is added to Tree";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14388;
    }
}
