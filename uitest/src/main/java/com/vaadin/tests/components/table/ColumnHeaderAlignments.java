package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class ColumnHeaderAlignments extends TestBase {

    private static final String BAZ = "Baz (right)";
    private static final String BAR = "Bar (center)";
    private static final String FOO = "Foo (left)";
    private Table fooTable;
    private Table barTable;
    private Table bazTable;

    @Override
    protected void setup() {
        Select theme = new Select();
        theme.addItem("reindeer");
        theme.addItem("runo");
        theme.addItem("base");
        theme.addItem("liferay");
        theme.setValue("reindeer");
        theme.setNullSelectionAllowed(false);
        theme.setImmediate(true);
        theme.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                setTheme(String.valueOf(event.getProperty().getValue()));
            }
        });
        addComponent(theme);
        CheckBox footers = new CheckBox("Show footers");
        footers.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean visible = (Boolean) event.getProperty().getValue();
                fooTable.setFooterVisible(visible);
                barTable.setFooterVisible(visible);
                bazTable.setFooterVisible(visible);
            }
        });
        footers.setImmediate(true);
        addComponent(footers);
        HorizontalLayout tables = new HorizontalLayout();
        fooTable = createTable(null);
        tables.addComponent(fooTable);
        barTable = createTable("strong");
        tables.addComponent(barTable);
        bazTable = createTable("black");
        tables.addComponent(bazTable);
        addComponent(tables);
    }

    private Table createTable(String style) {
        Table table = new Table();
        table.addContainerProperty(FOO, String.class, "");
        table.addContainerProperty(BAR, String.class, "");
        table.addContainerProperty(BAZ, String.class, "");

        table.setColumnAlignment(FOO, Table.ALIGN_LEFT);
        table.setColumnAlignment(BAR, Table.ALIGN_CENTER);
        table.setColumnAlignment(BAZ, Table.ALIGN_RIGHT);
        if (style != null) {
            table.setStyleName(style);
        }

        for (int i = 0; i < 100; i++) {
            Item item = table.addItem(i);
            item.getItemProperty(FOO).setValue("foo");
            item.getItemProperty(BAR).setValue("bar");
            item.getItemProperty(BAZ).setValue("baz");
        }

        return table;
    }

    @Override
    protected String getDescription() {
        return "Aligned column headers should have style names telling the alignment";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5066;
    }

}
