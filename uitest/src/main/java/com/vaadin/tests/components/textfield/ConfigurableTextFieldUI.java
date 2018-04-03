package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.TextField;

public class ConfigurableTextFieldUI
        extends AbstractFieldTest<TextField, String> {

    @Override
    protected Class<TextField> getTestClass() {
        return TextField.class;
    }

}
