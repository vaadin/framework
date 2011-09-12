package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;

public class TableColumnResizeContentsWidth extends TestBase {

    private static final String COL1 = "COL1";

    @Override
    protected void setup() {
        final Table table = new Table();
        table.addGeneratedColumn(COL1, new ColumnGenerator() {
            public Object generateCell(Table source, Object itemId,
                    Object columnId) {
                TextField textField = new TextField();
                textField.setWidth("100%");
                return textField;
            }
        });

        table.addItem();

        table.setWidth("200px");
        table.setColumnWidth(COL1, 100);

        addComponent(table);
        addComponent(new Button("Increase width", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                table.setColumnWidth(COL1, table.getColumnWidth(COL1) + 20);
                table.requestRepaint();
            }
        }));
        addComponent(new Button("Decrease width", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                table.setColumnWidth(COL1, table.getColumnWidth(COL1) - 40);
                table.requestRepaint();
            }
        }));
    }

    @Override
    protected String getDescription() {
        return "When a column is resized, it's contents should update to match the new size";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7393);
    }

}
