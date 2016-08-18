package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.abstractfield.LegacyAbstractTextFieldTest;
import com.vaadin.v7.ui.TextField;

public class TextFieldTest extends LegacyAbstractTextFieldTest<TextField>
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
