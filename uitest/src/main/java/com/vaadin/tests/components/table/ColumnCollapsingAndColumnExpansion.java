package com.vaadin.tests.components.table;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnCollapseEvent;
import com.vaadin.ui.Table.ColumnCollapseListener;

public class ColumnCollapsingAndColumnExpansion extends AbstractTestUIWithLog {

    private Table table;

    @Override
    protected void setup(VaadinRequest request) {

        table = new Table();

        table.addContainerProperty("Col1", String.class, null);
        table.addContainerProperty("Col2", String.class, null);
        table.addContainerProperty("Col3", String.class, null);
        table.setColumnCollapsingAllowed(true);

        table.addActionHandler(new Handler() {

            final Action H = new Action("Toggle Col2");
            final Action[] actions = new Action[] { H };

            @Override
            public Action[] getActions(Object target, Object sender) {
                return actions;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                table.setColumnCollapsed("Col2",
                        !table.isColumnCollapsed("Col2"));

            }
        });

        table.setSizeFull();

        for (int y = 1; y < 5; y++) {
            table.addItem(new Object[] { "cell " + 1 + "-" + y,
                    "cell " + 2 + "-" + y, "cell " + 3 + "-" + y, },
                    new Object());
        }
        table.addColumnCollapseListener(new ColumnCollapseListener() {

            @Override
            public void columnCollapseStateChange(ColumnCollapseEvent event) {
                log("Collapse state for " + event.getPropertyId()
                        + " changed to "
                        + table.isColumnCollapsed(event.getPropertyId()));

            }
        });
        addComponent(table);

        for (int i = 1; i <= 3; i++) {
            HorizontalLayout hl = new HorizontalLayout();
            final String id = "Col" + i;
            Button hide = new Button("Collapse " + id, new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    table.setColumnCollapsed(id, true);
                }
            });

            Button show = new Button("Show " + id, new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    table.setColumnCollapsed(id, false);
                }
            });

            hl.addComponent(hide);
            hl.addComponent(show);
            addComponent(hl);
        }

    }

    @Override
    protected String getTestDescription() {
        return "After hiding column 2 the remaining columns (1 and 3) should use all available space in the table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3246;
    }
}
