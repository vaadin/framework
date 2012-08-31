package com.vaadin.tests.components.table;

import java.util.Collection;

import com.vaadin.data.Property;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class LongMultiselect extends TestBase {

    private enum ItemProperty {
        COLUMN1, COLUMN2
    }

    @Override
    protected void setup() {
        VerticalLayout layout = generateLayout();
        addComponent(layout);
        layout.setSizeFull();
    }

    private VerticalLayout generateLayout() {
        VerticalLayout layout = new VerticalLayout();

        final Table table = new Table("Ticket #8264 table");
        layout.addComponent(table);
        table.setWidth("200px");
        table.setHeight("170px");
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true);

        // Create example data
        table.addContainerProperty(ItemProperty.COLUMN1, String.class, null);
        table.addContainerProperty(ItemProperty.COLUMN2, String.class, null);
        for (int i = 1; i < 100; i++) {
            table.addItem(new String[] { "Item " + i, null }, i);
        }

        // Add action button
        layout.addComponent(new Button("Do It", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // Set ItemProperty.COLUMN2 for all selected values of table
                Collection selectedIds = (Collection) table.getValue();
                for (final Object itemId : selectedIds) {
                    final Property p = table.getItem(itemId).getItemProperty(
                            ItemProperty.COLUMN2);
                    if (p.getValue() instanceof String) {
                        p.setValue(null);
                    } else {
                        p.setValue("updated");
                    }
                }
            }
        }));

        return layout;
    }

    @Override
    protected String getDescription() {
        return "Multiselecting 94 rows (from \"item 5\" to \"item 98\") and modifying second column of each row selected (press Do It). This should work (update the 2nd column) and not cause JS exception.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8264;
    }
}
