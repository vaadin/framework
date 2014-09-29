package com.vaadin.tests.components.tree;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Tree;

public class TreeItemDoubleClick extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Tree tree = new Tree("Immediate With ItemClickListener");
        tree.setImmediate(true);
        tree.setNullSelectionAllowed(false);

        for (int i = 1; i < 6; i++) {
            tree.addItem("Tree Item " + i);
        }

        ItemClickEvent.ItemClickListener listener = new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    log.log("Double Click " + event.getItemId());
                }
            }
        };

        tree.addItemClickListener(listener);

        addComponent(tree);

        Button button = new Button("Change immediate flag");
        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                // this wouldn't work if tree had a value change listener
                tree.setImmediate(!tree.isImmediate());
                log.log("tree.isImmediate() is now " + tree.isImmediate());
            }

        });

        addComponent(button);

    }

    @Override
    protected String getTestDescription() {
        return "Tests that double click is fired";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14745;
    }

}
