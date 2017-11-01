package com.vaadin.tests.containers;

import java.util.Collection;
import java.util.Locale;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.DefaultItemSorter;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class TestItemSorter extends TestBase {

    private static final Object BUTTON = "Button";
    private static final Object CHECKBOX = "CheckBox";
    private static final Object STRING = "String";

    private Table table;
    private IndexedContainer container;

    @Override
    protected void setup() {
        table = new Table("DefaultItemSorter with custom comparator");
        container = createContainer();
        populateContainer(container);
        container.setItemSorter(new DefaultItemSorter((o1, o2) -> {
            if (o1 instanceof CheckBox && o2 instanceof CheckBox) {
                Boolean b1 = ((CheckBox) o1).getValue();
                return b1.compareTo(((CheckBox) o2).getValue());
            }
            if (o1 instanceof Button && o2 instanceof Button) {
                String caption1 = ((Button) o1).getCaption()
                        .toLowerCase(Locale.ROOT);
                String caption2 = ((Button) o2).getCaption()
                        .toLowerCase(Locale.ROOT);
                return caption1.compareTo(caption2);
            }
            if (o1 instanceof String && o2 instanceof String) {
                return ((String) o1).toLowerCase(Locale.ROOT)
                        .compareTo(((String) o2).toLowerCase(Locale.ROOT));
            }

            return 0;
        }));
        table.setContainerDataSource(container);

        addComponent(table);

    }

    private static void populateContainer(IndexedContainer container) {
        container.removeAllItems();
        String[] strings = { "Text 1", "Text 2", "true", "false", "Caption 1",
                "Caption 2" };
        for (String s : strings) {
            Object id = container.addItem();
            Item item = container.getItem(id);
            item.getItemProperty(STRING).setValue(s);
            item.getItemProperty(BUTTON).setValue(new Button(s));
            item.getItemProperty(CHECKBOX)
                    .setValue(new CheckBox("", s.equals("true")));
        }

    }

    private static IndexedContainer createContainer() {
        IndexedContainer ic = new IndexedContainer() {
            @Override
            public Collection<?> getSortableContainerPropertyIds() {
                // Default implementation allows sorting only if the property
                // type can be cast to Comparable
                return getContainerPropertyIds();
            }
        };
        ic.addContainerProperty(BUTTON, Button.class, null);
        ic.addContainerProperty(CHECKBOX, CheckBox.class, null);
        ic.addContainerProperty(STRING, String.class, null);
        return ic;
    }

    @Override
    protected String getDescription() {
        return "Test that uses a custom ItemSorter to allow sorting Property types that do not implement Comparable";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
