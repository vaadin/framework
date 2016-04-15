package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class LabelEmbeddedClickThroughForTable extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "Clicking on a Label or Embedded inside a Table should select the row in the same way that clicking on a text selects the row.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2688;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        table.setHeight("500px");
        table.setSelectable(true);
        table.addContainerProperty("Column 1", String.class, "");
        table.addContainerProperty("Column 2", Component.class, "");
        table.addContainerProperty("Column 3", Component.class, "");
        table.addContainerProperty("Column 4", Component.class, "");

        Item item = table.addItem("Item 1 (row 1)");
        item.getItemProperty("Column 1").setValue("String A");
        item.getItemProperty("Column 2").setValue(new Label("Label A"));
        item.getItemProperty("Column 3").setValue(
                new Label("<b>Label A</b>", ContentMode.HTML));
        item.getItemProperty("Column 4").setValue(
                new Embedded("An embedded image", new ThemeResource(
                        "../runo/icons/32/ok.png")));

        item = table.addItem("Item 2 (row 2)");
        item.getItemProperty("Column 1").setValue("String B");
        item.getItemProperty("Column 2").setValue(new Label("Label B"));
        item.getItemProperty("Column 3")
                .setValue(
                        new Label(
                                "<a style=\"color: blue\" href=\"javascript:false\">Label B</a>",
                                ContentMode.HTML));
        item.getItemProperty("Column 4").setValue(
                new Embedded("", new ThemeResource(
                        "../runo/icons/32/cancel.png")));

        table.addItemClickListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                System.out.println("Clickevent on item " + event.getItemId()
                        + ", column: " + event.getPropertyId());

            }

        });
        addComponent(table);
    }

}
