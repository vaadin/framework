package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableWithNoncollapsibleColumns extends TestBase {

    @Override
    protected void setup() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        addComponent(layout);

        final Table table = new Table();
        table.setWidth("100%");
        table.setHeight("300px");
        table.setColumnCollapsingAllowed(true);

        table.addContainerProperty("Column 1 - noncollapsible", String.class,
                null);
        table.addContainerProperty("Column 2 - collapsible", String.class, null);
        table.addContainerProperty("Column 3 - toggle collapsing",
                String.class, null);

        table.setColumnCollapsible("Column 1 - noncollapsible", false);
        layout.addComponent(table);

        final Button button1 = new Button("Column 1: collapse/show",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setColumnCollapsed(
                                "Column 1 - noncollapsible",
                                !table.isColumnCollapsed("Column 1 - noncollapsible"));
                    }
                });
        final Button button2 = new Button("Column 2: collapse/show",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setColumnCollapsed(
                                "Column 2 - collapsible",
                                !table.isColumnCollapsed("Column 2 - collapsible"));
                    }
                });

        final Button button3 = new Button("Column 3: collapse/show",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setColumnCollapsed(
                                "Column 3 - toggle collapsing",
                                !table.isColumnCollapsed("Column 3 - toggle collapsing"));
                    }
                });
        final Button button4 = new Button(
                "Column 3: make noncollapsible/collapsible",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setColumnCollapsible(
                                "Column 3 - toggle collapsing",
                                !table.isColumnCollapsible("Column 3 - toggle collapsing"));
                    }
                });

        layout.addComponent(button1);
        layout.addComponent(button2);
        layout.addComponent(button3);
        layout.addComponent(button4);

    }

    @Override
    protected String getDescription() {
        return "Often a table has one column that identifies the row better than any other and it would not make sense to collapse that one. Make it possible from the server side api to disable collapsing for some properties. These properties could appear as grayed out in the collapse drop down menu.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7495;
    }

}
