package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

public class TableRowHeight extends TestBase {

    @Override
    protected String getDescription() {
        return "This test case contains 5 tables in various configurations. All tables have a pageLength of "
                + PAGELENGTH
                + " and thus should show as many rows without any scrollbars (height is undefined for all tables).";

    }

    @Override
    protected Integer getTicketNumber() {
        return 2691;
    }

    private static final int PAGELENGTH = 2;

    @Override
    public void setup() {
        Table table1 = initTable(PAGELENGTH, false, false, false);
        addComponent(new Label("Plain table"));
        addComponent(table1);

        Table table2 = initTable(PAGELENGTH, true, false, false);
        addComponent(new Label("Table with label component in generated column"));
        addComponent(table2);

        Table table3 = initTable(PAGELENGTH, false, true, false);
        addComponent(new Label(
                "Table with layout component in generated column"));
        addComponent(table3);

        Table table4 = initTable(PAGELENGTH, true, true, false);
        addComponent(new Label(
                "Table with both label and layout component in generated column"));
        addComponent(table4);

        Table table5 = initTable(PAGELENGTH, false, false, true);
        addComponent(new Label("Table with label horizontal scrollbar"));
        addComponent(table5);

    }

    private Table initTable(int pageLength, boolean addLabelColGen,
            boolean addLayoutColGen, boolean fixedColWidths) {
        Table table = new Table();
        table.setWidth("100%");
        table.setPageLength(pageLength);

        IndexedContainer idx = new IndexedContainer();
        idx.addContainerProperty("firstname", String.class, null);
        idx.addContainerProperty("lastname", String.class, null);
        Item i = idx.addItem(1);
        i.getItemProperty("firstname").setValue("John");
        i.getItemProperty("lastname").setValue("Johnson");
        i = idx.addItem(2);
        i.getItemProperty("firstname").setValue("Jane");
        i.getItemProperty("lastname").setValue("Janeine");

        table.setContainerDataSource(idx);

        table.setColumnHeader("firstname", "FirstName");
        table.setColumnHeader("lastname", "LastName");
        if (addLabelColGen) {
            table.addGeneratedColumn("name1", new LabelColumnGenerator());
        }
        if (addLayoutColGen) {
            table.addGeneratedColumn("name2", new LayoutColumnGenerator());
        }
        if (fixedColWidths) {
            table.setWidth("400px");
            table.setColumnWidth("firstname", 200);
            table.setColumnWidth("lastname", 300);
        }

        return table;
    }

    public class LabelColumnGenerator implements ColumnGenerator {

        @Override
        public Component generateCell(Table source, Object itemId,
                Object columnId) {
            Item item = source.getItem(itemId);
            String firstname = (String) item.getItemProperty("firstname")
                    .getValue();
            String lastname = (String) item.getItemProperty("lastname")
                    .getValue();
            Label label = new Label(firstname + " " + lastname);
            return label;
        }

    }

    public class LayoutColumnGenerator implements ColumnGenerator {

        @Override
        public Component generateCell(Table source, Object itemId,
                Object columnId) {
            Item item = source.getItem(itemId);
            GridLayout layout = new GridLayout(1, 2);
            String firstname = (String) item.getItemProperty("firstname")
                    .getValue();
            String lastname = (String) item.getItemProperty("lastname")
                    .getValue();
            layout.addComponent(new Label(firstname), 0, 0);
            layout.addComponent(new Label(lastname), 0, 1);
            return layout;
        }

    }

}
