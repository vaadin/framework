package com.vaadin.tests.components.select;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Select;

public class SelectIconPlacement extends TestBase {
    private static final long serialVersionUID = 1L;

    private Select mySelect;

    @Override
    protected void setup() {
        mySelect = new Select("Foo");
        String bar = "FooBarBaz";
        mySelect.addItem(bar);
        mySelect.setItemIcon(bar, new ThemeResource("common/icons/error.png"));
        mySelect.select(bar);
        addComponent(mySelect);
    }

    @Override
    protected String getDescription() {
        return "A select with item icons pushes the caption of that item to the right to make room for the icon. It works fine in all browsers except IE8.<br/>"
                + "Upon component render the icon and caption is on top of each others, and it corrects itself when you open the dropdown. ";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3991;
    }

}
