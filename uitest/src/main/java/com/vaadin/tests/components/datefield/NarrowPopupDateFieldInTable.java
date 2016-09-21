package com.vaadin.tests.components.datefield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.v7.ui.Table;

public class NarrowPopupDateFieldInTable extends TestBase {

    private final Object DATE = "Date";

    @Override
    public void setup() {
        DateField df = new DateField();
        df.setWidth("100%");

        Table t = new Table();
        t.setWidth("100px");
        t.addContainerProperty(DATE, Component.class, null);
        t.addItem(new Object[] { df }, "1");
        addComponent(t);
    }

    @Override
    protected String getDescription() {
        return "Simple test to ensure a narrow PopupDateField can be used in a Table.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6166;
    }

}
