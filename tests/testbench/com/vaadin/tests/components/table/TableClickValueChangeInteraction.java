package com.vaadin.tests.components.table;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TableClickValueChangeInteraction extends AbstractTestCase {

    final Window mainWindow = new Window();

    @Override
    public void init() {
        setMainWindow(mainWindow);

        GridLayout layout = new GridLayout(4, 4);
        layout.setSpacing(true);
        layout.setMargin(true);
        mainWindow.setContent(layout);

        for (int i = 0; i < 16; ++i) {
            mainWindow.addComponent(makeTable((i & 8) > 0, (i & 4) > 0,
                    (i & 2) > 0, (i & 1) > 0));
        }

    }

    @Override
    protected String getDescription() {
        return "Table selection breaks if ItemClickListener requests repaint";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7127;
    }

    private Component makeTable(boolean immediate, boolean selectable,
            boolean listenClicks, boolean listenValueChanges) {

        final Table table = new Table((immediate ? "I" : "i")
                + (selectable ? "S" : "s") + (listenClicks ? "C" : "c")
                + (listenValueChanges ? "V" : "v"));
        final Label clickLabel = new Label("Click?");
        final Label valueChangeLabel = new Label("Value?");

        table.addContainerProperty("foo", String.class, null);
        for (int i = 0; i < 3; i++) {
            table.addItem("item" + i).getItemProperty("foo")
                    .setValue("An item " + i);
        }
        table.setImmediate(immediate);
        table.setSelectable(selectable);
        table.setWidth("100px");
        table.setHeight("100px");
        if (listenClicks) {
            table.addListener(new ItemClickListener() {
                public void itemClick(ItemClickEvent event) {
                    table.requestRepaint();
                    clickLabel.setValue("Click " + event.getItemId());
                }
            });
        }
        if (listenValueChanges) {
            table.addListener(new ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    valueChangeLabel.setValue("Value " + event.getProperty());
                }
            });
        }

        Layout result = new VerticalLayout();
        result.addComponent(table);
        result.addComponent(clickLabel);
        result.addComponent(valueChangeLabel);
        return result;
    }
}