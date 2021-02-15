package com.vaadin.tests.components.textfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CompatibilityTextFieldShortcutTest extends SingleBrowserTest {

    private static final String TEXTFIELD_VALUE = "input";
    private static final String NOTIFICATION = "Received F8: "+TEXTFIELD_VALUE;

    @Test
    public void focusAndPressF8() {
        openTestURL();

        TextFieldElement textField = $(TextFieldElement.class).first();
        textField.focus();
        textField.setValue(TEXTFIELD_VALUE);

        WebElement textFieldText = textField.findElement(By.tagName("input"));

        textFieldText.sendKeys(Keys.F8);

        assertEquals(NOTIFICATION,
                $(NotificationElement.class).first().getCaption());
    }
}
