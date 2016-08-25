package com.vaadin.v7.tests.components.passwordfield;

import com.vaadin.v7.event.FieldEvents.TextChangeListener;
import com.vaadin.v7.tests.components.textfield.AbstractTextFieldTest;
import com.vaadin.v7.ui.PasswordField;

public class PasswordFieldTest
        extends AbstractTextFieldTest<PasswordField>
        implements TextChangeListener {

    @Override
    protected Class<PasswordField> getTestClass() {
        return PasswordField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
    }

}
