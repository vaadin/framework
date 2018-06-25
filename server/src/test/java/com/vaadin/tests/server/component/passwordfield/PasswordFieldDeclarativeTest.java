package com.vaadin.tests.server.component.passwordfield;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.PasswordField;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class PasswordFieldDeclarativeTest
        extends DeclarativeTestBase<PasswordField> {

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin-password-field readonly value=\"test value\"/>";
        PasswordField tf = new PasswordField();
        tf.setValue("test value");
        tf.setReadOnly(true);
        testRead(design, tf);
        testWrite(design, tf);
    }
}
