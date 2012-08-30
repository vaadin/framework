package com.vaadin.tests.components.table;

import com.vaadin.data.Property;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class TableCacheBuildEfficiency extends TestBase {

    @Override
    protected String getDescription() {
        return "On each add, row property values should be queried only once (one log row for first addition).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4299;
    }

    @Override
    protected void setup() {

        final CssLayout log = new CssLayout();
        log.setWidth("100%");

        final Table table = new Table() {
            @Override
            public Property<?> getContainerProperty(Object itemId,
                    Object propertyId) {
                log("Fetched container property \"" + propertyId
                        + "\" for item \"" + itemId + "\"");
                return super.getContainerProperty(itemId, propertyId);
            }

            private void log(String string) {
                log.addComponent(new Label(string));

            }
        };

        table.addContainerProperty("foo", String.class, "bar");

        Button b = new Button("Click to add row", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.addItem();

            }
        });

        getLayout().addComponent(table);
        getLayout().addComponent(b);
        getLayout().addComponent(log);

    }
}
