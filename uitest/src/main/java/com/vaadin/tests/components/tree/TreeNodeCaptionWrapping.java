package com.vaadin.tests.components.tree;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Tree;

public class TreeNodeCaptionWrapping extends TestBase {

    @Override
    protected String getDescription() {
        return "The text should not wrap to the following line but instead be cut off when there is too little horizontal space.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3098;
    }

    @Override
    protected void setup() {
        setTheme("runo");
        Tree tree = new Tree();
        tree.setWidth("100px");

        tree.addItem("1");
        tree.setItemIcon("1", new ThemeResource("../runo/icons/16/ok.png"));

        String mainItem = "A very long item that should not wrap";
        String subItem = "Subitem - also long";

        tree.addItem(mainItem);
        tree.setItemIcon(mainItem, new ThemeResource(
                "../runo/icons/16/error.png"));

        tree.addItem(subItem);
        tree.setParent(subItem, mainItem);

        tree.expandItem("1");
        tree.expandItem(mainItem);
        tree.expandItem(subItem);

        addComponent(tree);
    }
}
