package com.vaadin.tests.components;

import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Vaadin Ltd
 */
public class BinderFormValidateTest  extends SingleBrowserTest {

    @Test
    public void checkErrorAfterEditingOneFieldInBinder() {
        openTestURL();

        TextFieldElement username = $(TextFieldElement.class).id("username");
        PasswordFieldElement password = $(PasswordFieldElement.class).id("password");

        username.waitForVaadin();
        // initial - no errors
        assertTrue("Username should have no error",
                !username.getHTML().contains("aria-invalid=\"true\""));

        assertTrue("Password should have no error",
                !password.getHTML().contains("aria-invalid=\"true\""));

        // adding text into username
        username.setValue("test");

        username.waitForVaadin();
        // there should be no error.. because username is valid...
        assertTrue("Username should have no error",
                !username.getHTML().contains("aria-invalid=\"true\""));

        assertTrue("Password should have no error",
                !password.getHTML().contains("aria-invalid=\"true\""));

        // refocus username and remove value
        username.focus();
        username.clear();

        username.waitForVaadin();
        // there should be an error in the username field.
        assertTrue("Username should have an error",
                username.getHTML().contains("aria-invalid=\"true\""));

        assertTrue("Password should have no error",
                !password.getHTML().contains("aria-invalid=\"true\""));
    }
}