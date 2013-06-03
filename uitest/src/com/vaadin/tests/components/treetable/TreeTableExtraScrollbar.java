package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;

public class TreeTableExtraScrollbar extends TestBase {

    @Override
    protected String getDescription() {
        return "Vertical scrollbar should not cause a horizontal scrollbar"
                + " if there is more excess space than vertical scrollbar needs.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10513;
    }

    @Override
    protected void setup() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("310px");
        layout.setHeight("300px");

        final Table table = new TreeTable();
        table.setSizeFull();

        table.addGeneratedColumn("parameterId", new EmptyColumnGenerator());
        table.addGeneratedColumn("wordingTextId", new TypeColumnGenerator());
        table.addGeneratedColumn("parameterTypeId", new TypeColumnGenerator());

        table.setColumnWidth("parameterId", 26);

        table.addItem(new TestObject("name 1", "value 1"));
        table.addItem(new TestObject("name 2", "value 2"));
        table.addItem(new TestObject("name 3", "value 3"));
        table.addItem(new TestObject("name 4", "value 4"));
        table.addItem(new TestObject("name 5", "value 5"));

        layout.addComponent(table);

        Button button = new Button("Add");
        button.setId("button");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.addItem(new TestObject("name 6-1", "value 6-1"));
                table.addItem(new TestObject("name 6-2", "value 6-2"));
                table.addItem(new TestObject("name 6-3", "value 6-3"));
                table.addItem(new TestObject("name 6-4", "value 6-4"));
                table.addItem(new TestObject("name 6-5", "value 6-5"));
                table.addItem(new TestObject("name 6-6", "value 6-6"));
                table.addItem(new TestObject("name 6-7", "value 6-7"));
                table.addItem(new TestObject("name 6-8", "value 6-8"));
                table.addItem(new TestObject("name 6-9", "value 6-9"));
                table.addItem(new TestObject("name 6-9", "value 6-10"));
            }
        });

        addComponent(layout);
        addComponent(button);
    }

    private class EmptyColumnGenerator implements Table.ColumnGenerator {
        @Override
        public Object generateCell(Table table, Object itemId, Object columnId) {
            return null;
        }
    }

    private class TypeColumnGenerator implements Table.ColumnGenerator {
        @Override
        public Object generateCell(Table table, Object itemId, Object columnId) {
            if (itemId instanceof TestObject) {
                return new Label(((TestObject) itemId).getValue());
            }
            return null;
        }
    }

}

class TestObject {

    private String name;
    private String value;

    public TestObject(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
