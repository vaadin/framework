package com.vaadin.tests.components.tree;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.TreeData;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeResize extends AbstractTestUI {

    private static final int WIDTH_WIDE = 600;
    private static final int WIDTH_NARROW = 300;

    @Override
    protected void setup(VaadinRequest request) {
        Tree<String> tree = new Tree<>();

        tree.setWidth(100, Unit.PERCENTAGE);

        TreeData<String> data = new TreeData<>();
        data.addItem(null, "Foo");
        data.addItem("Foo", "Extra long text content that can be"
                + " wider than its container component");
        data.addItem(null, "Bar");
        data.addItem(null, "Baz");
        tree.setTreeData(data);

        // Expand the wide one initially.
        tree.expand("Foo");

        Panel wrapper = new Panel(tree);
        wrapper.setWidth(WIDTH_WIDE, Unit.PIXELS);
        addComponent(wrapper);

        addComponent(new Button("Change tree width", e -> {
            wrapper.setWidth(wrapper.getWidth() == WIDTH_WIDE ? WIDTH_NARROW
                    : WIDTH_WIDE, Unit.PIXELS);
        }));
    }

}
