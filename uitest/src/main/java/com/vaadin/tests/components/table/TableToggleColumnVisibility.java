package com.vaadin.tests.components.table;

import com.vaadin.server.ClassResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

/**
 * Test that toggling column visibility does not change custom header, icon,
 * alignment
 */
@SuppressWarnings("serial")
public class TableToggleColumnVisibility extends AbstractReindeerTestUI {

    private final Object[][] columnSets = new Object[][] { { "Name" },
            { "Name", "Last Name" }, { "Last Name", "Name" } };
    private int currentSetNumber = 1;

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();

        final Table table = new Table();

        table.addContainerProperty("Name", String.class, "");
        table.addContainerProperty("Last Name", String.class, null,
                "Hello World", new ClassResource("fi.gif"), Table.Align.RIGHT);

        table.setHeight("200px");

        table.addItem(new Object[] { "Muoso", "Virtanen" }, new Integer(1));
        table.addItem(new Object[] { "Nehemias", "Korhonen" }, new Integer(2));
        table.addItem(new Object[] { "Hannu", "Nieminen" }, new Integer(3));
        table.addItem(new Object[] { "Eelante", "Mäkinen" }, new Integer(4));
        table.setVisibleColumns(columnSets[1]);

        final Button visibToggler = new Button("visibility");
        visibToggler.setId("visib-toggler");
        visibToggler.addClickListener(event -> {
            currentSetNumber = (currentSetNumber == 0) ? 1 : 0;
            table.setVisibleColumns(columnSets[currentSetNumber]);
        });

        final Button orderToggler = new Button("change order");
        orderToggler.setId("order-toggler");
        orderToggler.addClickListener(event -> {
            currentSetNumber = (currentSetNumber == 1) ? 2 : 1;
            table.setVisibleColumns(columnSets[currentSetNumber]);
        });

        layout.addComponent(table);
        layout.addComponent(visibToggler);
        layout.addComponent(orderToggler);

        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Toggling visibility of column should not change custom header,"
                + " icon, alignment";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6245;
    }

}
