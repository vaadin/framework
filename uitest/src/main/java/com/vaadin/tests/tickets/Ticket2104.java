package com.vaadin.tests.tickets;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;

public class Ticket2104 extends LegacyApplication {

    private static final Label info = new Label(
            "Click event should _always_ come trough. Switching features on/off should immediatly affect the tree (verify w/ debug window)",
            ContentMode.RAW);

    Tree tree = new Tree();
    Table table = new Table();

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow();
        setMainWindow(main);

        main.addComponent(info);

        HorizontalLayout ol = new HorizontalLayout();
        main.addComponent(ol);
        CheckBox cb = new CheckBox("immediate");
        cb.setValue(tree.isImmediate());
        cb.addValueChangeListener(event -> tree.setImmediate(event.getValue()));
        cb.setImmediate(true);
        ol.addComponent(cb);
        cb = new CheckBox("selectable");
        cb.setValue(tree.isSelectable());
        cb.addValueChangeListener(
                event -> tree.setSelectable(event.getValue()));
        cb.setImmediate(true);
        ol.addComponent(cb);
        cb = new CheckBox("nullsel");
        cb.setValue(tree.isNullSelectionAllowed());
        cb.addValueChangeListener(
                event -> tree.setNullSelectionAllowed(event.getValue()));
        cb.setImmediate(true);
        ol.addComponent(cb);
        cb = new CheckBox("multi");
        cb.setValue(tree.isMultiSelect());
        cb.addValueChangeListener(
                event -> tree.setMultiSelect(event.getValue()));
        cb.setImmediate(true);
        ol.addComponent(cb);
        cb = new CheckBox("icon");
        cb.addValueChangeListener(event -> {
            if (tree.getItemIconPropertyId() == null) {
                tree.setItemIconPropertyId("icon");
            } else {
                tree.setItemIconPropertyId(null);
            }
        });
        cb.setImmediate(true);
        ol.addComponent(cb);

        main.addComponent(tree);
        tree.setImmediate(true);
        tree.setNullSelectionAllowed(false);
        tree.addItem("UI 1");
        tree.addItem("1. Child 1");
        tree.setParent("1. Child 1", "UI 1");
        tree.addItem("1. Child 2");
        tree.setParent("1. Child 2", "UI 1");
        tree.addItem("UI 2");
        tree.addItem("2. Child 1");
        tree.setParent("2. Child 1", "UI 2");
        tree.addItem("2. Child 2");
        tree.setParent("2. Child 2", "UI 2");
        tree.addContainerProperty("icon", ExternalResource.class,
                new ExternalResource(
                        "http://www.itmill.com/res/images/itmill_logo.gif"));

        tree.addListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                getMainWindow().addComponent(
                        new Label(event.toString() + " // " + event.getItemId()
                                + "//" + event.getSource()));

            }
        });

        ol = new HorizontalLayout();
        main.addComponent(ol);
        cb = new CheckBox("immediate");
        cb.setValue(table.isImmediate());
        cb.addValueChangeListener(event -> table.setImmediate(event.getValue()));
        cb.setImmediate(true);
        ol.addComponent(cb);
        cb = new CheckBox("selectable");
        cb.setValue(table.isSelectable());
        cb.addValueChangeListener(
                event -> table.setSelectable(event.getValue()));
        cb.setImmediate(true);
        ol.addComponent(cb);
        cb = new CheckBox("nullsel");
        cb.setValue(table.isNullSelectionAllowed());
        cb.addValueChangeListener(
                event -> table.setNullSelectionAllowed(event.getValue()));
        cb.setImmediate(true);
        ol.addComponent(cb);
        cb = new CheckBox("multi");
        cb.setValue(table.isMultiSelect());
        cb.addValueChangeListener(
                event -> table.setMultiSelect(event.getValue()));
        cb.setImmediate(true);
        ol.addComponent(cb);
        main.addComponent(table);
        table.setWidth("150px");
        table.setImmediate(true);
        table.setSelectable(true);
        table.setNullSelectionAllowed(false);
        for (int i = 0; i < 10; i++) {
            table.addItem("Item " + i);
        }
        table.addListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                getMainWindow().addComponent(
                        new Label(event.toString() + " // " + event.getItemId()
                                + "//" + event.getSource()));

            }
        });
        table.addContainerProperty("Column", String.class, "value");
    }

}
