package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HasChildMeasurementHint.ChildMeasurementHint;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class TableChildMeasurementHint extends AbstractReindeerTestUI {

    private HorizontalLayout buttonLayout = new HorizontalLayout();
    private HorizontalLayout layout;
    private Table table1, table2, table3;

    @Override
    public void setup(VaadinRequest request) {
        initMain();
    }

    protected void initMain() {
        ((AbstractOrderedLayout) getContent()).setMargin(false);
        layout = new HorizontalLayout();
        layout.setSizeFull();
        buttonLayout.setSpacing(false);
        addComponent(buttonLayout);
        addComponent(layout);

        table1 = createTable();
        table1.setSizeFull();
        table1.setChildMeasurementHint(ChildMeasurementHint.MEASURE_ALWAYS);

        table2 = createTable();
        table2.setSizeFull();
        table2.setChildMeasurementHint(ChildMeasurementHint.MEASURE_IF_NEEDED);

        table3 = createTable();
        table3.setSizeFull();
        table3.setChildMeasurementHint(ChildMeasurementHint.MEASURE_NEVER);

        buttonLayout
                .addComponent(new Button("Show table1", new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        layout.addComponent(table1);
                        table1.focus();
                    }
                }));
        buttonLayout
                .addComponent(new Button("Show table2", new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        layout.removeComponent(table1);
                        layout.addComponent(table2);
                        table2.focus();
                    }
                }));
        buttonLayout
                .addComponent(new Button("Show table3", new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        layout.removeComponent(table2);
                        layout.addComponent(table3);
                        table3.focus();
                    }
                }));

    }

    protected Table createTable() {
        Table table = new Table();
        table.setSelectable(true);
        table.setPageLength(39);

        for (int i = 0; i < 5; i++) {
            table.addContainerProperty("First_Name" + i, String.class, null);
            table.addContainerProperty("Last Name" + i, String.class, null);
            table.addContainerProperty("Year" + i, Integer.class, null);
        }

        /* Add a few items in the table. */
        int j = 0;
        for (int i = 0; i < 2; i++) {
            table.addItem(
                    makeRow(new Object[] { "Nicolaus" + i, "Copernicus", 1473 },
                            5),
                    j++);
            table.addItem(
                    makeRow(new Object[] { "Tycho" + i, "Brahe", 1546 }, 5),
                    j++);
            table.addItem(
                    makeRow(new Object[] { "Giordano" + i, "Bruno", 1548 }, 5),
                    j++);
            table.addItem(
                    makeRow(new Object[] { "Galileo" + i, "Galilei", 1564 }, 5),
                    j++);
            table.addItem(
                    makeRow(new Object[] { "Johannes" + i, "Kepler", 1571 }, 5),
                    j++);
            table.addItem(
                    makeRow(new Object[] { "Isaac" + i, "Newton", 1643 }, 5),
                    j++);
        }

        table.addGeneratedColumn("First_Name" + 0, new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table components, Object o, Object o2) {
                ComboBox b = new ComboBox("ComboBox");
                b.setWidthUndefined();
                return b;
            }
        });

        table.addGeneratedColumn("First_Name" + 1, new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table components, Object o, Object o2) {
                GridLayout b = new GridLayout();
                b.addComponents(new Label("l1"), new Button("b"),
                        new Label("l2"));
                b.setWidthUndefined();
                return b;
            }
        });

        table.addGeneratedColumn("First_Name" + 2, new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table components, Object o, Object o2) {
                Button b = new Button("Button");
                b.setWidthUndefined();
                return b;
            }
        });

        table.addGeneratedColumn("First_Name" + 3, new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table components, Object o, Object o2) {
                TextField b = new TextField("Textfield");
                b.setWidthUndefined();
                return b;
            }
        });

        table.addGeneratedColumn("First_Name" + 4, new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table components, Object o, Object o2) {
                AbstractDateField<?, ?> b = new TestDateField("DateField");
                b.setWidthUndefined();
                return b;
            }
        });

        table.addGeneratedColumn("First_Name" + 5, new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table components, Object o, Object o2) {
                Label b = new Label("Label");
                b.setWidthUndefined();
                return b;
            }
        });

        return table;
    }

    protected Object[] makeRow(Object[] data, int c) {
        Object[] row = new Object[c * data.length];
        for (int j = 0; j < c; j++) {
            int x = 0;
            for (Object value : data) {
                row[j * data.length + x] = value;
                x++;
            }
        }

        return row;
    }
}