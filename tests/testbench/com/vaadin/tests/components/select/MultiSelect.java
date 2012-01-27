package com.vaadin.tests.components.select;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Select;

public class MultiSelect extends TestBase {

    @SuppressWarnings("deprecation")
    @Override
    protected void setup() {
        Select selectComponent = new Select();
        selectComponent.setMultiSelect(true);

        String[] selection = { "One", "Hund", "Three" };
        for (String word : selection) {
            selectComponent.addItem(word);
        }

        addComponent(selectComponent);
    }

    @Override
    protected String getDescription() {
        return "The select is in multi select mode and should be rendered as such";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4553;
    }

}
