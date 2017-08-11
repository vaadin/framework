package com.vaadin.tests.components.tree;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Tree;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeOnBrowserResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Tree<String> tree = new Tree<>();
        tree.setItems(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                        + "Ut a ante congue, dictum elit vitae, mollis justo. "
                        + "Nunc porttitor, eros et eleifend accumsan, quam dolor venenatis tortor, "
                        + "in euismod lorem massa quis nisi. In pretium viverra tincidunt. ");

        tree.setSizeFull();
        addComponent(tree);
    }

}
