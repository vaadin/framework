package com.vaadin.tests.components.table;

import com.vaadin.event.Action;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;

public class TableContextMenu extends TestBase {

    private static final Action ACTION_MYACTION = new Action("Action!!");

    @Override
    protected void setup() {
        Table table = new Table();
        table.setSelectable(true);
        table.setMultiSelect(true);

        table.addActionHandler(new Action.Handler() {
            @Override
            public void handleAction(Action action, Object sender, Object target) {
                Notification.show("Done that :-)");
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { ACTION_MYACTION };
            }
        });

        // TODO should work with all combinations
        table.setImmediate(true);
        table.setSelectable(true);
        table.setMultiSelect(true);

        table.addContainerProperty("Foo", String.class, "BAR1");
        table.addContainerProperty("Bar", String.class, "FOO2");

        // FIXME works with lots of rows (more than pagelength), don't work with
        // none
        for (int i = 0; i < 3; i++) {
            table.addItem();
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Right clicking on an item without a context menu should bring"
                + "up the Tables context menu. With touch devices context menu must popup with long touch.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8639;
    }

}
