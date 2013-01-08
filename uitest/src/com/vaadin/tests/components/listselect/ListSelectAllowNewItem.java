package com.vaadin.tests.components.listselect;

import java.util.Arrays;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ListSelect;

public class ListSelectAllowNewItem extends TestBase {

    @Override
    protected void setup() {
        ListSelect select = new ListSelect("Select", Arrays.asList("Option 1",
                "Option 2"));
        select.setImmediate(true);
        select.setNewItemsAllowed(true);
        addComponent(select);
    }

    @Override
    protected String getDescription() {
        return "ListSelect with allowNewItems turned on";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10537;
    }

}
