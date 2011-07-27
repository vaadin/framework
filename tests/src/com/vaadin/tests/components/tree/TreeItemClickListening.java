package com.vaadin.tests.components.tree;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;

public class TreeItemClickListening extends TestBase {

    private int clickCounter = 0;

    @Override
    protected void setup() {

        final Label output = new Label("", Label.CONTENT_PREFORMATTED);

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
            public void itemClick(ItemClickEvent event) {
                clickCounter++;
                switch (event.getButton()) {
                case ItemClickEvent.BUTTON_LEFT:
                    output.setValue(output.getValue().toString() + clickCounter
                            + ": Left Click\n");
                    break;
                case ItemClickEvent.BUTTON_RIGHT:
                    output.setValue(output.getValue().toString() + clickCounter
                            + ": Right Click\n");
                    break;
                }
            }
        });

        addComponent(tree);
        addComponent(output);
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
