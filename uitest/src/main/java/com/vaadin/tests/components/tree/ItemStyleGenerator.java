package com.vaadin.tests.components.tree;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Tree;

public class ItemStyleGenerator extends TestBase {

    private Component styles;
    private String css = "<style type=\"text/css\">"
            + ".v-tree-node-red {color: red;}"
            + ".v-tree-node-green {color: green;}"
            + ".v-tree-node-caption-blue {color:blue;}" //
            + "</style>";

    @Override
    protected String getDescription() {
        return "Item Style generator can be used to style items.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3070;
    }

    @Override
    protected void setup() {
        styles = new CustomLayout(css);
        addComponent(styles);

        Tree tree = new Tree();

        tree.setItemStyleGenerator(new Tree.ItemStyleGenerator() {
            @Override
            public String getStyle(Tree source, Object itemId) {
                // simple return itemId as css style name
                return itemId.toString();
            }
        });

        tree.addItem("red");
        tree.setChildrenAllowed("red", false);
        tree.addItem("green");
        tree.addItem("green children");
        tree.setParent("green children", "green");
        tree.addItem("blue");
        tree.addItem("non-blue-childnode");
        tree.setParent("non-blue-childnode", "blue");

        addComponent(tree);
    }
}
