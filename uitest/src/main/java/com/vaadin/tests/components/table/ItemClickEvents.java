package com.vaadin.tests.components.table;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Tree;

public class ItemClickEvents extends AbstractReindeerTestUI {

    private Tree tree;
    private Table table;
    private Log log;

    @Override
    @SuppressWarnings("unchecked")
    protected void setup(VaadinRequest request) {

        tree = new Tree();
        table = new Table();
        log = new Log(5);

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
                        "https://vaadin.com/vaadin-theme/images/vaadin-logo.png"));

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
            // Most likely won't trigger on Linux due to WMs using alt + mouse
            // button
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

    private static HorizontalLayout createHorizontalLayout(AbstractSelect c) {
        HorizontalLayout layout = new HorizontalLayout();
        CheckBox b = new CheckBox("immediate");
        b.setValue(c.isImmediate());
        b.addValueChangeListener(event -> c.setImmediate(event.getValue()));
        b.setImmediate(true);
        layout.addComponent(b);
        b = new CheckBox("selectable");
        if (c instanceof Table) {
            b.setValue(((Table) c).isSelectable());
            b.addValueChangeListener(
                    event -> ((Table) c).setSelectable(event.getValue()));
        } else if (c instanceof Tree) {
            b.setValue(((Tree) c).isSelectable());
            b.addValueChangeListener(
                    event -> ((Tree) c).setSelectable(event.getValue()));
        }
        b.setImmediate(true);
        layout.addComponent(b);
        b = new CheckBox("nullsel");
        b.setValue(c.isNullSelectionAllowed());
        b.addValueChangeListener(
                event -> c.setNullSelectionAllowed(event.getValue()));
        b.setImmediate(true);
        layout.addComponent(b);
        b = new CheckBox("multi");
        b.setValue(c.isMultiSelect());
        b.addValueChangeListener(event -> c.setMultiSelect(event.getValue()));
        b.setImmediate(true);
        layout.addComponent(b);
        return layout;
    }

    @Override
    protected String getTestDescription() {
        return "Click events should always come trough no matter how the table is configured.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5458;
    }

}
