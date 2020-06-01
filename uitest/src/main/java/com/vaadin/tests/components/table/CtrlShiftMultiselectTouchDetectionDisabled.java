package com.vaadin.tests.components.table;

import java.util.Set;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Label;

public class CtrlShiftMultiselectTouchDetectionDisabled
        extends CtrlShiftMultiselect {

    protected Label label;

    @Override
    protected void setup() {
        super.setup();
        label = new Label("0");
        label.setId("count");
        label.setCaption("Amount of selected items");
        table.setMultiSelectTouchDetectionEnabled(false);
        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Property property = event.getProperty();
                Set set = (Set) property.getValue();
                label.setValue("" + set.size());
            }
        });
        addComponent(label);
    }

    @Override
    protected String getDescription() {
        return "Allow disabling multi selection's touch screen detection for hybrid devices";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11601;
    }

}