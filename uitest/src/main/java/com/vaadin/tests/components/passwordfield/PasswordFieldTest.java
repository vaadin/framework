package com.vaadin.tests.components.passwordfield;

import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.abstractfield.LegacyAbstractTextFieldTest;
import com.vaadin.v7.ui.LegacyPasswordField;

public class PasswordFieldTest
        extends LegacyAbstractTextFieldTest<LegacyPasswordField>
        implements TextChangeListener {

    @Override
    protected Class<LegacyPasswordField> getTestClass() {
        return LegacyPasswordField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
    }

}
