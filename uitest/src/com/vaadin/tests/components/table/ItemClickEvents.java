package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;

public class ItemClickEvents extends TestBase {

    Tree tree = new Tree();
    Table table = new Table();
    Log log = new Log(5);

    @Override
    public void setup() {
        log.setId("log");

        HorizontalLayout ol = createHorizontalLayout(tree);
        Button b = new Button("icon", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (tree.getItemIconPropertyId() == null) {
                    tree.setItemIconPropertyId("icon");
                } else {
                    tree.setItemIconPropertyId(null);
                }

            }
        });
        ol.addComponent(b);

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
            @Override
            public void itemClick(ItemClickEvent event) {
                logEvent(event, "tree");
            }
        });
        tree.setId("tree");

        HorizontalLayout ol2 = createHorizontalLayout(table);
        table.setWidth("150px");
        table.setImmediate(true);
        table.setSelectable(true);
        table.setNullSelectionAllowed(false);
        table.addContainerProperty("Column", String.class, "value");
        for (int i = 0; i < 10; i++) {
            Item item = table.addItem("Item " + i);
            item.getItemProperty("Column").setValue("Row " + i);

        }
        table.addListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                logEvent(event, "table");
            }
        });
        table.setId("table");

        addComponent(log);
        addComponent(new Button("Clear log", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log.clear();
            }
        }));
        addComponent(ol);
        addComponent(tree);
        addComponent(ol2);
        addComponent(table);
    }

    protected void logEvent(ItemClickEvent event, String targetComponent) {
        String type = event.getButtonName() + " " + "click";
        if (event.isDoubleClick()) {
            type = "doubleClick";
        }

        String modifiers = "";
        if (event.isAltKey()) {
            modifiers += "alt ";
        }
        if (event.isMetaKey()) {
            modifiers += "meta ";
        }
        if (event.isCtrlKey()) {
            modifiers += "ctrl ";
        }
        if (event.isShiftKey()) {
            modifiers += "shift ";
        }
        if (!"".equals(modifiers)) {
            modifiers = " (" + modifiers.trim() + ")";
        }

        log.log(type + " on " + targetComponent + "/" + event.getItemId()
                + modifiers);

    }

    private static HorizontalLayout createHorizontalLayout(Component c) {
        HorizontalLayout layout = new HorizontalLayout();
        CheckBox b = new CheckBox("immediate", new MethodProperty<Boolean>(c,
                "immediate"));
        b.setImmediate(true);
        layout.addComponent(b);
        b = new CheckBox("selectable", new MethodProperty<Boolean>(c,
                "selectable"));
        b.setImmediate(true);
        layout.addComponent(b);
        b = new CheckBox("nullsel", new MethodProperty<Boolean>(c,
                "nullSelectionAllowed"));
        b.setImmediate(true);
        layout.addComponent(b);
        b = new CheckBox("multi", new MethodProperty<Boolean>(c, "multiSelect"));
        b.setImmediate(true);
        layout.addComponent(b);
        return layout;
    }

    @Override
    protected String getDescription() {
        return "Click events should always come trough no matter how the table is configured.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5458;
    }

}
