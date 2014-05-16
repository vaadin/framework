package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class SetCurrentPageFirstItemId extends TestBase {
    int index = 0;

    private final Table table = new Table();

    @Override
    public void setup() {

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setHeight("100%");
        mainLayout.setMargin(true);

        getMainWindow().setContent(mainLayout);

        mainLayout.addComponent(table);
        table.setSizeFull();
        table.addContainerProperty("rowID", Integer.class, null);
        for (int i = 0; i < 20; i++) {
            addRow();
        }

        Button addrowButton = new Button("Add row");
        addrowButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent pEvent) {
                Object id = addRow();
                table.setCurrentPageFirstItemId(id);
            }
        });

        mainLayout.addComponent(addrowButton);
    }

    private Object addRow() {
        return table.addItem(new Object[] { index++ }, null);
    }

    @Override
    protected String getDescription() {
        return "Table.setCurrentPageFirstItemId doesn't always work with full sized Table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7607;
    }
}
