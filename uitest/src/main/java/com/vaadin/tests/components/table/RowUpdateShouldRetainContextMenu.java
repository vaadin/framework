package com.vaadin.tests.components.table;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;

public class RowUpdateShouldRetainContextMenu extends TestBase {

    private ProgressIndicator indicator = new ProgressIndicator();
    private Table table = new Table();

    private int ctr = 0;

    @Override
    protected void setup() {
        indicator.setWidth("200px");
        indicator.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                // Do some changes to the table
                table.setColumnHeader("Column", "Column " + ctr);
                table.getItem(2).getItemProperty("Column")
                        .setValue("Test " + ctr++);
            }
        });
        Thread updater = new Thread() {
            private float progress = 0;

            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                    getContext().lock();
                    try {
                        indicator.setValue(progress += 0.01);
                    } finally {
                        getContext().unlock();
                    }
                }
            }
        };
        updater.start();

        addComponent(indicator);

        table.setWidth("200px");
        table.setHeight("200px");

        table.addActionHandler(new Action.Handler() {
            @Override
            public void handleAction(Action action, Object sender, Object target) {
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new Action("Action 1"),
                        new Action("Action 2"), };
            }
        });

        table.addContainerProperty("Column", String.class, "");

        table.addItem();
        for (int i = 0; i < 15; ++i) {
            table.addItem(new String[] { "Row " + ctr++, }, ctr);
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Open context menu is closed if a row is updated via e.g. server push";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8526;
    }

}
