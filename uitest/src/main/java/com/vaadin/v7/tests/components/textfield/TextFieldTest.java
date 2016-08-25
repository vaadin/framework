package com.vaadin.v7.tests.components.textfield;

import com.vaadin.v7.event.FieldEvents.TextChangeListener;
import com.vaadin.v7.ui.TextField;

public class TextFieldTest extends AbstractTextFieldTest<TextField>
        implements TextChangeListener {

    @Override
    protected Class<TextField> getTestClass() {
        return TextField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
    }

}
