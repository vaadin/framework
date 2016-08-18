package com.vaadin.tests.util;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.data.Validator.InvalidValueException;

public class CheckBoxWithPropertyDataSource extends CheckBox {

    public CheckBoxWithPropertyDataSource(String caption) {
        super(caption);
    }

    public CheckBoxWithPropertyDataSource(String caption,
            Property<Boolean> property) {
        super(caption);

        setValue(property.getValue());
        addValueChangeListener(event -> property.setValue(event.getValue()));

        if (property instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) property)
                    .addValueChangeListener(event -> setValue(
                            (Boolean) event.getProperty().getValue()));
        }
    }

    public void validate() {
        if (isRequired() && !getValue()) {
            throw new InvalidValueException(
                    "Required CheckBox should be checked");
        }
    }

}
