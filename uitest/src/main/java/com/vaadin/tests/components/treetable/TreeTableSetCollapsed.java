package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TreeTable;

public class TreeTableSetCollapsed extends TestBase {

    @Override
    protected void setup() {
        createTreeTableAndPopulate();
        addComponent(new Button("Create another TreeTable",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        createTreeTableAndPopulate();
                    }
                }));
    }

    private void createTreeTableAndPopulate() {
        TreeTable tt = new TreeTable();
        tt.addContainerProperty("Foo", String.class, "");
        tt.addContainerProperty("Bar", String.class, "");

        Object item1 = tt.addItem(new Object[] { "Foo", "Bar" }, null);
        Object item2 = tt.addItem(new Object[] { "Foo2", "Bar2" }, null);

        tt.setParent(item2, item1);

        tt.setCollapsed(item1, false);

        addComponent(tt);
    }

    @Override
    protected String getDescription() {
        return "Using setCollapsed before the treetable has initially been rendered should not cause any problems";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7605);
    }

}
