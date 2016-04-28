package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.abstractfield.AbstractTextFieldTest;
import com.vaadin.ui.TextField;

public class TextFieldTest extends AbstractTextFieldTest<TextField> implements
        TextChangeListener {

    @Override
    protected Class<TextField> getTestClass() {
        return TextField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
    }

}
