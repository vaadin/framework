package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.ItemDataSource;
import com.vaadin.ui.ComboBox;

public class ComboBoxNavigation extends TestBase {

    @Override
    protected String getDescription() {
        return "Entering e in the field and scrolling down with the arrow keys should always select the next item, also when the page changes. Scrolling back up should always select the previous item, also when changing pages.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2214;
    }

    @Override
    protected void setup() {
        ComboBox<String> cb = new ComboBox<>(null, new ItemDataSource(100));

        addComponent(cb);

    }

}
