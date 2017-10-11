package com.vaadin.tests.components;

import com.vaadin.testbench.elements.AbstractTextFieldElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;

import static org.junit.Assert.*;

/**
 * @author Vaadin Ltd
 */
public class BinderFormValidateTest extends SingleBrowserTest {

    @Test
    public void checkErrorAfterEditingOneFieldInBinder() {
        openTestURL();

        TextFieldElement username = $(TextFieldElement.class).id("username");
        PasswordFieldElement password = $(PasswordFieldElement.class).id("password");

        username.waitForVaadin();
        password.waitForVaadin();

        // initial - no errors
        assertTrue(hasNoError(username));
        assertTrue(hasNoError(password));

        // adding text into username
        username.focus();
        username.sendKeys("t", "e", "s", "t");
        username.sendKeys(Keys.TAB);

        username.waitForVaadin();
        password.waitForVaadin();

        // there should be no error.. because username is valid...
        assertTrue(hasNoError(username));
        assertTrue(hasNoError(password));

        // refocus username and remove value
        username.focus();
        username.clear();

        username.waitForVaadin();
        password.waitForVaadin();

        // there should be an error in the username field.
        assertTrue(hasError(username));
        assertTrue(hasNoError(password));
    }

    private static boolean hasError(AbstractTextFieldElement field) {
        return field.getAttribute("class").contains("v-textfield-error");
    }

    private static boolean hasNoError(AbstractTextFieldElement field) {
        return !hasError(field);
    }
}