package com.vaadin.tests.components.form;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Form;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class FormRenderingFlicker extends TestBase {

    private VerticalLayout tableLayout;
    private Table table;
    private Panel tablePanel;
    private Form form;

    @Override
    protected String getDescription() {
        return "Clicking on an item in the table will replace the panel (surrounding the table) with a form. This should not cause the table rows to move downwards or cause any other visible flicker";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2816;
    }

    @Override
    protected void setup() {
        createTableLayout();
        form = new Form();

        tablePanel = new Panel();
        tablePanel.setContent(tableLayout);

        addComponent(tablePanel);
    }

    private void createTableLayout() {
        tableLayout = new VerticalLayout();
        table = new Table();
        table.addContainerProperty("name", String.class, "");
        table.addContainerProperty("age", String.class, "");
        for (int i = 0; i < 100; i++) {
            table.addItem(new Object[] { "Name " + i, String.valueOf(i) },
                    new Object());
        }
        table.setImmediate(true);
        table.addListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                clicked(event.getItem());
            }

        });

        tableLayout.addComponent(table);
    }

    protected void clicked(Item item) {
        getLayout().replaceComponent(tablePanel, form);
    }

}
