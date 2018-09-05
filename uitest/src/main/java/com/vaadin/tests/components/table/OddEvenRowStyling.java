package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.Table;

/**
 * @author jonatan
 *
 */
public class OddEvenRowStyling extends TestBase {

    @Override
    protected void setup() {
        Table t = new Table();
        t.setPageLength(10);
        t.addContainerProperty("foo", String.class, "");
        for (int i = 0; i < 33; i++) {
            t.addItem(i).getItemProperty("foo").setValue("bar");
        }
        addComponent(t);
    }

    @Override
    protected String getDescription() {
        return "Odd/even row styling should not change when scrolling";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7644;
    }

}
