package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

public class TableWithChildComponents extends TestBase implements ClickListener {

    private static final String COL2 = "Column 2 - generated";
    private static final String COL1 = "Column 1 - components";
    private Log log = new Log(10);

    @Override
    protected void setup() {
        Table table = new Table();
        table.setWidth("500px");
        table.setPageLength(10);
        table.addContainerProperty(COL1, Component.class, null);
        table.addContainerProperty(COL2, Component.class, null);

        table.addGeneratedColumn(COL2, new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Button(
                        "Item id: " + itemId + " column: " + columnId,
                        TableWithChildComponents.this);
            }
        });

        for (int i = 0; i < 100; i++) {
            Item item = table.addItem("Row " + i);
            item.getItemProperty(COL1).setValue(
                    new NativeButton("Row " + i + " native", this));
        }

        addComponent(table);
        addComponent(log);

    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        log.log("Click on " + event.getButton().getCaption());

    }

}
