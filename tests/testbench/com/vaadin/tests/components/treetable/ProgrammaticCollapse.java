package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

public class ProgrammaticCollapse extends TestBase {

    @Override
    protected void setup() {
        VerticalLayout layout = new VerticalLayout();
        final TreeTable table = new TreeTable();
        table.setPageLength(10);
        table.addContainerProperty("A", String.class, null);
        table.addContainerProperty("B", String.class, null);
        for (int i = 1; i <= 100; ++i) {
            int parentid = i;
            table.addItem(new Object[] { "A" + i, "B" + i }, parentid);
            for (int j = 1; j < 5; ++j) {
                int id = 1000 * i + j;
                table.addItem(new Object[] { "A" + i + "." + j,
                        "B" + i + "." + j }, id);
                table.setParent(id, parentid);
            }
        }
        layout.addComponent(table);
        layout.addComponent(new Button("Expand / Collapse",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        boolean collapsed = !table.isCollapsed(1);
                        Notification.show("set collapsed: " + collapsed);
                        table.setCollapsed(1, collapsed);
                    }
                }));
        layout.addComponent(new Button("Expand / Collapse last",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        boolean collapsed = !table.isCollapsed(100);
                        Notification.show("set collapsed: " + collapsed);
                        table.setCollapsed(100, collapsed);
                    }
                }));
        layout.addComponent(new Button("Expand / Collapse multiple",
                new ClickListener() {
                    private boolean collapsed = true;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        collapsed = !collapsed;
                        Notification.show("set collapsed: " + collapsed);
                        for (int i = 0; i < 50; ++i) {
                            table.setCollapsed(i * 2, collapsed);
                        }
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
