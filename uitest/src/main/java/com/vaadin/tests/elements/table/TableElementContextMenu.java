package com.vaadin.tests.elements.table;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.Table;

public class TableElementContextMenu extends AbstractTestUI {

    Table table = new Table();
    private int COLUMNS = 4;
    private int ROWS = 10;

    @Override
    protected void setup(VaadinRequest request) {
        fillTable(table);
        table.addActionHandler(new TableActionHandler());
        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that calling TableElement.contextClick() opens the context menu";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14385;
    }

    // set up the properties (columns)
    private void initProperties(Table table) {
        for (int i = 0; i < COLUMNS; i++) {
            table.addContainerProperty("property" + i, String.class,
                    "some value");
        }
    }

    // fill the table with some random data
    private void fillTable(Table table) {
        initProperties(table);
        for (int i = 0; i < ROWS; i++) {
            String[] line = new String[COLUMNS];
            for (int j = 0; j < COLUMNS; j++) {
                line[j] = "col=" + j + " row=" + i;
            }
            table.addItem(line, null);
        }
    }

    public class TableActionHandler implements Handler {

        @Override
        public Action[] getActions(Object target, Object sender) {
            Action[] actions = new Action[2];
            actions[0] = new Action("Add");
            actions[1] = new Action("Edit");
            return actions;
        }

        @Override
        public void handleAction(Action action, Object sender, Object target) {
            Notification.show(action.getCaption());

        }

    }
}
