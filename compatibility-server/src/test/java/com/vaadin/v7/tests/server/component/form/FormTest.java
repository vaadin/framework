package com.vaadin.v7.tests.server.component.form;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.ui.Form;
import com.vaadin.v7.ui.TextField;

/**
 * Test for {@link Form}.
 *
 * @author Vaadin Ltd
 */
public class FormTest {

    @Test
    public void testFocus() {
        Form form = new Form();
        final boolean firstFieldIsFocused[] = new boolean[1];
        TextField field1 = new TextField() {
            @Override
            public boolean isConnectorEnabled() {
                return false;
            }

            @Override
            public void focus() {
                firstFieldIsFocused[0] = true;
            }
        };

        final boolean secondFieldIsFocused[] = new boolean[1];
        TextField field2 = new TextField() {
            @Override
            public boolean isConnectorEnabled() {
                return true;
            }

            @Override
            public void focus() {
                secondFieldIsFocused[0] = true;
            }
        };
        form.addField("a", field1);
        form.addField("b", field2);
        form.focus();

        assertTrue("Field with enabled connector is not focused",
                secondFieldIsFocused[0]);
        assertFalse("Field with disabled connector is focused",
                firstFieldIsFocused[0]);
    }
}
