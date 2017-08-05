package com.vaadin.tests.components.tree;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.TreeData;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeWideContentResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Tree<String> tree = new Tree<>();

        tree.setWidth(100, Unit.PERCENTAGE);
        tree.setHeight("100px");

        TreeData<String> data = new TreeData<>();
        data.addItem(null, "Foo");
        data.addItem("Foo", "Extra long text content that should be wider"
                + " than the allocated width of the Tree.");
        data.addItem(null, "Bar");
        data.addItem(null, "Baz");
        tree.setTreeData(data);

        // Expand the wide one initially.
        tree.expand("Foo");

        addComponent(tree);
//        addComponent(new Button("Toggle auto recalc", e -> tree
//                .setAutoRecalculateWidth(!tree.isAutoRecalculateWidth())));

        addComponent(new Button("Toggle visible", e -> tree.setVisible(!tree.isVisible())));
    }

}
