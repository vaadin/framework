package com.itmill.toolkit.demo.testbench;

import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TestForContainerFilterable extends CustomComponent {

    OrderedLayout lo = new OrderedLayout();
    IndexedContainer ic = new IndexedContainer();
    Table t = new Table();
    private static String parts[] = { "Neo", "Sa", "rem", "the", "adi", "za",
            "tre", "day", "Ca", "re", "cen", "ter", "mi", "nal" };
    TextField fooFilter = new TextField("foo-filter");
    TextField barFilter = new TextField("bar-filter");
    Button filterButton = new Button("Filter");
    Label count = new Label();

    public TestForContainerFilterable() {
        setCompositionRoot(lo);

        // Init datasource
        ic.addContainerProperty("foo", String.class, "");
        ic.addContainerProperty("bar", String.class, "");
        for (int i = 0; i < 1000; i++) {
            Object id = ic.addItem();
            ic.getContainerProperty(id, "foo").setValue(randomWord());
            ic.getContainerProperty(id, "bar").setValue(randomWord());
        }

        // Init filtering view
        Panel filterPanel = new Panel("Filter", new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL));
        filterPanel.setWidth(100);
        filterPanel.setWidthUnits(Sizeable.UNITS_PERCENTAGE);
        lo.addComponent(filterPanel);
        filterPanel.addComponent(fooFilter);
        filterPanel.addComponent(barFilter);
        filterPanel.addComponent(filterButton);
        fooFilter
                .setDescription("Filters foo column in case-sensitive contains manner.");
        barFilter
                .setDescription("Filters bar column in case-insensitive prefix manner.");
        filterPanel.addComponent(count);

        // Table
        lo.addComponent(t);
        t.setPageLength(12);
        t.setWidth(100);
        t.setWidthUnits(Sizeable.UNITS_PERCENTAGE);
        t.setContainerDataSource(ic);

        // Handler
        filterButton.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                ic.removeAllContainerFilters();
                if (fooFilter.toString().length() > 0) {
                    ic.addContainerFilter("foo", fooFilter.toString(), false,
                            false);
                }
                if (barFilter.toString().length() > 0) {
                    ic.addContainerFilter("bar", barFilter.toString(), true,
                            true);
                }
                count.setValue("Rows in table: " + ic.size());
            }
        });

        // Resetbutton
        lo.addComponent(new Button("Rebind table datasource",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        t.setContainerDataSource(ic);
                    }
                }));
    }

    private String randomWord() {
        int len = (int) (Math.random() * 4);
        StringBuffer buf = new StringBuffer();
        while (len-- >= 0) {
            buf.append(parts[(int) (Math.random() * parts.length)]);
        }
        return buf.toString();
    }
}
