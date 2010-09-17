package com.vaadin.tests.components.table;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class ColumnHeaderAlignments extends TestBase {

    @Override
    protected void setup() {
        Select theme = new Select();
        theme.addItem("reindeer");
        theme.addItem("runo");
        theme.addItem("base");
        theme.setValue("reindeer");
        theme.setNullSelectionAllowed(false);
        theme.setImmediate(true);
        theme.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                setTheme(String.valueOf(event.getProperty().getValue()));
            }
        });
        addComponent(theme);
        HorizontalLayout tables = new HorizontalLayout();
        tables.addComponent(createTable(null));
        tables.addComponent(createTable("strong"));
        tables.addComponent(createTable("black"));
        addComponent(tables);
    }

    private Table createTable(String style) {
        Table table = new Table();
        table.addContainerProperty("Foo (left)", String.class, "");
        table.addContainerProperty("Bar (center)", String.class, "");
        table.addContainerProperty("Baz (right)", String.class, "");

        table.setColumnAlignment("Foo (left)", Table.ALIGN_LEFT);
        table.setColumnAlignment("Bar (center)", Table.ALIGN_CENTER);
        table.setColumnAlignment("Baz (right)", Table.ALIGN_RIGHT);
        if (style != null) {
            table.setStyleName(style);
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
