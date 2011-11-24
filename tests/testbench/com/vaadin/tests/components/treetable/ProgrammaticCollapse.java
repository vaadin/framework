package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeTable;

public class ProgrammaticCollapse extends TestBase {

    @Override
    protected void setup() {
        HorizontalLayout layout = new HorizontalLayout();
        final TreeTable table = new TreeTable();
        table.addContainerProperty("A", String.class, null);
        table.addContainerProperty("B", String.class, null);
        table.addItem(new Object[] { "A1", "B1" }, 1);
        table.addItem(new Object[] { "A2", "B2" }, 2);
        table.setParent(2, 1);
        layout.addComponent(table);
        layout.addComponent(new Button("Expand / Collapse",
                new ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        boolean collapsed = !table.isCollapsed(1);
                        table.getWindow().showNotification(
                                "set collapsed: " + collapsed);
                        table.setCollapsed(1, collapsed);
                    }
                }));
        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Using setCollapsed(...) after the treetable has been rendered should update the UI";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7988;
    }

}
