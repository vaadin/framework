package com.vaadin.tests.components.passwordfield;

import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.abstractfield.LegacyAbstractTextFieldTest;
import com.vaadin.v7.ui.PasswordField;

public class PasswordFieldTest
        extends LegacyAbstractTextFieldTest<PasswordField>
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
