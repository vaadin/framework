package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableMatchesMouseDownMouseUpElement extends AbstractTestUI {

    static final String CLEAR_BUTTON_ID = "clear-button-id";

    @Override
    protected String getTestDescription() {
        return "Both mouse down and mouse up should be done on same cell to be considered as a click.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14347;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();
        table.setHeight("500px");
        table.setSelectable(true);
        table.setNullSelectionAllowed(true);
        table.addContainerProperty("Column 1", String.class, "");
        table.addContainerProperty("Column 2", Component.class, "");
        table.addContainerProperty("Column 3", Component.class, "");
        table.addContainerProperty("Column 4", Component.class, "");

        Item item = table.addItem("Item 1 (row 1)");
        item.getItemProperty("Column 1").setValue("String A");
        item.getItemProperty("Column 2").setValue(new Label("Label A"));
        item.getItemProperty("Column 3").setValue(
                new Label("<b>Label A</b>", ContentMode.HTML));
        VerticalLayout l = new VerticalLayout();
        l.setId("row-1");
        l.setHeight(100, Unit.PIXELS);
        item.getItemProperty("Column 4").setValue(l);

        item = table.addItem("Item 2 (row 2)");
        item.getItemProperty("Column 1").setValue("String B");
        item.getItemProperty("Column 2").setValue(new Label("Label B"));
        item.getItemProperty("Column 3")
                .setValue(
                        new Label(
                                "<a style=\"color: blue\" href=\"javascript:false\">Label B</a>",
                                ContentMode.HTML));
        l = new VerticalLayout();
        l.setId("row-2");
        l.setSizeFull();
        item.getItemProperty("Column 4").setValue(l);

        Button clear = new Button("Clear");
        clear.setId(CLEAR_BUTTON_ID);
        clear.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.setValue(null);
            }
        });
        addComponent(table);
        addComponent(clear);
    }
}
