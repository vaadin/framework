package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;

public class DisabledSortingTableSqlContainer extends TableSqlContainer {

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);

        addButton("Enable sorting", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                table.setSortEnabled(true);
            }
        });

        addButton("Disable sorting", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                table.setSortEnabled(false);
            }
        });

        addButton("Sort by empty array", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                table.sort(new Object[] {}, new boolean[] {});
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Sorting with empty arrays should reset sorting and hide sorting indicator in Table connected to a SQLContainer";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16563;
    }

}
