package com.vaadin.tests.components.table;

import com.vaadin.event.Action;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class TableContextMenuOnField extends TestBase {

    private static final Action ACTION_MYACTION = new Action("Action!!");

    @Override
    protected void setup() {
        Table table = new Table();
        table.setSelectable(true);
        table.setMultiSelect(true);

        table.addActionHandler(new Action.Handler() {
            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                // TODO Auto-generated method stub

            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { ACTION_MYACTION };
            }
        });

        table.addGeneratedColumn("layout", new Table.ColumnGenerator() {

            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {

                VerticalLayout layout = new VerticalLayout();
                layout.addComponent(new TextField());

                layout.addLayoutClickListener(
                        event -> getMainWindow().showNotification("HELLO"));

                return layout;
            }
        });

        table.addGeneratedColumn("textfield", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                return new TextField();
            }
        });

        table.addGeneratedColumn("link", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Link("Link", null);
            }
        });

        table.addGeneratedColumn("button", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Button("Button");
            }
        });

        table.addItem();
        table.addItem();
        table.addItem();
        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Right clicking on an item without a context menu should bring"
                + "up the Tables context menu";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4264;
    }

}
