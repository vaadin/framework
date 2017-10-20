package com.vaadin.tests.components.combobox;

import com.vaadin.v7.ui.ComboBox;

public class ComboBoxPageLength extends ComboBoxes2<ComboBox> {

    @Override
    public void initializeComponents() {
        super.initializeComponents();
        getComponent().addValueChangeListener(e -> {
            if (e.getProperty() != null) {
                if (e.getProperty().getValue() != null) {
                    Integer value = Integer
                            .parseInt(((String) e.getProperty().getValue())
                                    .split(" ")[1]);
                    getComponent().setPageLength(value);
                } else {
                    getComponent().setPageLength(0);
                }
            }
        });
    }

    @Override
    protected Integer getTicketNumber() {
        return 5381;
    }

    @Override
    protected String getTestDescription() {
        return super.getTestDescription()
                + ", changing ComboBox value will change the ComboBox pageLength to the # of the selected item, or to 0 in null selection.";
    }
}
