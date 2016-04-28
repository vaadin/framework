package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class DisabledTableShouldNotSendPageLengthUpdates extends TestBase {

    final Table table = new Table();

    @Override
    protected void setup() {
        HorizontalSplitPanel split = new HorizontalSplitPanel();
        table.addContainerProperty("name", Integer.class, 0);
        Button button = new Button("Add items", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                for (int i = 0; i < 5; i++) {
                    Object id = table.addItem();
                    table.getItem(id).getItemProperty("name").setValue(i);
                }
            }
        });
        table.setEnabled(false);
        table.setSizeFull();
        split.setFirstComponent(table);
        split.setSecondComponent(button);
        getLayout().setSizeFull();
        split.setSizeFull();
        addComponent(split);
    }

    @Override
    protected String getDescription() {
        return "A disabled table should not send pageLength updates causing 'Warning: Ignoring variable change for disabled component class com.vaadin.ui.Table' warnings in the server logs";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4317;
    }

}
