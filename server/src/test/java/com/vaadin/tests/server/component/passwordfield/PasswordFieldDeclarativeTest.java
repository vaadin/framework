package com.vaadin.tests.server.component.passwordfield;

import com.vaadin.tests.server.component.textfield.TextFieldDeclarativeTest;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/**
 * Declarative test for PasswordField. Provides only information about
 * ColorPickerArea class. All tests are in the superclass.
 *
 * @author Vaadin Ltd
 *
 */
public class PasswordFieldDeclarativeTest extends TextFieldDeclarativeTest {

    @Override
    protected Class<? extends TextField> getComponentClass() {
        return PasswordField.class;
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-password-field";
    }
}
