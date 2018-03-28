package com.vaadin.tests.components.textfield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.TextField;

public class EnumTextField extends AbstractTestUIWithLog {

    public enum MyEnum {
        FIRST_VALUE, VALUE, THE_LAST_VALUE;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.addValueChangeListener(event -> {
            if (tf.isValid()) {
                log(tf.getValue() + " (valid)");
            } else {
                log(tf.getValue() + " (INVALID)");
            }
        });

        tf.setPropertyDataSource(new ObjectProperty<Enum>(MyEnum.FIRST_VALUE));
        addComponent(tf);
    }

}
