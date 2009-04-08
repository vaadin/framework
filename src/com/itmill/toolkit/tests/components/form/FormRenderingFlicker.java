package com.itmill.toolkit.tests.components.form;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.event.ItemClickEvent;
import com.itmill.toolkit.event.ItemClickEvent.ItemClickListener;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.VerticalLayout;

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
        tablePanel.setLayout(tableLayout);

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
