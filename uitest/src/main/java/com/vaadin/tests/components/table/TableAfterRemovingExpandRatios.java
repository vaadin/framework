package com.vaadin.tests.components.table;

/**
 * Test that column natural width is restored after removing expand ratio
 *
 * @author Vaadin Ltd
 */

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.ui.Table;

public class TableAfterRemovingExpandRatios extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest req) {
        getLayout().setSizeFull();

        Table tableInitial = createTable();
        Table table = createTable();

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(createSetExpandRatiosButton(table));
        buttons.addComponent(createUnsetExpandRatiosButton(table));
        buttons.addComponent(createAddItemButton(table));

        addComponent(tableInitial);
        addComponent(table);
        addComponent(buttons);
    }

    private Table createTable() {
        Table table = new Table();
        table.addContainerProperty("column1", String.class, "Humprtrtwwww");
        table.addContainerProperty("column2", String.class, "Dumpttttwwww");
        table.addContainerProperty("column3", String.class, "Dogtt");

        for (int row = 0; row < 4; row++) {
            table.addItem();
        }
        table.setWidth("500px");
        table.setHeight("300px");
        return table;

    }

    private NativeButton createSetExpandRatiosButton(final Table table) {
        NativeButton button = new NativeButton("Set expand", event -> {
            table.setColumnExpandRatio("column1", 1.0f);
            table.setColumnExpandRatio("column2", 3.0f);
        });
        button.setId("expand-button");
        return button;
    }

    private NativeButton createUnsetExpandRatiosButton(final Table table) {
        NativeButton button = new NativeButton("Unset expand", event -> {
            table.setColumnExpandRatio("column1", -1);
            table.setColumnExpandRatio("column2", -1);
        });
        button.setId("unexpand-button");
        return button;
    }

    private NativeButton createAddItemButton(final Table table) {
        NativeButton button = new NativeButton("Add item",
                event -> table.addItem());
        button.setId("add-button");
        return button;

    }

    @Override
    protected String getTestDescription() {
        return "On setting expand ratios to false previous column widths should be restored";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15101;
    }

}
