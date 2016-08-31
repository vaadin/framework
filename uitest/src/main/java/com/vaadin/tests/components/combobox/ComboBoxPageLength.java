package com.vaadin.tests.components.combobox;

import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.ComboBox;

public class ComboBoxPageLength extends ComboBoxes2<ComboBox> {

    @Override
    public void initializeComponents() {
        super.initializeComponents();
        getComponent().addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty() != null) {
                    if (event.getProperty().getValue() != null) {
                        Integer value = Integer.parseInt(
                                ((String) event.getProperty().getValue())
                                        .split(" ")[1]);
                        getComponent().setPageLength(value);
                    } else {
                        getComponent().setPageLength(0);
                    }
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
