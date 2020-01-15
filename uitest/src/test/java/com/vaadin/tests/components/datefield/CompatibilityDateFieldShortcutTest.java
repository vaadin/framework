package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CompatibilityDateFieldShortcutTest extends SingleBrowserTest {

    private static final String DATEFIELD_VALUE_ORIGINAL = "11/01/2018";
    private static final String DATEFIELD_VALUE_MODIFIED = "21/01/2018";

    @Test
    public void modifyValueAndPressEnter() {
        openTestURL();

        DateFieldElement dateField = $(DateFieldElement.class).first();
        WebElement dateFieldText = dateField.findElement(By.tagName("input"));

        assertEquals("DateField value should be \"" + DATEFIELD_VALUE_ORIGINAL
                + "\"", DATEFIELD_VALUE_ORIGINAL, dateField.getValue());

        dateFieldText.click();
        dateFieldText.sendKeys(Keys.HOME, Keys.DELETE, "2");
        dateFieldText.sendKeys(Keys.ENTER);

        assertEquals("DateField value should be \"" + DATEFIELD_VALUE_MODIFIED
                + "\"", DATEFIELD_VALUE_MODIFIED, dateField.getValue());

        assertEquals(DATEFIELD_VALUE_MODIFIED,
                $(NotificationElement.class).first().getCaption());
    }
}
