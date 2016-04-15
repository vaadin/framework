package com.vaadin.tests.components.passwordfield;

import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.abstractfield.AbstractTextFieldTest;
import com.vaadin.ui.PasswordField;

public class PasswordFieldTest extends AbstractTextFieldTest<PasswordField>
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
