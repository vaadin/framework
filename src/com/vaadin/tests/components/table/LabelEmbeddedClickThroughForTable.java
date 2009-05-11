package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class LabelEmbeddedClickThroughForTable extends TestBase {

    @Override
    protected String getDescription() {
        return "Clicking on a Label or Embedded inside a Table should select the row in the same way that clicking on a text selects the row.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2688;
    }

    @Override
    protected void setup() {
        Table table = new Table();
        table.setSelectable(true);
        table.addContainerProperty("Column 1", String.class, "");
        table.addContainerProperty("Column 2", Component.class, "");
        table.addContainerProperty("Column 3", Component.class, "");
        table.addContainerProperty("Column 4", Component.class, "");

        Item item = table.addItem("Item 1 (row 1)");
        item.getItemProperty("Column 1").setValue("String A");
        item.getItemProperty("Column 2").setValue(new Label("Label A"));
        item.getItemProperty("Column 3").setValue(
                new Label("<b>Label A</b>", Label.CONTENT_XHTML));
        item.getItemProperty("Column 4").setValue(
                new Embedded("An embedded image", new ThemeResource(
                        "icons/32/ok.png")));

        item = table.addItem("Item 2 (row 2)");
        item.getItemProperty("Column 1").setValue("String B");
        item.getItemProperty("Column 2").setValue(new Label("Label B"));
        item
                .getItemProperty("Column 3")
                .setValue(
                        new Label(
                                "<a href=\"http://www.itmill.com\" target=_blank>Label A</a>",
                                Label.CONTENT_XHTML));
        item.getItemProperty("Column 4").setValue(
                new Embedded("", new ThemeResource("icons/32/cancel.png")));

        table.addListener(new ItemClickListener() {

            public void itemClick(ItemClickEvent event) {
                getMainWindow().showNotification(
                        "Clickevent on item " + event.getItemId()
                                + ", column: " + event.getPropertyId());

            }

        });
        addComponent(table);
    }

}
