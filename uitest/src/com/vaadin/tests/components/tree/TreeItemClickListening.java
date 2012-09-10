package com.vaadin.tests.components.tree;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Tree;

public class TreeItemClickListening extends TestBase {

    private int clickCounter = 0;

    private Log log = new Log(5);

    @Override
    protected void setup() {

        Tree tree = new Tree();
        tree.setImmediate(true);

        tree.addContainerProperty("caption", String.class, "");
        for (int i = 1; i <= 10; i++) {
            String item = "Node " + i;
            tree.addItem(item);
            tree.getContainerProperty(item, "caption").setValue("Caption " + i);
            tree.setChildrenAllowed(item, false);
        }
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId("caption");

        tree.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                clickCounter++;
                switch (event.getButton()) {
                case LEFT:
                    log.log("Left Click");
                    break;
                case RIGHT:
                    log.log("Right Click");
                    break;
                case MIDDLE:
                    log.log("Middle Click");
                    break;
                }
            }
        });

        addComponent(tree);
        addComponent(log);
    }

    @Override
    protected String getDescription() {
        return "Item click event should be triggered from all mouse button clicks";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6845;
    }

}
