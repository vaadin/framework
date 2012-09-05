package com.vaadin.tests.containers.sqlcontainer;

import com.vaadin.Application;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;

/**
 * See http://dev.vaadin.com/ticket/9155 .
 */
public class ComboBoxUpdateProblem extends Application {
    private final DatabaseHelper databaseHelper = new DatabaseHelper();

    @Override
    public void init() {
        setMainWindow(new UI.LegacyWindow("Test window"));

        ComboBox combo = new ComboBox("Names",
                databaseHelper.getTestContainer());
        combo.setItemCaptionPropertyId("FIELD1");
        combo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        combo.setImmediate(true);

        getMainWindow().addComponent(combo);
    }

}