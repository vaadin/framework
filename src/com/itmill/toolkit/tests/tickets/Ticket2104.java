package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.event.ItemClickEvent;
import com.itmill.toolkit.event.ItemClickEvent.ItemClickListener;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2104 extends Application {

    private static final Label info = new Label(
            "Click event should _always_ come trough. Switching features on/off should immediatly affect the tree (verify w/ debug window)",
            Label.CONTENT_RAW);

    Tree tree = new Tree();
    Table table = new Table();

    @Override
    public void init() {
        Window main = new Window();
        setMainWindow(main);

        main.addComponent(info);

        OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        main.addComponent(ol);
        Button b = new Button("immediate",
                new MethodProperty(tree, "immediate"));
        b.setImmediate(true);
        ol.addComponent(b);
        b = new Button("selectable", new MethodProperty(tree, "selectable"));
        b.setImmediate(true);
        ol.addComponent(b);
        b = new Button("nullsel", new MethodProperty(tree,
                "nullSelectionAllowed"));
        b.setImmediate(true);
        ol.addComponent(b);
        b = new Button("multi", new MethodProperty(tree, "multiSelect"));
        b.setImmediate(true);
        ol.addComponent(b);
        b = new Button("icon", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (tree.getItemIconPropertyId() == null) {
                    tree.setItemIconPropertyId("icon");
                } else {
                    tree.setItemIconPropertyId(null);
                }

            }
        });
        ol.addComponent(b);

        main.addComponent(tree);
        tree.setImmediate(true);
        tree.setNullSelectionAllowed(false);
        tree.addItem("Root 1");
        tree.addItem("1. Child 1");
        tree.setParent("1. Child 1", "Root 1");
        tree.addItem("1. Child 2");
        tree.setParent("1. Child 2", "Root 1");
        tree.addItem("Root 2");
        tree.addItem("2. Child 1");
        tree.setParent("2. Child 1", "Root 2");
        tree.addItem("2. Child 2");
        tree.setParent("2. Child 2", "Root 2");
        tree.addContainerProperty("icon", ExternalResource.class,
                new ExternalResource(
                        "http://www.itmill.com/res/images/itmill_logo.gif"));

        tree.addListener(new ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                getMainWindow().addComponent(
                        new Label(event.toString() + " // " + event.getItemId()
                                + "//" + event.getSource()));

            }
        });

        ol = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        main.addComponent(ol);
        b = new Button("immediate", new MethodProperty(table, "immediate"));
        b.setImmediate(true);
        ol.addComponent(b);
        b = new Button("selectable", new MethodProperty(table, "selectable"));
        b.setImmediate(true);
        ol.addComponent(b);
        b = new Button("nullsel", new MethodProperty(table,
                "nullSelectionAllowed"));
        b.setImmediate(true);
        ol.addComponent(b);
        b = new Button("multi", new MethodProperty(table, "multiSelect"));
        b.setImmediate(true);
        ol.addComponent(b);
        main.addComponent(table);
        table.setWidth("150px");
        table.setImmediate(true);
        table.setSelectable(true);
        table.setNullSelectionAllowed(false);
        for (int i = 0; i < 10; i++) {
            table.addItem("Item " + i);
        }
        table.addListener(new ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                getMainWindow().addComponent(
                        new Label(event.toString() + " // " + event.getItemId()
                                + "//" + event.getSource()));

            }
        });
        table.addContainerProperty("Column", String.class, "value");
    }

}
