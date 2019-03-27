package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;

public class TableReadOnlyTextField extends AbstractTestUI {

    @Override
    public String getDescription() {
        return "Selected row clicking ReadOnly Textfield should should work";
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(makeTable());
    }

    @Override
    protected Integer getTicketNumber() {
        return 11474;
    }

    private Table makeTable() {

        final Table table = new Table("Table");
        final Label clickLabel = new Label("Click?");
        final Label valueChangeLabel = new Label("Value?");

        table.addContainerProperty("Main", String.class, null);
        table.addContainerProperty("Details", com.vaadin.ui.TextField.class,
                null);
        for (int i = 0; i < 3; i++) {
            com.vaadin.ui.TextField test = new com.vaadin.ui.TextField(
                    "Testing " + i);
            test.setValue("Test " + i);
            test.setReadOnly(true);
            table.addItem(new Object[] { ("Value" + i), test }, i);
        }
        table.setImmediate(true);
        table.setSelectable(true);
        table.addItemClickListener(event -> {
            table.markAsDirty();
            clickLabel.setValue("Click " + event.getItemId());
        });

        table.addValueChangeListener(event -> valueChangeLabel
                .setValue("Value " + event.getProperty().getValue()));
        getLayout().addComponent(clickLabel);
        getLayout().addComponent(valueChangeLabel);
        return table;
    }
}
