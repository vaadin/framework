package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.abstractfield.AbstractTextFieldTest;
import com.vaadin.ui.TextField;

public class TextFieldsGeneric extends AbstractTextFieldTest<TextField> {

    @Override
    protected Class<TextField> getTestClass() {
        return TextField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

    }

}
