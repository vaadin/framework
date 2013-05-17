package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;

public class TreeTableExtraScrollbarWithChildren extends TestBase {

    @Override
    protected String getDescription() {
        return "Arrow calculation should not cause a horizontal scrollbar"
                + " if there is enough space for the final contents.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10513;
    }

    @Override
    protected void setup() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("300px");
        layout.setHeight("300px");

        final TreeTable table = new TreeTable();
        table.setSizeFull();

        table.addGeneratedColumn("parameterId", new HierarchyColumnGenerator());
        table.addGeneratedColumn("wordingTextId", new TypeColumnGenerator());
        table.addGeneratedColumn("parameterTypeId", new TypeColumnGenerator());

        table.setColumnWidth("parameterId", 26);

        table.addItem(new TestObject("name 1", "value 1"));
        table.addItem(new TestObject("name 2", "value 2"));
        table.addItem(new TestObject("name 3", "value 3"));
        table.addItem(new TestObject("name 4", "value 4"));
        table.addItem(new TestObject("name 5", "value 5"));
        final TestObject parent = new TestObject("name 6", "value 6");
        table.addItem(parent);
        table.setFooterVisible(true);
        for (int i = 1; i <= 10; ++i) {
            TestObject child = new TestObject("name 6-" + i, "value 6-" + i);
            table.addItem(child);
            table.setParent(child, parent);
        }
        table.setVisibleColumns(new Object[] { "wordingTextId", "parameterId",
                "parameterTypeId" });
        table.setColumnHeaders(new String[] { "", "", "" });
        table.setHierarchyColumn("parameterId");

        layout.addComponent(table);

        Button button = new Button("Toggle");
        button.setId("button");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.setCollapsed(parent, !table.isCollapsed(parent));
                Notification.show("collapsed: " + table.isCollapsed(parent));
            }
        });

        addComponent(layout);
        addComponent(button);
    }

    private class HierarchyColumnGenerator implements Table.ColumnGenerator {
        @Override
        public Object generateCell(Table table, Object itemId, Object columnId) {
            Label label = new Label("this should be mostly hidden");
            label.setSizeUndefined();
            return label;
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
